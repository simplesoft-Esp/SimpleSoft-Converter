package com.simplesoft.converter.domain
import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode
import kotlin.math.pow

object Engine {
 val context: MathContext = MathContext.DECIMAL128
 fun parse(text: String): BigDecimal? {
  val s = text.trim().replace(',','.')
  if(s.length > 120 || !Regex("[+-]?(?:[0-9]+(?:\\.[0-9]*)?|\\.[0-9]+)").matches(s)) return null
  return s.toBigDecimalOrNull()
 }
 fun partial(s: String) = s.trim() in listOf("", "+", "-", ".", ",", "-.", "-,", "+.", "+,")
 fun toBase(v: BigDecimal, unit: String): BigDecimal {
  val u=Catalog.unit(unit)
  val result=u.rule.toBase(v)
  require(u.category != Category.TEMPERATURE || result.signum()>=0) { "BELOW_ABSOLUTE_ZERO" }
  return result
 }
 fun fromBase(b: BigDecimal, unit: String): BigDecimal = Catalog.unit(unit).rule.fromBase(b)
 fun convert(v: BigDecimal, p: PairKey): BigDecimal {
  require(Catalog.unit(p.from).category == Catalog.unit(p.to).category)
  return fromBase(toBase(v,p.from),p.to)
 }
 data class Display(val text: String, val approximate: Boolean)
 fun display(v: BigDecimal, language: String="es"): Display {
  val rounded = v.round(MathContext(10,RoundingMode.HALF_EVEN)).stripTrailingZeros()
  val exponent=rounded.precision()-rounded.scale()-1
  val s=if(v.signum()!=0 && (exponent >= 10 || exponent <= -7)) rounded.toString() else rounded.toPlainString()
  return Display(if(language=="es") s.replace('.',',') else s,rounded.compareTo(v)!=0)
 }
}
object UsageRanking {
 const val HALF_LIFE_DAYS=14.0
 fun decayed(score: Double, last: Long, now: Long): Double = score * 2.0.pow(-((now-last).coerceAtLeast(0).toDouble()/86_400_000.0)/HALF_LIFE_DAYS)
}

enum class InputError { BELOW_ABSOLUTE_ZERO, INPUT_INVALID, OUT_OF_RANGE }
enum class StorageError { READ, WRITE }
