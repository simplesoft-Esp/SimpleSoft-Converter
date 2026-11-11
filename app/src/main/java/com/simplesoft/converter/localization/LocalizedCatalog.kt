package com.simplesoft.converter.localization
import android.content.Context
import com.simplesoft.converter.domain.*
fun searchCatalog(context: Context): List<SearchUnit> = Catalog.all.map {u->SearchUnit(u.id,u.category,context.getString(u.category.nameRes),context.getString(u.nameRes),u.symbol,context.resources.getStringArray(u.aliasesRes).toList())}
