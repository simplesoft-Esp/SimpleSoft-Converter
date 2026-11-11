package com.simplesoft.converter.domain
import com.simplesoft.converter.R

enum class Family(val nameRes: Int) { PHYSICAL(R.string.family_physical), TECHNICAL(R.string.family_technical), EVERYDAY(R.string.family_everyday) }
enum class Category(val nameRes: Int, val family: Family = Family.PHYSICAL) {
 LENGTH(R.string.category_length), MASS(R.string.category_mass), TEMPERATURE(R.string.category_temperature), VOLUME(R.string.category_volume), AREA(R.string.category_area), SPEED(R.string.category_speed), PRESSURE(R.string.category_pressure)
}
data class PlannedCategory(val id: String,val nameRes: Int,val family: Family,val equivalenceBased: Boolean=false)
object CategoryFamilies {
 fun available(family: Family)=Category.entries.filter {it.family==family}
 val planned=listOf(
  PlannedCategory("energy",R.string.planned_energy,Family.TECHNICAL),PlannedCategory("power",R.string.planned_power,Family.TECHNICAL),
  PlannedCategory("time",R.string.planned_time,Family.TECHNICAL),PlannedCategory("digital-storage",R.string.planned_digital_storage,Family.TECHNICAL),
  PlannedCategory("shoe-size",R.string.planned_shoe_size,Family.EVERYDAY,true),PlannedCategory("clothes-size",R.string.planned_clothes_size,Family.EVERYDAY,true),PlannedCategory("cooking",R.string.planned_cooking,Family.EVERYDAY)
 )
}
