package com.simplesoft.converter.domain
import java.math.BigDecimal
import java.math.MathContext

/** Each rule states its canonical base. Future empirical equivalences need a different model. */
sealed interface ConversionRule {
 fun toBase(value: BigDecimal): BigDecimal
 fun fromBase(value: BigDecimal): BigDecimal
}
data class LinearRule(val numerator: BigDecimal, val denominator: BigDecimal=BigDecimal.ONE): ConversionRule {
 override fun toBase(value: BigDecimal)=if(denominator.compareTo(BigDecimal.ONE)==0) value.multiply(numerator) else value.multiply(numerator).divide(denominator,MathContext.DECIMAL128)
 override fun fromBase(value: BigDecimal)=value.multiply(denominator).divide(numerator,MathContext.DECIMAL128)
}
data class AffineRule(val before: BigDecimal,val numerator: BigDecimal,val denominator: BigDecimal,val after: BigDecimal): ConversionRule {
 override fun toBase(value: BigDecimal)=(value+before).multiply(numerator).divide(denominator,MathContext.DECIMAL128)+after
 override fun fromBase(value: BigDecimal)=(value-after).multiply(denominator).divide(numerator,MathContext.DECIMAL128)-before
}
object ConversionRules {
 private fun d(s: String)=BigDecimal(s)
 fun forUnit(id: String,factor: String): ConversionRule = when(id) {
  "C" -> AffineRule(d("0"),d("1"),d("1"),d("273.15"))
  "F" -> AffineRule(d("-32"),d("5"),d("9"),d("273.15"))
  // Speed uses metres/hour as canonical base, avoiding rounded recurring factors.
  "ms" -> LinearRule(d("3600")); "kmh" -> LinearRule(d("1000")); "mph" -> LinearRule(d("1609.344")); "kn" -> LinearRule(d("1852"))
  // Exact defining constants: pound × standard gravity / inch squared.
  "psi" -> LinearRule(d("0.45359237").multiply(d("9.80665")),d("0.0254").pow(2))
  else -> LinearRule(d(factor))
 }
}
