package com.simplesoft.converter.widget

import com.simplesoft.converter.domain.*
import com.simplesoft.converter.localization.ResourceFixtures
import org.junit.Assert.*
import org.junit.Test
import kotlin.math.ceil

class PickerLayoutsTest {
 private val categories get()=Category.entries.toList()
 @Test fun sevenCategoriesInRequestedOrder() {assertEquals(listOf(Category.LENGTH,Category.MASS,Category.TEMPERATURE,Category.VOLUME,Category.AREA,Category.SPEED,Category.PRESSURE),categories)}
 @Test fun sevenVisibleAtMinimumAndNormalSizes() {
  for((w,h) in listOf(260 to 420,280 to 440,400 to 600)) {
   val p=PickerLayouts.plan(Picker.CATEGORY,categories.size,w,h)
   assertFalse(p.navigationVisible);assertEquals(0,p.lastPage(categories.size));assertEquals(categories,p.range(0,categories.size).map {categories[it]})
  }
 }
 @Test fun speedAndPressureDirectSelection() {
  val s=MiniReducer.apply(MiniState(),"menu-category")
  for(c in listOf(Category.SPEED,Category.PRESSURE)) {
   val p=PickerLayouts.plan(s.picker,categories.size)
   assertTrue(c in p.range(0,categories.size).map {categories[it]})
   assertEquals(c,MiniReducer.apply(s,"category:${c.name}",p).pair.category)
  }
 }
 @Test fun nextDoesNotCreateUnnecessaryCategoryPage() {val s=MiniReducer.apply(MiniState(),"menu-category");assertEquals(0,MiniReducer.apply(s,"next").page);assertEquals(0,MiniReducer.apply(s,"previous").page)}
 @Test fun unitsRetainFivePerPageAndNavigation() {
  for(mode in listOf(Picker.FROM,Picker.TO)) {
   val s=MiniState(picker=mode);val count=MiniReducer.count(s);val p=PickerLayouts.plan(mode,count)
   assertTrue(p.navigationVisible);assertEquals(5,p.pageSize);assertEquals(1,p.lastPage(count))
   assertEquals(1,MiniReducer.apply(s,"next",p).page);assertEquals(3,p.range(1,count).count())
  }
 }
 @Test fun futureOverflowPagesOnlyWhenNeeded() {
  val p=PickerLayouts.plan(Picker.CATEGORY,31,260,420)
  assertTrue(p.navigationVisible);assertTrue(p.lastPage(31)>0)
  val indexes=(0..p.lastPage(31)).flatMap {p.range(it,31).toList()}
  assertEquals((0 until 31).toList(),indexes)
 }
 @Test fun largeFontUsesNecessaryPaginationAndSurvivesCodec() {
  val s=MiniState(picker=Picker.CATEGORY);val p=PickerLayouts.plan(s.picker,7,260,420,2.5f)
  assertTrue(p.navigationVisible)
  val next=MiniReducer.apply(s,"next",p);assertEquals(1,next.page)
  assertEquals(1,MiniCodec.decode(MiniCodec.encode(next)).page)
  assertEquals(0,MiniReducer.apply(next,"previous",p).page)
 }
 @Test fun geometryFitsAvailableHeightWithoutSmallerTouchTargets() {
  assertEquals(260,MiniReducer.MIN_WIDTH);assertEquals(420,MiniReducer.MIN_HEIGHT)
  for(font in listOf(1f,1.5f,2f,2.5f)) for(height in listOf(420,440,600)) {
   val p=PickerLayouts.plan(Picker.CATEGORY,7,260,height,font)
   val visible=p.range(0,7).count();val rows=ceil(visible.toDouble()/p.columns).toInt()
   val used=rows*p.rowHeightDp+PickerLayouts.RESERVED_DP+(if(p.navigationVisible) PickerLayouts.NAVIGATION_DP else 0)
   assertTrue("$font/$height uses $used",used<=height);assertTrue(p.rowHeightDp>=48)
  }
 }
 @Test fun categoryNamesRemainTranslated() {
  val es=ResourceFixtures.strings("es");val en=ResourceFixtures.strings("en")
  assertEquals("Velocidad",es["category_speed"]);assertEquals("Presión",es["category_pressure"])
  assertEquals("Speed",en["category_speed"]);assertEquals("Pressure",en["category_pressure"])
  categories.forEach {val key="category_${it.name.lowercase()}";assertFalse(es.getValue(key).isBlank());assertFalse(en.getValue(key).isBlank())}
 }
 @Test fun savedOldCategoryPageIsNormalizedWithoutLosingAmount() {
  val s=MiniState(input="12.25",picker=Picker.CATEGORY,page=1)
  val decoded=MiniCodec.decode(MiniCodec.encode(s));val p=PickerLayouts.plan(decoded.picker,7)
  assertEquals(0,p.page(decoded.page,7));assertEquals("12.25",MiniReducer.apply(decoded,"back",p).input)
 }
}
