package com.simplesoft.converter.widget
import com.simplesoft.converter.domain.*
import org.junit.Test
import org.junit.Assert.*
import java.math.BigDecimal
class MiniReducerTest {
 private fun enter(value: String,start: MiniState=MiniState()): MiniState=value.fold(start) {s,c->MiniReducer.apply(s,when(c){'.'->"decimal";'-'->"sign";else->c.toString()})}
 private fun same(expected: String,s: MiniState) {assertEquals(0,MiniReducer.result(s).value!!.compareTo(BigDecimal(expected)))}
 @Test fun digitsAndDecimal() {val s=enter("2.5",MiniState(pairId="km>m"));assertEquals("2.5",s.input);same("2500",s);assertEquals("2.5",MiniReducer.apply(s,"decimal").input)}
 @Test fun negativeAndSignToggle() {val s=enter("-40",MiniState(pairId="C>F"));same("-40",s);assertEquals("40",MiniReducer.apply(s,"sign").input)}
 @Test fun clearAndErase() {val s=enter("123");assertEquals("12",MiniReducer.apply(s,"erase").input);assertNull(MiniReducer.result(MiniReducer.apply(s,"clear")).value)}
 @Test fun emptyDecimalAndNegativeDecimal() {assertEquals("0.",MiniReducer.apply(MiniState(),"decimal").input);assertEquals("-0.",MiniReducer.apply(MiniReducer.apply(MiniState(),"sign"),"decimal").input)}
 @Test fun exactSwapsSurviveStorageRoundTrip() {var s=enter("1");repeat(30) {s=MiniCodec.decode(MiniCodec.encode(MiniReducer.apply(s,"swap")))};assertEquals("m>ft",s.pairId);assertEquals(0,Engine.fromBase(s.canonical!!.toBigDecimal(),"m").compareTo(BigDecimal.ONE))}
 @Test fun changesCategoryAndLimitsUnitChoices() {var s=MiniReducer.apply(MiniState(),"category:PRESSURE");assertEquals(Category.PRESSURE,s.pair.category);assertEquals(s,MiniReducer.apply(s,"from:kg"));s=MiniReducer.apply(s,"from:Pa");assertEquals("Pa",s.pair.from)}
 @Test fun remembersPairsPerCategory() {var s=MiniReducer.apply(MiniState(),"category:PRESSURE");s=MiniReducer.apply(s,"to:mmHg");s=MiniReducer.apply(s,"category:LENGTH");s=MiniReducer.apply(s,"category:PRESSURE");assertEquals("mmHg",s.pair.to)}
 @Test fun selectorsPaginateInsideWidget() {
  var s=MiniReducer.apply(MiniState(),"menu-category");assertEquals(Picker.CATEGORY,s.picker)
  s=MiniReducer.apply(s,"next");assertEquals(0,s.page)
  assertEquals(Picker.MAIN,MiniReducer.apply(s,"category:VOLUME").picker)
  s=MiniReducer.apply(MiniState(),"menu-from");s=MiniReducer.apply(s,"next");assertEquals(1,s.page)
  assertEquals(1,MiniReducer.apply(s,"next").page)
 }
 @Test fun persistenceIncludesNumberAndCanonicalBase() {val s=MiniReducer.apply(enter("12.25"),"swap");assertEquals(s,MiniCodec.decode(MiniCodec.encode(s)))}
 @Test fun separateInstancesAndDeletion() {val disk=mutableMapOf<Int,String>();disk[7]=MiniCodec.encode(enter("12"));disk[8]=MiniCodec.encode(enter("99",MiniState(pairId="kg>lb")));assertEquals("12",MiniCodec.decode(disk[7]).input);assertEquals("99",MiniCodec.decode(disk[8]).input);disk.remove(7);assertEquals(MiniState(),MiniCodec.decode(disk[7]));assertEquals("kg>lb",MiniCodec.decode(disk[8]).pairId)}
 @Test fun sizesRespectMinimum() {assertTrue(MiniReducer.fits(260,420));assertTrue(MiniReducer.fits(400,600));assertFalse(MiniReducer.fits(240,420));assertFalse(MiniReducer.fits(300,200))}
 @Test fun temperatureValidation() {assertEquals(InputError.BELOW_ABSOLUTE_ZERO,MiniReducer.result(enter("-300",MiniState(pairId="C>K"))).error)}
 @Test fun unsupportedCommandsDoNothing() {val s=enter("1");assertEquals(s,MiniReducer.apply(s,"+"));assertEquals(s,MiniReducer.apply(s,"00"));assertEquals(s,MiniReducer.apply(s,"launch-app"))}
 @Test fun invalidStorageIsSafe() {assertEquals(MiniState(),MiniCodec.decode("broken"));assertEquals(MiniState(),MiniCodec.decode("{}"));assertEquals(MiniState(),MiniCodec.decode(null))}
 @Test fun pressureUsesSharedEngine() {same("100000",enter("1",MiniState(pairId="bar>Pa")))}
}
