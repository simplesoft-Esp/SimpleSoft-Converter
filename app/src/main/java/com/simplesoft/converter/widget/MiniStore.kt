package com.simplesoft.converter.widget
import android.content.Context
class MiniStore(context: Context) {
 private val prefs=context.applicationContext.getSharedPreferences("converter_mini",Context.MODE_PRIVATE)
 fun load(id: Int)=MiniCodec.decode(prefs.getString("widget_$id",null))
 fun save(id: Int,state: MiniState)=prefs.edit().putString("widget_$id",MiniCodec.encode(state)).commit()
 fun remove(id: Int) {prefs.edit().remove("widget_$id").commit()}
 fun remap(old: Int,new: Int) {
  if(old==new) return
  val raw=prefs.getString("widget_$old",null)?:return
  prefs.edit().putString("widget_$new",raw).remove("widget_$old").commit()
 }
}
