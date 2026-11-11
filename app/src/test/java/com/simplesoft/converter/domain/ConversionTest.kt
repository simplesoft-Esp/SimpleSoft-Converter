package com.simplesoft.converter.domain
import org.junit.Assert.*
import org.junit.Test
import java.math.BigDecimal

class ConversionTest {
 private fun result(v:String,a:String,b:String)=Engine.convert(BigDecimal(v),PairKey(a,b))
 private fun equal(expected:String,value:BigDecimal) = assertEquals(0,BigDecimal(expected).compareTo(value))
 @Test fun inchDefinition() {equal("0.0254",result("1","in","m"))}
 @Test fun mileDefinition() {equal("1609.344",result("1","mi","m"))}
 @Test fun poundDefinition() {equal("0.45359237",result("1","lb","kg"))}
 @Test fun ounceDefinition() {equal("16",result("1","lb","oz"))}
 @Test fun differentGallons() {equal("3.785411784",result("1","galUS","l"));equal("4.54609",result("1","galUK","l"))}
 @Test fun squareFeet() {equal("0.09290304",result("1","ft2","m2"))}
 @Test fun acres() {equal("4046.8564224",result("1","acre","m2"))}
 @Test fun speedDefinition() {equal("1.852",result("1","kn","kmh"));equal("1.609344",result("1","mph","kmh"));equal("36",result("10","ms","kmh"))}
 @Test fun freezing() {equal("32",result("0","C","F"));equal("273.15",result("32","F","K"))}
 @Test fun boiling() {equal("212",result("100","C","F"))}
 @Test fun negativeTemperature() {equal("-40",result("-40","C","F"))}
 @Test fun absoluteZero() {equal("0",result("-273.15","C","K"));equal("0",result("-459.67","F","K"))}
 @Test fun belowZeroRejected() {for((v,u) in listOf("-0.0001" to "K","-273.151" to "C","-459.671" to "F")) assertThrows(IllegalArgumentException::class.java) {result(v,u,"K")}}
 @Test fun incompatibleCategoriesRejected() {assertThrows(IllegalArgumentException::class.java) {result("1","m","kg")}}
 @Test fun commaAndPartialInput() {equal("2.5",Engine.parse("2,5")!!);equal("0.5",Engine.parse(".5")!!);assertTrue(Engine.partial("-,"));assertNull(Engine.parse("-"))}
 @Test fun invalidInput() {for(s in listOf("NaN","Infinity","1,2.3","1 000","1e2","1,234,567","9".repeat(121))) assertNull(s,Engine.parse(s))}
 @Test fun roundingMarked() {val v=result("1","m","ft");assertEquals("3,280839895",Engine.display(v).text);assertTrue(Engine.display(v).approximate);assertFalse(Engine.display(BigDecimal("1.000")).approximate)}
 @Test fun canonicalSwapDoesNotReparseRoundedText() {val base=Engine.toBase(BigDecimal.ONE,"m");val feet=Engine.fromBase(base,"ft");assertTrue(Engine.display(feet).approximate);equal("1",Engine.fromBase(base,"m"))}
 @Test fun everyPairRoundTrip() {
  for(c in Category.entries) for(a in Catalog.units(c)) for(b in Catalog.units(c)) {
   val v=BigDecimal("123.456789");val back=Engine.convert(Engine.convert(v,PairKey(a.id,b.id)),PairKey(b.id,a.id))
   assertTrue("${a.id} -> ${b.id}: $back",back.subtract(v).abs()<BigDecimal("1E-25"))
  }
 }
 @Test fun directionalPairs() {assertNotEquals(PairKey("m","ft"),PairKey("ft","m"));assertNull(PairKey.decode("m>kg"));assertNull(PairKey.decode("missing>m"))}
 @Test fun rankingDecay() {assertEquals(4.0,UsageRanking.decayed(8.0,0,14L*86_400_000),1E-12);assertEquals(8.0,UsageRanking.decayed(8.0,1000,0),0.0)}
 @Test fun recentActivityBeatsStaleCount() {assertTrue(UsageRanking.decayed(5.0,0,0)>UsageRanking.decayed(8.0,-28L*86_400_000,0))}
}
