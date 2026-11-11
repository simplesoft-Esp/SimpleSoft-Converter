package com.simplesoft.converter.widget

import android.app.Activity
import android.appwidget.AppWidgetHost
import android.appwidget.AppWidgetHostView
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.widget.FrameLayout

/** Debug-only instrumentation host. No launcher entry or link from the production UI. */
class WidgetTestHostActivity: Activity() {
 private lateinit var host: AppWidgetHost
 private lateinit var box: FrameLayout
 lateinit var widgetView: AppWidgetHostView
 var currentId=0;private set
 private val ids=mutableListOf<Int>()
 override fun onCreate(saved: Bundle?) {
  super.onCreate(saved)
  host=AppWidgetHost(this,505);host.startListening()
  box=FrameLayout(this);setContentView(box)
  ids.addAll(saved?.getIntArray("ids")?.toList()?:emptyList())
  if(ids.isEmpty()) addInstance() else showInstance(saved?.getInt("current")?:ids.first())
 }
 fun addInstance(): Int {
  val manager=AppWidgetManager.getInstance(this);val id=host.allocateAppWidgetId()
  val options=Bundle().apply {putInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH,280);putInt(AppWidgetManager.OPTION_APPWIDGET_MAX_WIDTH,280);putInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT,440);putInt(AppWidgetManager.OPTION_APPWIDGET_MAX_HEIGHT,440)}
  check(manager.bindAppWidgetIdIfAllowed(id,ComponentName(this,ConverterMiniProvider::class.java),options)) {"Grant appwidget bind permission with adb shell appwidget grantbind first"}
  ids.add(id);showInstance(id);return id
 }
 fun showInstance(id: Int) {
  currentId=id
  val manager=AppWidgetManager.getInstance(this)
  widgetView=host.createView(this,id,manager.getAppWidgetInfo(id))
  box.removeAllViews();box.addView(widgetView,FrameLayout.LayoutParams((280*resources.displayMetrics.density).toInt(),(440*resources.displayMetrics.density).toInt()))
  sendBroadcast(Intent(this,ConverterMiniProvider::class.java).setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE).putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS,intArrayOf(id)))
 }
 fun deleteInstance(id: Int) {host.deleteAppWidgetId(id);ids.remove(id)}
 override fun onSaveInstanceState(out: Bundle) {out.putIntArray("ids",ids.toIntArray());out.putInt("current",currentId);super.onSaveInstanceState(out)}
 override fun onDestroy() {host.stopListening();if(isFinishing) ids.toList().forEach {host.deleteAppWidgetId(it)};super.onDestroy()}
}
