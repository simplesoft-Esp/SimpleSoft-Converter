package com.simplesoft.converter.domain
import org.junit.Test
import org.junit.Assert.*
import java.math.BigDecimal
class PressureTest {
 private fun cv(v:String,a:String,b:String)=Engine.convert(BigDecimal(v),PairKey(a,b))
 private fun exact(s:String,v:BigDecimal)=assertEquals(0,BigDecimal(s).compareTo(v))
 @Test fun barAndPrefixes() {exact("100000",cv("1","bar","Pa"));exact("100",cv("1","mbar","Pa"));exact("1000",cv("1","kPa","Pa"))}
 @Test fun standardAtmosphere() {exact("101325",cv("1","atm","Pa"));exact("1.01325",cv("1","atm","bar"))}
 @Test fun conventionalMercury() {exact("133.322387415",cv("1","mmHg","Pa"));assertNotEquals(0,cv("760","mmHg","Pa").compareTo(BigDecimal("101325")))}
 @Test fun psiDefinition() {
  val expected=BigDecimal("6894.757293168361336722673445346891")
  assertTrue(cv("1","psi","Pa").subtract(expected).abs()<BigDecimal("1E-27"))
 }
 @Test fun canonicalPressureSwap() {val base=Engine.toBase(BigDecimal.ONE,"bar");assertTrue(Engine.display(Engine.fromBase(base,"psi")).approximate);exact("1",Engine.fromBase(base,"bar"))}
 @Test fun familiesOnlyExposeImplementedCategories() {assertEquals(7,CategoryFamilies.available(Family.PHYSICAL).size);assertTrue(CategoryFamilies.available(Family.TECHNICAL).isEmpty());assertTrue(CategoryFamilies.available(Family.EVERYDAY).isEmpty())}
}
