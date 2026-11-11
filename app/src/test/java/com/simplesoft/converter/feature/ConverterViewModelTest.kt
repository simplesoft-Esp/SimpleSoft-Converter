package com.simplesoft.converter.feature

import androidx.lifecycle.viewModelScope
import com.simplesoft.converter.data.*
import com.simplesoft.converter.domain.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.test.*
import org.junit.*
import org.junit.Assert.*

private class FakeRepository: ConverterRepository {
 override val favorites=MutableStateFlow<List<Favorite>>(emptyList())
 override val usage=MutableStateFlow<List<Usage>>(emptyList())
 override val preferences=MutableStateFlow(Preferences())
 override suspend fun select(pair: PairKey) {preferences.value=preferences.value.copy(last=pair.id,family=pair.category.family,categoryPairs=preferences.value.categoryPairs+(pair.category to pair.id))}
 override suspend fun family(value: Family) {preferences.value=preferences.value.copy(family=value)}
 override suspend fun reduceMotion(value: Boolean) {preferences.value=preferences.value.copy(reduceMotion=value)}
 override suspend fun theme(value: String) {preferences.value=preferences.value.copy(theme=value)}
 override suspend fun learning(value: Boolean) {preferences.value=preferences.value.copy(learning=value)}
 override suspend fun resetPreferences() {preferences.value=Preferences()}
 override suspend fun toggle(pair: PairKey) {
  val ids=favorites.value.map {it.pair}.toMutableList();if(!ids.remove(pair.id))ids.add(pair.id)
  favorites.value=ids.mapIndexed {i,p->Favorite(p,i)}
 }
 override suspend fun move(id: String,delta: Int) {
  val list=favorites.value.map {it.pair}.toMutableList();val i=list.indexOf(id);val j=i+delta
  if(i>=0 && j in list.indices) {val tmp=list[i];list[i]=list[j];list[j]=tmp;favorites.value=list.mapIndexed {n,p->Favorite(p,n)}}
 }
 override suspend fun record(pair: PairKey,now: Long) {
  val old=usage.value.firstOrNull {it.pair==pair.id}
  usage.value=(usage.value.filter {it.pair!=pair.id}+Usage(pair.id,(old?.count?:0)+1,UsageRanking.decayed(old?.score?:0.0,old?.last?:now,now)+1,now)).sortedByDescending {it.last}
 }
 override suspend fun clearUsage() {usage.value=emptyList()}
 override suspend fun clearFavorites() {favorites.value=emptyList()}
}
@OptIn(ExperimentalCoroutinesApi::class)
class ConverterViewModelTest {
 private val dispatcher=StandardTestDispatcher()
 @Before fun setup() {Dispatchers.setMain(dispatcher)}
 @After fun teardown() {Dispatchers.resetMain()}
 @Test fun operationCountsOnceAcrossSeveralTriggers()=runTest(dispatcher) {
  val repo=FakeRepository();val vm=ConverterViewModel(repo);advanceUntilIdle()
  vm.edit("1");vm.edit("12");vm.edit("123");vm.confirm();vm.confirm();vm.foreground();advanceUntilIdle()
  assertEquals(1L,repo.usage.value.single().count)
  vm.select(PairKey("ft","m"));vm.confirm();advanceUntilIdle();assertEquals(1,repo.usage.value.size)
  vm.viewModelScope.cancel()
 }
 @Test fun exactSwapAndThemePreference()=runTest(dispatcher) {
  val repo=FakeRepository();val vm=ConverterViewModel(repo);advanceUntilIdle();vm.edit("1")
  repeat(20) {vm.swap()};vm.theme("dark");vm.reduceMotion(true);advanceUntilIdle()
  assertEquals(0,vm.state.value.result!!.compareTo(java.math.BigDecimal("3.280839895013123359580052493438320")))
  assertEquals("dark",vm.state.value.theme);assertTrue(vm.state.value.reduceMotion)
  vm.viewModelScope.cancel()
 }
 @Test fun favoritesOrderedAndExcludedFromFrequent()=runTest(dispatcher) {
  val repo=FakeRepository();val vm=ConverterViewModel(repo);advanceUntilIdle();vm.edit("1");vm.confirm();vm.toggle();advanceUntilIdle()
  vm.select(PairKey("kg","lb"));vm.toggle();advanceUntilIdle();vm.move("kg>lb",-1);advanceUntilIdle()
  assertEquals(listOf("kg>lb","m>ft"),vm.state.value.favorites.map {it.pair});assertTrue(vm.state.value.frequent.isEmpty())
  vm.clearUsage();advanceUntilIdle();assertEquals(2,vm.state.value.favorites.size)
  vm.viewModelScope.cancel()
 }
 @Test fun reopensPreferencesWithoutRestoringNumericInput()=runTest(dispatcher) {
  val repo=FakeRepository();val first=ConverterViewModel(repo);advanceUntilIdle()
  first.select(PairKey("bar","psi"));first.edit("2,5");first.confirm();first.family(Family.TECHNICAL);advanceUntilIdle();first.viewModelScope.cancel()
  val second=ConverterViewModel(repo);advanceUntilIdle()
  assertEquals("bar>psi",second.state.value.pair.id);assertEquals(Family.TECHNICAL,second.state.value.family);assertEquals("",second.state.value.text)
  second.family(Family.PHYSICAL);second.category(Category.PRESSURE);advanceUntilIdle();assertEquals("bar>psi",second.state.value.pair.id)
  second.learning(false);advanceUntilIdle();second.edit("4");second.confirm();advanceUntilIdle();assertEquals(1L,repo.usage.value.single().count)
  second.viewModelScope.cancel()
 }
}
