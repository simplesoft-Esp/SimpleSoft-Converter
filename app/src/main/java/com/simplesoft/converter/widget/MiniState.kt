package com.simplesoft.converter.widget

import com.simplesoft.converter.domain.*
import java.math.BigDecimal
import org.json.JSONObject

enum class Picker { MAIN, CATEGORY, FROM, TO }
data class MiniState(
 val pairId: String="m>ft",val input: String="",val precise: String?=null,val canonical: String?=null,
 val remembered: Map<String,String> = emptyMap(),val picker: Picker=Picker.MAIN,val page: Int=0
) {val pair: PairKey get()=PairKey.decode(pairId)?:Catalog.default(Category.LENGTH)}
data class MiniResult(val value: BigDecimal?=null,val error: InputError?=null)
object MiniReducer {
 const val PAGE_SIZE=5
 const val MIN_WIDTH=260
 const val MIN_HEIGHT=420
 fun fits(width: Int,height: Int)=width>=MIN_WIDTH && height>=MIN_HEIGHT
 fun inputValue(s: MiniState)=s.precise?.toBigDecimalOrNull()?:Engine.parse(s.input)
 private fun base(s: MiniState): BigDecimal?=s.canonical?.toBigDecimalOrNull()?:inputValue(s)?.let {Engine.toBase(it,s.pair.from)}
 fun result(s: MiniState): MiniResult = try {
  MiniResult(base(s)?.let {Engine.fromBase(it,s.pair.to)})
 } catch(_: IllegalArgumentException) {MiniResult(error=InputError.BELOW_ABSOLUTE_ZERO)}
 private fun select(s: MiniState,p: PairKey): MiniState {
  val v=inputValue(s)
  val b=runCatching {v?.let {Engine.toBase(it,p.from)}}.getOrNull()
  return s.copy(pairId=p.id,precise=v?.toPlainString(),canonical=b?.toPlainString(),remembered=s.remembered+(p.category.name to p.id),picker=Picker.MAIN,page=0)
 }
 fun apply(original: MiniState,command: String,plan: PickerPlan=PickerLayouts.plan(original.picker,count(original))): MiniState {
  val s=original.copy(page=plan.page(original.page,count(original)))
  when(command) {
   "menu-category"->return s.copy(picker=Picker.CATEGORY,page=0)
   "menu-from"->return s.copy(picker=Picker.FROM,page=0)
   "menu-to"->return s.copy(picker=Picker.TO,page=0)
   "back"->return s.copy(picker=Picker.MAIN,page=0)
   "next"->return s.copy(page=(s.page+1).coerceAtMost(lastPage(s,plan)))
   "previous"->return s.copy(page=(s.page-1).coerceAtLeast(0))
   "swap"->{
    val b=runCatching {base(s)}.getOrNull()?:return s
    val v=result(s).value?:return s
    val pair=PairKey(s.pair.to,s.pair.from)
    return s.copy(pairId=pair.id,input=v.stripTrailingZeros().toPlainString(),precise=v.toPlainString(),canonical=b.toPlainString(),remembered=s.remembered+(pair.category.name to pair.id))
   }
  }
  if(command.startsWith("category:")) {
   val c=Category.entries.firstOrNull {it.name==command.substringAfter(':')}?:return s
   return select(s,PairKey.decode(s.remembered[c.name]?:"")?.takeIf {it.category==c}?:Catalog.default(c))
  }
  if(command.startsWith("from:") || command.startsWith("to:")) {
   val unit=Catalog.all.firstOrNull {it.id==command.substringAfter(':')}?:return s
   if(unit.category!=s.pair.category) return s
   return select(s,if(command.startsWith("from:")) s.pair.copy(from=unit.id) else s.pair.copy(to=unit.id))
  }
  if(s.picker!=Picker.MAIN) return s
  val text=when(command) {
   "clear"->""
   "erase"->s.input.dropLast(1)
   "sign"->if(s.input.startsWith('-')) s.input.drop(1) else "-"+s.input
   "decimal"->when {'.' in s.input->s.input;s.input==""->"0.";s.input=="-"->"-0.";else->s.input+"."}
   else->if(command.length==1 && command[0] in '0'..'9') {
    if(s.input.length>=120) s.input else when(s.input) {"0"->command;"-0"->"-$command";else->s.input+command}
   } else return s
  }
  return s.copy(input=text,precise=null,canonical=null)
 }
 fun count(s: MiniState)=if(s.picker==Picker.CATEGORY) Category.entries.size else Catalog.units(s.pair.category).size
 fun lastPage(s: MiniState,plan: PickerPlan=PickerLayouts.plan(s.picker,count(s)))=plan.lastPage(count(s))
}
object MiniCodec {
 fun encode(s: MiniState): String = JSONObject().put("version",1).put("pair",s.pairId).put("input",s.input).put("precise",s.precise?:JSONObject.NULL).put("canonical",s.canonical?:JSONObject.NULL).put("remembered",JSONObject(s.remembered)).put("picker",s.picker.name).put("page",s.page).toString()
 fun decode(raw: String?): MiniState = runCatching {
  require(raw!=null && raw.length<=20000)
  val o=JSONObject(raw);require(o.getInt("version")==1)
  val p=PairKey.decode(o.getString("pair"))?:error("pair")
  val text=o.getString("input");require(text.length<=180 && (Engine.partial(text) || Engine.parse(text)!=null || text.toBigDecimalOrNull()!=null))
  fun number(key: String)=if(o.isNull(key)) null else o.getString(key).also {require(it.length<=200 && it.toBigDecimalOrNull()?.let {n->n.precision()<=180 && kotlin.math.abs(n.scale().toLong())<=200}==true)}
  val memories=mutableMapOf<String,String>();val obj=o.optJSONObject("remembered")
  Category.entries.forEach {c->PairKey.decode(obj?.optString(c.name)?:"")?.takeIf {it.category==c}?.let {memories[c.name]=it.id}}
  val picker=Picker.entries.firstOrNull {it.name==o.optString("picker")}?:Picker.MAIN
  val state=MiniState(p.id,text,number("precise"),number("canonical"),memories,picker,o.optInt("page",0).coerceAtLeast(0))
  state.copy(page=state.page.coerceAtMost((MiniReducer.count(state)-1).coerceAtLeast(0)))
 }.getOrDefault(MiniState())
}
