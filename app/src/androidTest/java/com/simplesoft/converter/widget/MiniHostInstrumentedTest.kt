package com.simplesoft.converter.widget

import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.simplesoft.converter.R
import org.junit.Test
import org.junit.Assert.*
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MiniHostInstrumentedTest {
 private fun waitUntil(block: ()->Boolean) {
  val end=System.currentTimeMillis()+10000
  while(System.currentTimeMillis()<end) {if(block())return;Thread.sleep(60)}
  assertTrue("Timed out waiting for widget host",block())
 }
 private fun descendants(v: View): List<View> = listOf(v)+(if(v is ViewGroup) (0 until v.childCount).flatMap {descendants(v.getChildAt(it))} else emptyList())
 @Test fun convertsThroughRealRemoteViewsBroadcastsWithoutMainActivity() {
  val instrumentation=InstrumentationRegistry.getInstrumentation()
  val monitor=instrumentation.addMonitor("com.simplesoft.converter.MainActivity",null,false)
  ActivityScenario.launch(WidgetTestHostActivity::class.java).use {scenario ->
   fun click(id: Int) {
    waitUntil {var ready=false;scenario.onActivity {ready=it.widgetView.findViewById<View>(id)?.let {v->v.isShown && v.isEnabled && v.hasOnClickListeners()}==true};ready}
    scenario.onActivity {assertTrue(it.widgetView.findViewById<View>(id).performClick())}
   }
   fun choose(nameId: Int) {
    repeat(3) {
     var found=false
     waitUntil {var visible=false;scenario.onActivity {visible=it.widgetView.findViewById<View>(R.id.picker_panel).isShown};visible}
     scenario.onActivity {activity ->
      val name=activity.getString(nameId)
      val button=descendants(activity.widgetView).filterIsInstance<Button>().firstOrNull {it.id==R.id.option_button && it.text.toString().startsWith(name) && it.isShown}
      if(button!=null) {assertTrue(button.performClick());found=true}
     }
     if(found)return
     click(R.id.picker_next)
     Thread.sleep(150)
    }
    fail("Option not found")
   }
   fun number(value: String) {
    val keys=listOf(R.id.k0,R.id.k1,R.id.k2,R.id.k3,R.id.k4,R.id.k5,R.id.k6,R.id.k7,R.id.k8,R.id.k9)
    value.forEach {c->click(if(c=='.') R.id.decimal else if(c=='-') R.id.sign else keys[c-'0']);Thread.sleep(100)}
   }
   fun resultContains(value: String) {waitUntil {var ok=false;scenario.onActivity {ok=it.widgetView.findViewById<TextView>(R.id.mini_result).text.toString().contains(value)};ok}}
   click(R.id.mini_from);choose(R.string.unit_km)
   click(R.id.mini_to);choose(R.string.unit_m)
   number("2.5");resultContains("2500")
   var first=0;scenario.onActivity {first=it.currentId;assertEquals("2.5",MiniStore(it).load(first).input)}
   scenario.recreate();resultContains("2500")
   click(R.id.mini_category)
   waitUntil {var visible=false;scenario.onActivity {visible=it.widgetView.findViewById<View>(R.id.picker_panel).isShown};visible}
   scenario.onActivity {activity ->
    val buttons=descendants(activity.widgetView).filterIsInstance<Button>().filter {it.id==R.id.option_button && it.isShown}
    assertEquals(7,buttons.size)
    assertFalse(activity.widgetView.findViewById<View>(R.id.picker_navigation).isShown)
    buttons.forEach {button ->
     val rect=android.graphics.Rect();assertTrue(button.getGlobalVisibleRect(rect));assertEquals(button.height,rect.height())
    }
   }
   choose(R.string.category_speed)
   click(R.id.mini_category);choose(R.string.category_pressure)
   click(R.id.mini_to);choose(R.string.unit_pa)
   click(R.id.clear);number("1");resultContains("100000")
   click(R.id.mini_swap);resultContains("1 bar")
   click(R.id.mini_category);choose(R.string.category_temperature)
   click(R.id.clear);number("-40");resultContains("-40")
   var second=0;scenario.onActivity {second=it.addInstance()}
   number("99")
   waitUntil {MiniStore(instrumentation.targetContext).load(second).input=="99"}
   scenario.onActivity {assertEquals("99",MiniStore(it).load(second).input);assertEquals("-40",MiniStore(it).load(first).input);it.showInstance(first)}
   resultContains("-40")
   scenario.onActivity {it.deleteInstance(second)}
   waitUntil {MiniStore(instrumentation.targetContext).load(second)==MiniState()}
   assertEquals(0,monitor.hits)
  }
  instrumentation.removeMonitor(monitor)
 }
}
