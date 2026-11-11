package com.simplesoft.converter.feature

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.simplesoft.converter.data.*
import com.simplesoft.converter.domain.*
import java.math.BigDecimal
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*

data class ConverterState(
 val ready: Boolean=false, val pair: PairKey=Catalog.default(Category.LENGTH), val text: String="",
 val result: BigDecimal?=null, val error: InputError?=null, val theme: String="system", val learning: Boolean=true,
 val favorites: List<Favorite> = emptyList(), val frequent: List<Usage> = emptyList(), val recent: List<Usage> = emptyList(),
 val family: Family=Family.PHYSICAL, val reduceMotion: Boolean=false, val storageError: StorageError?=null
)
class ConverterViewModel(private val repo: ConverterRepository): ViewModel() {
 private val mutable=MutableStateFlow(ConverterState())
 val state: StateFlow<ConverterState> = mutable.asStateFlow()
 private var exact: BigDecimal?=null
 private var base: BigDecimal?=null
 private var dirty=false
 private var preferences=Preferences()
 private var stats=emptyList<Usage>()
 private var favorites=emptyList<Favorite>()
 // One serial writer orders confirmation, selection and destructive actions.
 private val writes=Channel<suspend ()->Unit>(Channel.UNLIMITED)
 private fun write(action: suspend ()->Unit) { writes.trySend(action) }
 init {
  viewModelScope.launch { for(action in writes) try { action() } catch(e: Exception) {
   if(e is CancellationException) throw e
   mutable.update { it.copy(storageError=StorageError.WRITE) }
  } }
  viewModelScope.launch {
   try {
    combine(repo.preferences,repo.favorites,repo.usage) { p,f,u -> Triple(p,f,u) }.collect { (p,f,u) ->
     preferences=p;favorites=f;stats=u
     val first=!state.value.ready
     mutable.update { it.copy(ready=true,theme=p.theme,learning=p.learning,family=p.family,reduceMotion=p.reduceMotion,
      pair=if(first) PairKey.decode(p.last)?:Catalog.default(Category.LENGTH) else it.pair,
      favorites=f,recent=u.take(20)) }
     if(!dirty) refreshRanking()
    }
   } catch(e: Exception) { if(e is CancellationException) throw e
    mutable.update { it.copy(storageError=StorageError.READ) }
   }
  }
 }
 private fun refreshRanking() {
  val now=System.currentTimeMillis();val pinned=favorites.map { it.pair }.toSet()
  val ordered=stats.filter { it.pair !in pinned }.sortedWith(compareByDescending<Usage> { UsageRanking.decayed(it.score,it.last,now) }.thenByDescending { it.last }.thenBy { it.pair }).take(3)
  mutable.update { it.copy(frequent=if(it.learning) ordered else emptyList()) }
 }
 fun edit(text: String) {
  if(!state.value.ready) return
  exact=Engine.parse(text);base=null
  var error: InputError?=null
  if(exact!=null) try { base=Engine.toBase(exact!!,state.value.pair.from) } catch(e: IllegalArgumentException) { error=InputError.BELOW_ABSOLUTE_ZERO }
  else if(!Engine.partial(text)) error=if(text.length>120) InputError.OUT_OF_RANGE else InputError.INPUT_INVALID
  dirty=true
  mutable.update { it.copy(text=text,error=error,result=base?.let { b -> Engine.fromBase(b,it.pair.to) }) }
 }
 fun confirm() {
  if(!dirty) return
  dirty=false
  if(state.value.result!=null && state.value.learning) {
   val pair=state.value.pair;val now=System.currentTimeMillis();write { repo.record(pair,now) }
  }
  refreshRanking()
 }
 fun foreground() { if(!dirty) refreshRanking() }
 fun select(pair: PairKey) {
  if(!state.value.ready) return
  confirm()
  try { base=exact?.let { Engine.toBase(it,pair.from) }
   mutable.update { it.copy(pair=pair,family=pair.category.family,result=base?.let { b->Engine.fromBase(b,pair.to) },error=null) }
  } catch(e: IllegalArgumentException) {base=null;mutable.update { it.copy(pair=pair,family=pair.category.family,result=null,error=InputError.BELOW_ABSOLUTE_ZERO) } }
  preferences=preferences.copy(last=pair.id,family=pair.category.family,categoryPairs=preferences.categoryPairs+(pair.category to pair.id))
  write { repo.select(pair) }
 }
 fun chooseOrigin(id: String) {
  val unit=Catalog.unit(id)
  select(SearchSelection.forOrigin(id,preferences.categoryPairs[unit.category]))
 }
 fun category(category: Category) = select(PairKey.decode(preferences.categoryPairs[category]?:"")?:Catalog.default(category))
 fun swap(language: String="es") {
  val value=state.value.result?:return
  confirm()
  val old=state.value.pair;val pair=PairKey(old.to,old.from)
  exact=value
  // Retain canonical base across swaps. Never reparse the rounded presentation.
  mutable.update { it.copy(pair=pair,family=pair.category.family,text=Engine.display(value,language).text,result=base?.let { b->Engine.fromBase(b,pair.to) },error=null) }
  preferences=preferences.copy(last=pair.id,family=pair.category.family,categoryPairs=preferences.categoryPairs+(pair.category to pair.id))
  write { repo.select(pair) }
 }
 fun toggle() {val p=state.value.pair;write { repo.toggle(p) } }
 fun move(id: String, delta: Int) { write { repo.move(id,delta) } }
 fun family(value: Family) { confirm();mutable.update {it.copy(family=value)};write {repo.family(value)} }
 fun reduceMotion(value: Boolean) {write {repo.reduceMotion(value)} }
 fun theme(value: String) { write { repo.theme(value) } }
 fun learning(value: Boolean) { confirm(); write {repo.learning(value)} }
 fun clearUsage() {dirty=false;write {repo.clearUsage()} }
 fun clearFavorites() {write {repo.clearFavorites()} }
 fun resetPreferences() {confirm();write {repo.resetPreferences()};select(Catalog.default(Category.LENGTH)) }
}
