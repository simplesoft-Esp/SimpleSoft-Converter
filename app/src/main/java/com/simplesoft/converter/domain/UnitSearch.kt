package com.simplesoft.converter.domain
import java.text.Normalizer
import java.util.Locale

data class SearchUnit(val id: String,val category: Category,val categoryName: String,val name: String,val symbol: String,val aliases: List<String>)
object UnitSearch {
 fun normalize(text: String)=Normalizer.normalize(text,Normalizer.Form.NFD).replace(Regex("\\p{M}+"),"").lowercase(Locale.ROOT).trim()
 fun search(query: String,source: List<SearchUnit>): List<SearchUnit> {
  val q=normalize(query)
  if(q.isEmpty()) return emptyList()
  val tokens=q.split(Regex("\\s+"))
  fun text(u: SearchUnit)=normalize(listOf(u.categoryName,u.name,u.symbol,u.id,*u.aliases.toTypedArray()).joinToString(" "))
  return source.filter {u->tokens.all {it in text(u)}}.sortedWith(compareBy<SearchUnit> {
   when(q) {normalize(it.symbol),normalize(it.id)->0;normalize(it.name)->1;normalize(it.categoryName)->2;else->3}
  }.thenBy {it.category.ordinal}.thenBy {normalize(it.name)})
 }
}
object SearchSelection {
 fun forOrigin(id: String,rememberedPair: String?): PairKey {
  val unit=Catalog.unit(id)
  val old=PairKey.decode(rememberedPair?:"")?.takeIf {it.category==unit.category}?:Catalog.default(unit.category)
  val target=if(old.to!=id) old.to else Catalog.units(unit.category).first {it.id!=id}.id
  return PairKey(id,target)
 }
}
