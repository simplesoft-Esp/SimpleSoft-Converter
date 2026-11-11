package com.simplesoft.converter.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.RemoteViews
import com.simplesoft.converter.R
import com.simplesoft.converter.domain.*
import com.simplesoft.converter.localization.*
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

class ConverterMiniProvider: AppWidgetProvider() {
 override fun onReceive(context: Context,intent: Intent) {
  val pending=goAsync()
  executor.execute {
   try {
    when(intent.action) {
     ACTION -> {
      val id=intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,AppWidgetManager.INVALID_APPWIDGET_ID)
      val manager=AppWidgetManager.getInstance(context)
      if(manager.getAppWidgetInfo(id)?.provider==ComponentName(context,ConverterMiniProvider::class.java)) {
       val store=MiniStore(context);val old=store.load(id)
       val state=MiniReducer.apply(old,intent.getStringExtra(COMMAND)?:"",pickerPlan(context,manager,id,old))
       val saved=store.save(id,state)
       render(context,manager,id,if(saved) state else old,!saved)
      }
     }
     Intent.ACTION_LOCALE_CHANGED,Intent.ACTION_CONFIGURATION_CHANGED -> refreshNow(context)
     else -> super.onReceive(context,intent)
    }
   } finally {pending.finish()}
  }
 }
 override fun onUpdate(context: Context,manager: AppWidgetManager,ids: IntArray) {ids.forEach {render(context,manager,it,MiniStore(context).load(it))}}
 override fun onAppWidgetOptionsChanged(context: Context,manager: AppWidgetManager,id: Int,options: Bundle) {render(context,manager,id,MiniStore(context).load(id))}
 override fun onDeleted(context: Context,ids: IntArray) {val store=MiniStore(context);ids.forEach(store::remove)}
 override fun onRestored(context: Context,oldWidgetIds: IntArray,newWidgetIds: IntArray) {
  val store=MiniStore(context)
  oldWidgetIds.zip(newWidgetIds).forEach {(old,new)->store.remap(old,new)}
  val manager=AppWidgetManager.getInstance(context)
  onUpdate(context,manager,newWidgetIds)
  newWidgetIds.forEach {manager.updateAppWidgetOptions(it,Bundle().apply {putBoolean(AppWidgetManager.OPTION_APPWIDGET_RESTORE_COMPLETED,true)})}
 }
 companion object {
  const val ACTION="com.simplesoft.converter.MINI_ACTION"
  private const val COMMAND="command"
  private val executor=ThreadPoolExecutor(0,1,15,TimeUnit.SECONDS,LinkedBlockingQueue<Runnable>())
  fun refreshAll(context: Context) {val app=context.applicationContext;executor.execute {refreshNow(app)}}
  private fun refreshNow(context: Context) {
   val manager=AppWidgetManager.getInstance(context)
   manager.getAppWidgetIds(ComponentName(context,ConverterMiniProvider::class.java)).forEach {render(context,manager,it,MiniStore(context).load(it))}
  }
  private fun pending(context: Context,id: Int,command: String): PendingIntent {
   val intent=Intent(context,ConverterMiniProvider::class.java).setAction(ACTION)
    .setData(Uri.Builder().scheme("simplesoft-mini").authority("action").appendPath(id.toString()).appendPath(command).build())
    .putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,id).putExtra(COMMAND,command)
   return PendingIntent.getBroadcast(context,0,intent,PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
  }
  private fun dimensions(context: Context,manager: AppWidgetManager,id: Int): Pair<Int,Int> {
   val options=manager.getAppWidgetOptions(id)
   val landscape=context.resources.configuration.orientation==Configuration.ORIENTATION_LANDSCAPE
   val width=options.getInt(if(landscape) AppWidgetManager.OPTION_APPWIDGET_MAX_WIDTH else AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH,280)
   val height=options.getInt(if(landscape) AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT else AppWidgetManager.OPTION_APPWIDGET_MAX_HEIGHT,440)
   return width to height
  }
  private fun pickerPlan(context: Context,manager: AppWidgetManager,id: Int,state: MiniState): PickerPlan {
   val (width,height)=dimensions(context,manager,id)
   return PickerLayouts.plan(state.picker,MiniReducer.count(state),width,height,context.resources.configuration.fontScale)
  }
  private fun render(base: Context,manager: AppWidgetManager,id: Int,state: MiniState,saveError: Boolean=false) {
   val context=AppLanguage.context(base);val lang=AppLanguage.current()
   val view=RemoteViews(context.packageName,R.layout.converter_mini)
   // Apply explicit theme colors too: a launcher may reapply rather than reinflate RemoteViews.
   val ink=context.getColor(R.color.widget_text);val keyColor=context.getColor(R.color.widget_key)
   view.setInt(R.id.mini_root,"setBackgroundColor",context.getColor(R.color.widget_surface))
   listOf(R.id.mini_title,R.id.mini_input,R.id.mini_result,R.id.mini_category,R.id.mini_from,R.id.mini_to,R.id.picker_title,R.id.picker_page,R.id.picker_previous,R.id.picker_next,R.id.picker_back,R.id.mini_too_small,R.id.k0,R.id.k1,R.id.k2,R.id.k3,R.id.k4,R.id.k5,R.id.k6,R.id.k7,R.id.k8,R.id.k9,R.id.clear,R.id.erase,R.id.sign,R.id.decimal).forEach {view.setTextColor(it,ink)}
   listOf(R.id.mini_category,R.id.mini_from,R.id.mini_to,R.id.picker_previous,R.id.picker_next,R.id.picker_back,R.id.k0,R.id.k1,R.id.k2,R.id.k3,R.id.k4,R.id.k5,R.id.k6,R.id.k7,R.id.k8,R.id.k9,R.id.clear,R.id.erase,R.id.sign,R.id.decimal).forEach {view.setInt(it,"setBackgroundColor",keyColor)}
   view.setTextColor(R.id.mini_swap,context.getColor(R.color.widget_accent_text))
   view.setInt(R.id.mini_swap,"setBackgroundColor",context.getColor(R.color.widget_accent))
   view.setTextViewText(R.id.mini_title,context.getString(R.string.widget_name))
   // RemoteViews are inflated by the launcher. Set every visible localized string explicitly.
   fun text(v: Int,value: String) {view.setTextViewText(v,value)}
   fun click(v: Int,command: String,description: String) {view.setOnClickPendingIntent(v,pending(base,id,command));view.setContentDescription(v,description)}
   val (w,h)=dimensions(context,manager,id)
   val fits=MiniReducer.fits(w,h)
   view.setViewVisibility(R.id.mini_too_small,if(fits) View.GONE else View.VISIBLE)
   text(R.id.mini_too_small,context.getString(R.string.widget_enlarge))
   view.setViewVisibility(R.id.main_panel,if(fits && state.picker==Picker.MAIN) View.VISIBLE else View.GONE)
   view.setViewVisibility(R.id.picker_panel,if(fits && state.picker!=Picker.MAIN) View.VISIBLE else View.GONE)
   val pair=state.pair
   text(R.id.mini_category,context.getString(pair.category.nameRes)+" ▾")
   text(R.id.mini_from,Catalog.unit(pair.from).symbol+" ▾")
   text(R.id.mini_to,Catalog.unit(pair.to).symbol+" ▾")
   click(R.id.mini_category,"menu-category",context.getString(R.string.widget_choose_category))
   click(R.id.mini_from,"menu-from",context.getString(R.string.widget_choose_from)+": "+context.getString(Catalog.unit(pair.from).nameRes))
   click(R.id.mini_to,"menu-to",context.getString(R.string.widget_choose_to)+": "+context.getString(Catalog.unit(pair.to).nameRes))
   click(R.id.mini_swap,"swap",context.getString(R.string.widget_swap))
   val result=MiniReducer.result(state)
   view.setBoolean(R.id.mini_swap,"setEnabled",result.value!=null)
   val amount=(state.input.ifEmpty {"—"}).let {if(lang=="es") it.replace('.',',') else it}
   text(R.id.mini_input,"$amount ${Catalog.unit(pair.from).symbol}")
   view.setContentDescription(R.id.mini_input,context.getString(R.string.widget_input,amount,Catalog.unit(pair.from).symbol))
   val display=result.value?.let {Engine.display(it,lang)}
   val output=if(saveError) context.getString(R.string.widget_save_error) else result.error?.let {context.getString(it.resource())} ?: ((if(display?.approximate==true) "≈ " else "")+(display?.text?:"—")+" "+Catalog.unit(pair.to).symbol)
   text(R.id.mini_result,output)
   view.setContentDescription(R.id.mini_result,context.getString(R.string.widget_result,output,""))
   val digits=listOf(R.id.k0,R.id.k1,R.id.k2,R.id.k3,R.id.k4,R.id.k5,R.id.k6,R.id.k7,R.id.k8,R.id.k9)
   digits.forEachIndexed {digit,v->click(v,digit.toString(),context.getString(R.string.widget_digit,digit.toString()))}
   text(R.id.decimal,if(lang=="es") "," else ".")
   text(R.id.clear,context.getString(R.string.widget_clear_symbol))
   click(R.id.decimal,"decimal",context.getString(R.string.widget_decimal))
   click(R.id.clear,"clear",context.getString(R.string.widget_clear))
   click(R.id.erase,"erase",context.getString(R.string.widget_backspace))
   click(R.id.sign,"sign",context.getString(R.string.widget_sign))
   if(state.picker!=Picker.MAIN) {
    val items=if(state.picker==Picker.CATEGORY) Category.entries.map {context.getString(it.nameRes) to "category:${it.name}"}
     else Catalog.units(pair.category).map {context.getString(it.nameRes)+" · "+it.symbol to (if(state.picker==Picker.FROM) "from:${it.id}" else "to:${it.id}")}
    val plan=pickerPlan(context,manager,id,state)
    val page=plan.page(state.page,items.size)
    view.setViewVisibility(R.id.picker_navigation,if(plan.navigationVisible) View.VISIBLE else View.GONE)
    view.setViewVisibility(R.id.picker_previous,if(plan.navigationVisible) View.VISIBLE else View.GONE)
    view.setViewVisibility(R.id.picker_next,if(plan.navigationVisible) View.VISIBLE else View.GONE)
    view.setViewVisibility(R.id.picker_page,if(plan.navigationVisible) View.VISIBLE else View.GONE)
    text(R.id.picker_title,context.getString(when(state.picker){Picker.CATEGORY->R.string.widget_choose_category;Picker.FROM->R.string.widget_choose_from;else->R.string.widget_choose_to}))
    view.removeAllViews(R.id.picker_options)
    val visible=plan.range(page,items.size).map {items[it]}
    if(state.picker==Picker.CATEGORY) {
     visible.chunked(plan.columns).forEach {cells ->
      val row=RemoteViews(context.packageName,R.layout.mini_category_row)
      cells.forEach {(label,cmd)->
       val cellLayout=when(plan.rowHeightDp) {96->R.layout.mini_category_cell_96;64->R.layout.mini_category_cell_64;else->R.layout.mini_category_cell_48}
       val cell=RemoteViews(context.packageName,cellLayout)
       cell.setTextColor(R.id.option_button,ink)
       cell.setInt(R.id.option_button,"setBackgroundColor",keyColor)
       cell.setTextViewText(R.id.option_button,label)
       cell.setContentDescription(R.id.option_button,label)
       cell.setOnClickPendingIntent(R.id.option_button,pending(base,id,cmd))
       row.addView(R.id.category_row_children,cell)
      }
      view.addView(R.id.picker_options,row)
     }
    } else {
     visible.forEach {(label,cmd)->
      val row=RemoteViews(context.packageName,R.layout.mini_picker_option)
      row.setTextColor(R.id.option_button,ink)
      row.setInt(R.id.option_button,"setBackgroundColor",keyColor)
      row.setTextViewText(R.id.option_button,label)
      row.setContentDescription(R.id.option_button,label)
      row.setOnClickPendingIntent(R.id.option_button,pending(base,id,cmd))
      view.addView(R.id.picker_options,row)
     }
    }
    text(R.id.picker_page,context.getString(R.string.page,page+1,plan.lastPage(items.size)+1))
    text(R.id.picker_previous,context.getString(R.string.previous));text(R.id.picker_next,context.getString(R.string.next));text(R.id.picker_back,context.getString(R.string.back))
    click(R.id.picker_previous,"previous",context.getString(R.string.previous));click(R.id.picker_next,"next",context.getString(R.string.next));click(R.id.picker_back,"back",context.getString(R.string.back))
    view.setBoolean(R.id.picker_previous,"setEnabled",page>0);view.setBoolean(R.id.picker_next,"setEnabled",page<plan.lastPage(items.size))
   }
   manager.updateAppWidget(id,view)
  }
 }
}
