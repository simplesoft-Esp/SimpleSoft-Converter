package com.simplesoft.converter.widget

import kotlin.math.ceil

/** Geometry shared by rendering and action handling; never a second category catalog. */
data class PickerPlan(val columns: Int,val rowHeightDp: Int,val pageSize: Int,val navigationVisible: Boolean) {
 fun lastPage(count: Int)=((count.coerceAtLeast(1)-1)/pageSize).coerceAtLeast(0)
 fun page(page: Int,count: Int)=page.coerceIn(0,lastPage(count))
 fun range(requestedPage: Int,count: Int): IntRange {
  val start=page(requestedPage,count)*pageSize
  return start until minOf(start+pageSize,count)
 }
}
object PickerLayouts {
 // Title 40 + Back 48 + own padding 16 + a conservative host margin 16.
 const val RESERVED_DP=120
 const val NAVIGATION_DP=76 // page label 28 + previous/next row 48
 fun plan(picker: Picker,count: Int,widthDp: Int=280,heightDp: Int=440,fontScale: Float=1f): PickerPlan {
  if(picker!=Picker.CATEGORY) return PickerPlan(1,48,MiniReducer.PAGE_SIZE,true)
  val scale=if(fontScale.isFinite()) fontScale.coerceAtLeast(1f) else 1f
  val row=when {scale>2f->96;scale>1.3f->64;else->48}
  val columns=if(widthDp>=MiniReducer.MIN_WIDTH) 2 else 1
  val rows=ceil(count.coerceAtLeast(1).toDouble()/columns).toInt()
  if(rows*row <= (heightDp-RESERVED_DP).coerceAtLeast(0)) {
   return PickerPlan(columns,row,count.coerceAtLeast(1),false)
  }
  val rowsPerPage=((heightDp-RESERVED_DP-NAVIGATION_DP)/row).coerceAtLeast(1)
  return PickerPlan(columns,row,columns*rowsPerPage,true)
 }
}
