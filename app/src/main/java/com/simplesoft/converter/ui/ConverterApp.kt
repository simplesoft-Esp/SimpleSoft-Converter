package com.simplesoft.converter.ui

import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalConfiguration
import com.simplesoft.converter.localization.*
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.*
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.simplesoft.converter.R
import com.simplesoft.core.*
import com.simplesoft.converter.domain.*
import com.simplesoft.converter.feature.ConverterViewModel

private val Light=lightColorScheme(primaryContainer=Color(0xFFEAEAEA),onPrimaryContainer=Color(0xFF171717),secondaryContainer=Color(0xFFEAEAEA),onSecondaryContainer=Color(0xFF171717),surfaceContainer=Color(0xFFF2F2F2),surfaceContainerHigh=Color(0xFFEDEDED),surfaceContainerHighest=Color(0xFFE7E7E7),surfaceContainerLow=Color(0xFFF7F7F7),surfaceContainerLowest=Color.White,surfaceTint=Color(0xFF171717),primary=Color(0xFF171717),onPrimary=Color.White,background=Color(0xFFFAFAFA),onBackground=Color(0xFF171717),surface=Color.White,onSurface=Color(0xFF171717),surfaceVariant=Color(0xFFF2F2F2),onSurfaceVariant=Color(0xFF595959),outline=Color(0xFFD6D6D6),secondary=Color(0xFF595959))
private val Dark=darkColorScheme(primaryContainer=Color(0xFF333333),onPrimaryContainer=Color(0xFFF5F5F5),secondaryContainer=Color(0xFF333333),onSecondaryContainer=Color(0xFFF5F5F5),surfaceContainer=Color(0xFF222222),surfaceContainerHigh=Color(0xFF292929),surfaceContainerHighest=Color(0xFF333333),surfaceContainerLow=Color(0xFF1A1A1A),surfaceContainerLowest=Color(0xFF101010),surfaceTint=Color(0xFFF5F5F5),primary=Color(0xFFF5F5F5),onPrimary=Color(0xFF171717),background=Color(0xFF121212),onBackground=Color(0xFFF5F5F5),surface=Color(0xFF1E1E1E),onSurface=Color(0xFFF5F5F5),surfaceVariant=Color(0xFF292929),onSurfaceVariant=Color(0xFFB8B8B8),outline=Color(0xFF454545),secondary=Color(0xFFB8B8B8))

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable fun ConverterApp(vm: ConverterViewModel, activity: ComponentActivity) {
 val context=LocalContext.current
 val config=LocalConfiguration.current
 val language=config.locales[0].language
 val catalog=remember(context,language) {searchCatalog(context)}
 var query by rememberSaveable {mutableStateOf("")}
 val matches=remember(query,catalog) {UnitSearch.search(query,catalog)}
 val s by vm.state.collectAsStateWithLifecycle()
 val brand=rememberBrandMessages()
 val dark=when(s.theme) {"dark"->true;"light"->false;else->isSystemInDarkTheme()}
 SideEffect {
  val bars=if(dark) SystemBarStyle.dark(android.graphics.Color.TRANSPARENT) else SystemBarStyle.light(android.graphics.Color.TRANSPARENT,android.graphics.Color.TRANSPARENT)
  activity.enableEdgeToEdge(statusBarStyle=bars,navigationBarStyle=bars)
 }
 val lifecycle=LocalLifecycleOwner.current
 DisposableEffect(lifecycle,activity) {
  val observer=LifecycleEventObserver { _,event ->
   if(event==Lifecycle.Event.ON_STOP && !activity.isChangingConfigurations) vm.confirm()
   if(event==Lifecycle.Event.ON_START) vm.foreground()
  }
  lifecycle.lifecycle.addObserver(observer)
  onDispose {lifecycle.lifecycle.removeObserver(observer)}
 }
 val imeVisible=WindowInsets.isImeVisible
 var previouslyVisible by remember {mutableStateOf(false)}
 LaunchedEffect(imeVisible) {if(previouslyVisible && !imeVisible) vm.confirm();previouslyVisible=imeVisible}
 MaterialTheme(colorScheme=if(dark) Dark else Light) {
  var settings by rememberSaveable {mutableStateOf(false)}
  var chooser by rememberSaveable {mutableStateOf<String?>(null)}
  var favoriteEditor by rememberSaveable {mutableStateOf(false)}
  var allFavorites by rememberSaveable {mutableStateOf(false)}
  var recent by rememberSaveable {mutableStateOf(false)}
  var deletion by rememberSaveable {mutableStateOf<String?>(null)}
  val clipboard=LocalClipboardManager.current
  val keyboard=LocalSoftwareKeyboardController.current
  var copied by remember {mutableStateOf(false)}
  Scaffold(containerColor=MaterialTheme.colorScheme.background,bottomBar={
   if(!imeVisible && s.ready) SimpleSoftFooter(brand.messages,s.reduceMotion,Modifier.navigationBarsPadding())
  }) { padding ->
   LazyColumn(Modifier.fillMaxSize().padding(padding).imePadding(),contentPadding=PaddingValues(20.dp),verticalArrangement=Arrangement.spacedBy(16.dp)) {
    item {
     Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.SpaceBetween,verticalAlignment=Alignment.CenterVertically) {
      Column(Modifier.weight(1f)) { Text(context.getString(R.string.brand),fontSize=12.sp,color=MaterialTheme.colorScheme.onSurfaceVariant);Text(context.getString(R.string.converter),style=MaterialTheme.typography.headlineMedium) }
      TextButton(onClick={settings=true}) {Text(context.getString(R.string.settings))}
     }
    }
    item {OutlinedTextField(value=query,enabled=s.ready,onValueChange={query=it},label={Text(context.getString(R.string.search_hint))},singleLine=true,modifier=Modifier.fillMaxWidth(),trailingIcon={if(query.isNotEmpty()) TextButton(onClick={query=""}) {Text(context.getString(R.string.clear_search))}})}
    if(s.storageError!=null) item {Text(context.getString(s.storageError!!.resource()),color=MaterialTheme.colorScheme.error,modifier=Modifier.semantics {liveRegion=LiveRegionMode.Polite})}
    if(query.isNotBlank()) {
     item {Text(context.getString(R.string.search_results),style=MaterialTheme.typography.titleMedium)}
     if(matches.isEmpty()) item {Text(context.getString(R.string.no_results))}
     matches.groupBy {it.category}.forEach { (category,units) ->
      item(key="category_${category.name}") {TextButton(onClick={vm.category(category);query="";keyboard?.hide()}) {Text(context.getString(category.nameRes))}}
      items(units,key={"search_${it.id}"}) {unit ->
       OutlinedButton(onClick={vm.chooseOrigin(unit.id);query="";keyboard?.hide();copied=false},modifier=Modifier.fillMaxWidth()) {Text("${unit.name} · ${unit.symbol}")}
      }
     }
    } else {
    item {
     ScrollableTabRow(selectedTabIndex=s.family.ordinal,edgePadding=0.dp,containerColor=MaterialTheme.colorScheme.background) {
      Family.entries.forEach {family -> Tab(selected=s.family==family,onClick={keyboard?.hide();vm.family(family)},text={Text(context.getString(family.nameRes))}) }
     }
    }
    if(CategoryFamilies.available(s.family).isNotEmpty()) {
    item {OutlinedButton(enabled=s.ready,onClick={keyboard?.hide();chooser="category"}) {Text("${context.getString(s.pair.category.nameRes)}  ▾")}}
    item {
     OutlinedCard(Modifier.fillMaxWidth()) {
      Column(Modifier.padding(18.dp),verticalArrangement=Arrangement.spacedBy(12.dp)) {
       Text(context.getString(R.string.from),color=MaterialTheme.colorScheme.onSurfaceVariant)
       UnitButton(s.pair.from,s.ready) {chooser="from";keyboard?.hide()}
       OutlinedTextField(value=s.text,onValueChange={vm.edit(it);copied=false},modifier=Modifier.fillMaxWidth(),enabled=s.ready,label={Text(context.getString(R.string.amount))},singleLine=true,isError=s.error!=null,
        textStyle=MaterialTheme.typography.headlineMedium,
        keyboardOptions=KeyboardOptions(keyboardType=KeyboardType.Decimal,imeAction=ImeAction.Done),
        keyboardActions=KeyboardActions(onDone={vm.confirm();keyboard?.hide()}))
       s.error?.let {Text(context.getString(it.resource()),color=MaterialTheme.colorScheme.error,modifier=Modifier.semantics {liveRegion=LiveRegionMode.Polite})}
       FilledTonalButton(onClick={vm.swap(language);copied=false},enabled=s.result!=null,modifier=Modifier.align(Alignment.CenterHorizontally).semantics {contentDescription=context.getString(R.string.swap_description)}) {Text(context.getString(R.string.swap))}
       Text(context.getString(R.string.to),color=MaterialTheme.colorScheme.onSurfaceVariant)
       UnitButton(s.pair.to,s.ready) {chooser="to";keyboard?.hide()}
       val display=s.result?.let {Engine.display(it,language)}
       SelectionContainer {Text((if(display?.approximate==true) "≈ " else "")+(display?.text?:"—"),fontSize=32.sp,modifier=Modifier.fillMaxWidth().semantics {liveRegion=LiveRegionMode.Polite})}
       FlowRow(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.spacedBy(8.dp)) {
        OutlinedButton(enabled=s.result!=null,onClick={s.result?.let {clipboard.setText(AnnotatedString(Engine.display(it,language).text+" "+Catalog.unit(s.pair.to).symbol));vm.confirm();copied=true}}) {Text(if(copied) context.getString(R.string.copied) else context.getString(R.string.copy))}
        val pinned=s.favorites.any {it.pair==s.pair.id}
        OutlinedButton(enabled=s.ready,onClick=vm::toggle,modifier=Modifier.semantics {stateDescription=if(pinned) context.getString(R.string.pinned) else context.getString(R.string.not_pinned)}) {Text(if(pinned) context.getString(R.string.pinned_star) else context.getString(R.string.pin))}
       }
      }
     }
    }
    } else {
     item {
      Column(verticalArrangement=Arrangement.spacedBy(12.dp)) {
       Text(context.getString(R.string.no_family),style=MaterialTheme.typography.titleMedium)
       Text(context.getString(R.string.planned,CategoryFamilies.planned.filter {it.family==s.family}.joinToString {context.getString(it.nameRes)}))
       Text(context.getString(R.string.future_info),color=MaterialTheme.colorScheme.onSurfaceVariant)
      }
     }
    }
    }
    item {Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.SpaceBetween,verticalAlignment=Alignment.CenterVertically) {Text(context.getString(R.string.favorites),style=MaterialTheme.typography.titleMedium);TextButton(onClick={favoriteEditor=true},enabled=s.favorites.isNotEmpty()){Text(context.getString(R.string.edit))}}}
    if(s.favorites.isEmpty()) item {Text(context.getString(R.string.empty_favorites),color=MaterialTheme.colorScheme.onSurfaceVariant)}
    items(s.favorites.take(if(allFavorites) Int.MAX_VALUE else 3),key={"f${it.pair}"}) {f ->PairKey.decode(f.pair)?.let {p->PairButton(p,context.getString(R.string.manual_pin)) {vm.select(p);query="";keyboard?.hide();copied=false}}}
    if(s.favorites.size>3) item {TextButton(onClick={allFavorites=!allFavorites}) {Text(if(allFavorites) context.getString(R.string.see_less) else context.getString(R.string.see_all,s.favorites.size))}}
    if(s.learning) {
     item {Text(context.getString(R.string.frequent),style=MaterialTheme.typography.titleMedium)}
     if(s.frequent.isEmpty()) item {Text(context.getString(R.string.empty_frequent),color=MaterialTheme.colorScheme.onSurfaceVariant)}
     items(s.frequent.filter {u->s.favorites.none {it.pair==u.pair}},key={"u${it.pair}"}) {u->PairKey.decode(u.pair)?.let {p->PairButton(p,context.getString(R.string.usage_suggestion)) {vm.select(p);query="";keyboard?.hide();copied=false}}}
    }
    item {Text(if(s.ready) context.getString(R.string.offline_note) else context.getString(R.string.loading),fontSize=12.sp,color=MaterialTheme.colorScheme.onSurfaceVariant)}
   }
  }
  if(chooser!=null) ModalBottomSheet(onDismissRequest={chooser=null}) {
   val mode=chooser
   LazyColumn(Modifier.fillMaxWidth().heightIn(max=480.dp),contentPadding=PaddingValues(20.dp)) {
    item {Text(if(mode=="category") context.getString(R.string.category) else context.getString(R.string.choose_unit),style=MaterialTheme.typography.titleLarge)}
    if(mode=="category") items(CategoryFamilies.available(s.family)) {c ->TextButton(onClick={vm.category(c);chooser=null;copied=false},modifier=Modifier.fillMaxWidth()) {Text(context.getString(c.nameRes))}}
    else items(Catalog.units(s.pair.category)) {u ->TextButton(onClick={vm.select(if(mode=="from") s.pair.copy(from=u.id) else s.pair.copy(to=u.id));chooser=null;copied=false},modifier=Modifier.fillMaxWidth()) {Text("${context.getString(u.nameRes)} · ${u.symbol}")}}
   }
  }
  if(settings) AlertDialog(onDismissRequest={settings=false},confirmButton={TextButton(onClick={settings=false}) {Text(context.getString(R.string.close))}},title={Text(context.getString(R.string.settings))},text={Column(Modifier.verticalScroll(rememberScrollState()),verticalArrangement=Arrangement.spacedBy(8.dp)) {
   Text(context.getString(R.string.appearance))
   for((id,label) in listOf("system" to context.getString(R.string.system),"light" to context.getString(R.string.light),"dark" to context.getString(R.string.dark))) Row(verticalAlignment=Alignment.CenterVertically) {RadioButton(selected=s.theme==id,onClick={vm.theme(id)});TextButton(onClick={vm.theme(id)}){Text(label)}}
   Row(verticalAlignment=Alignment.CenterVertically) {Text(context.getString(R.string.learn_usage),Modifier.weight(1f));Switch(checked=s.learning,onCheckedChange=vm::learning,enabled=s.ready)}
   Row(verticalAlignment=Alignment.CenterVertically) {Text(context.getString(R.string.reduce_motion),Modifier.weight(1f));Switch(checked=s.reduceMotion,onCheckedChange=vm::reduceMotion,enabled=s.ready)}
   Text(context.getString(R.string.motion_info))
   Text(context.getString(R.string.privacy_info))
   TextButton(onClick={recent=true}) {Text(context.getString(R.string.recent))}
   TextButton(enabled=s.ready,onClick={deletion="usage"}) {Text(context.getString(R.string.clear_usage))}
   TextButton(enabled=s.ready,onClick={deletion="favorites"}) {Text(context.getString(R.string.clear_favorites))}
   TextButton(enabled=s.ready,onClick={deletion="preferences"}) {Text(context.getString(R.string.reset_preferences))}
   Image(painterResource(R.drawable.simplesoft_logo),contentDescription=context.getString(R.string.logo_description),modifier=Modifier.size(100.dp).background(Color.White))
   Text(context.getString(R.string.about),fontSize=12.sp)
  }})
  if(favoriteEditor) AlertDialog(onDismissRequest={favoriteEditor=false},confirmButton={TextButton(onClick={favoriteEditor=false}) {Text(context.getString(R.string.done))}},title={Text(context.getString(R.string.favorite_order))},text={Column(Modifier.verticalScroll(rememberScrollState())) {
   s.favorites.forEachIndexed {i,f -> val p=PairKey.decode(f.pair)
    Column {Text(p?.let {"${context.getString(it.category.nameRes)}: ${it.label}"}?:f.pair)
     Row {TextButton(enabled=i>0,onClick={vm.move(f.pair,-1)}) {Text(context.getString(R.string.move_up))};TextButton(enabled=i<s.favorites.lastIndex,onClick={vm.move(f.pair,1)}) {Text(context.getString(R.string.move_down))}}
    }
   }
  }})
  if(recent) AlertDialog(onDismissRequest={recent=false},confirmButton={TextButton(onClick={recent=false}){Text(context.getString(R.string.close))}},title={Text(context.getString(R.string.recent_pairs))},text={Column(Modifier.verticalScroll(rememberScrollState())) {
   if(s.recent.isEmpty()) Text(context.getString(R.string.no_recent))
   s.recent.forEach {u->PairKey.decode(u.pair)?.let {p->TextButton(onClick={vm.select(p);query="";recent=false;settings=false}) {Text("${context.getString(p.category.nameRes)}: ${p.label}")}}}
  }})
  if(deletion!=null) AlertDialog(onDismissRequest={deletion=null},title={Text(context.getString(R.string.confirm_delete))},text={Text(when(deletion){"usage"->context.getString(R.string.delete_usage_info);"favorites"->context.getString(R.string.delete_favorites_info);else->context.getString(R.string.reset_info)})},dismissButton={TextButton(onClick={deletion=null}){Text(context.getString(R.string.cancel))}},confirmButton={TextButton(onClick={when(deletion){"usage"->vm.clearUsage();"favorites"->vm.clearFavorites();else->vm.resetPreferences()};deletion=null}){Text(context.getString(R.string.confirm))}})
 }
}
@Composable private fun UnitButton(id: String,enabled: Boolean,onClick: ()->Unit) {val context=LocalContext.current;OutlinedButton(onClick=onClick,enabled=enabled,modifier=Modifier.fillMaxWidth()) {val u=Catalog.unit(id);Text("${context.getString(u.nameRes)} · ${u.symbol}  ▾")}}
@Composable private fun PairButton(p: PairKey,subtitle: String,onClick: ()->Unit) {val context=LocalContext.current;OutlinedButton(onClick=onClick,modifier=Modifier.fillMaxWidth()) {Column(Modifier.fillMaxWidth().padding(vertical=4.dp)) {Text("${context.getString(p.category.nameRes)} · ${p.label}");Text(subtitle,fontSize=12.sp,color=MaterialTheme.colorScheme.onSurfaceVariant)}}}
