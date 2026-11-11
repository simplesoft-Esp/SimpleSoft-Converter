package com.simplesoft.converter.domain
import java.math.BigDecimal
import com.simplesoft.converter.R

data class Measure(val id: String, val nameRes: Int, val aliasesRes: Int, val symbol: String, val category: Category, val rule: ConversionRule)
data class PairKey(val from: String, val to: String) {
 val id get() = "$from>$to"
 val category get() = Catalog.unit(from).category
 val label get() = "${Catalog.unit(from).symbol} → ${Catalog.unit(to).symbol}"
 companion object { fun decode(id: String): PairKey? = runCatching {
  val parts = id.split('>'); require(parts.size == 2)
  PairKey(parts[0], parts[1]).also { require(Catalog.unit(it.from).category == Catalog.unit(it.to).category) }
 }.getOrNull() }
}
object Catalog {
 private fun aliasResource(id: String): Int = when(id) {
  "mm" -> R.array.aliases_mm
  "cm" -> R.array.aliases_cm
  "m" -> R.array.aliases_m
  "km" -> R.array.aliases_km
  "in" -> R.array.aliases_in
  "ft" -> R.array.aliases_ft
  "yd" -> R.array.aliases_yd
  "mi" -> R.array.aliases_mi
  "mg" -> R.array.aliases_mg
  "g" -> R.array.aliases_g
  "kg" -> R.array.aliases_kg
  "t" -> R.array.aliases_t
  "oz" -> R.array.aliases_oz
  "lb" -> R.array.aliases_lb
  "C" -> R.array.aliases_c
  "F" -> R.array.aliases_f
  "K" -> R.array.aliases_k
  "ml" -> R.array.aliases_ml
  "l" -> R.array.aliases_l
  "m3" -> R.array.aliases_m3
  "galUS" -> R.array.aliases_galus
  "galUK" -> R.array.aliases_galuk
  "cm2" -> R.array.aliases_cm2
  "m2" -> R.array.aliases_m2
  "km2" -> R.array.aliases_km2
  "ha" -> R.array.aliases_ha
  "ft2" -> R.array.aliases_ft2
  "acre" -> R.array.aliases_acre
  "ms" -> R.array.aliases_ms
  "kmh" -> R.array.aliases_kmh
  "mph" -> R.array.aliases_mph
  "kn" -> R.array.aliases_kn
  "Pa" -> R.array.aliases_pa
  "kPa" -> R.array.aliases_kpa
  "bar" -> R.array.aliases_bar
  "mbar" -> R.array.aliases_mbar
  "atm" -> R.array.aliases_atm
  "psi" -> R.array.aliases_psi
  "mmHg" -> R.array.aliases_mmhg
  else -> error("Unknown unit ID")
 }

 private fun u(c: Category, id: String, nameRes: Int, symbol: String, factor: String) = Measure(id,nameRes,aliasResource(id),symbol,c,ConversionRules.forUnit(id, factor))
 val all = listOf(
  u(Category.LENGTH,"mm",R.string.unit_mm,"mm","0.001"),u(Category.LENGTH,"cm",R.string.unit_cm,"cm","0.01"),u(Category.LENGTH,"m",R.string.unit_m,"m","1"),u(Category.LENGTH,"km",R.string.unit_km,"km","1000"),u(Category.LENGTH,"in",R.string.unit_in,"in","0.0254"),u(Category.LENGTH,"ft",R.string.unit_ft,"ft","0.3048"),u(Category.LENGTH,"yd",R.string.unit_yd,"yd","0.9144"),u(Category.LENGTH,"mi",R.string.unit_mi,"mi","1609.344"),
  u(Category.MASS,"mg",R.string.unit_mg,"mg","0.000001"),u(Category.MASS,"g",R.string.unit_g,"g","0.001"),u(Category.MASS,"kg",R.string.unit_kg,"kg","1"),u(Category.MASS,"t",R.string.unit_t,"t","1000"),u(Category.MASS,"oz",R.string.unit_oz,"oz","0.028349523125"),u(Category.MASS,"lb",R.string.unit_lb,"lb","0.45359237"),
  u(Category.TEMPERATURE,"C",R.string.unit_c,"°C","1"),u(Category.TEMPERATURE,"F",R.string.unit_f,"°F","1"),u(Category.TEMPERATURE,"K",R.string.unit_k,"K","1"),
  u(Category.VOLUME,"ml",R.string.unit_ml,"mL","0.001"),u(Category.VOLUME,"l",R.string.unit_l,"L","1"),u(Category.VOLUME,"m3",R.string.unit_m3,"m³","1000"),u(Category.VOLUME,"galUS",R.string.unit_galus,"gal US","3.785411784"),u(Category.VOLUME,"galUK",R.string.unit_galuk,"gal imp","4.54609"),
  u(Category.AREA,"cm2",R.string.unit_cm2,"cm²","0.0001"),u(Category.AREA,"m2",R.string.unit_m2,"m²","1"),u(Category.AREA,"km2",R.string.unit_km2,"km²","1000000"),u(Category.AREA,"ha",R.string.unit_ha,"ha","10000"),u(Category.AREA,"ft2",R.string.unit_ft2,"ft²","0.09290304"),u(Category.AREA,"acre",R.string.unit_acre,"ac","4046.8564224"),
  u(Category.SPEED,"ms",R.string.unit_ms,"m/s","1"),u(Category.SPEED,"kmh",R.string.unit_kmh,"km/h","1"),u(Category.SPEED,"mph",R.string.unit_mph,"mph","1"),u(Category.SPEED,"kn",R.string.unit_kn,"kn","1"),
  u(Category.PRESSURE,"Pa",R.string.unit_pa,"Pa","1"),u(Category.PRESSURE,"kPa",R.string.unit_kpa,"kPa","1000"),u(Category.PRESSURE,"bar",R.string.unit_bar,"bar","100000"),u(Category.PRESSURE,"mbar",R.string.unit_mbar,"mbar","100"),u(Category.PRESSURE,"atm",R.string.unit_atm,"atm","101325"),u(Category.PRESSURE,"psi",R.string.unit_psi,"psi","1"),u(Category.PRESSURE,"mmHg",R.string.unit_mmhg,"mmHg conv","133.322387415")
 )
 fun unit(id: String) = all.first { it.id == id }
 fun units(c: Category) = all.filter { it.category == c }
 fun default(c: Category) = when(c) {
  Category.LENGTH -> PairKey("m","ft"); Category.MASS -> PairKey("kg","lb"); Category.TEMPERATURE -> PairKey("C","F")
  Category.VOLUME -> PairKey("l","ml"); Category.AREA -> PairKey("m2","ha"); Category.SPEED -> PairKey("kmh","mph"); Category.PRESSURE -> PairKey("bar","psi")
 }
}
