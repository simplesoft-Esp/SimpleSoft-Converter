package com.simplesoft.converter.localization
import java.io.File
import javax.xml.parsers.DocumentBuilderFactory
import com.simplesoft.converter.domain.*
object ResourceFixtures {
 fun strings(language: String): Map<String,String> {
  val doc=DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(File("src/main/res/${if(language=="es") "values-es" else "values"}/strings.xml"))
  val nodes=doc.getElementsByTagName("string")
  return (0 until nodes.length).associate {val n=nodes.item(it);n.attributes.getNamedItem("name").nodeValue to n.textContent}
 }
 fun units(language: String): List<SearchUnit> {
  val folder=if(language=="es") "values-es" else "values"
  val doc=DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(File("src/main/res/$folder/strings.xml"))
  val texts=strings(language);val arrays=doc.getElementsByTagName("string-array")
  val aliases=(0 until arrays.length).associate {val n=arrays.item(it);n.attributes.getNamedItem("name").nodeValue to n.textContent}
  return Catalog.all.map {u->SearchUnit(u.id,u.category,texts.getValue("category_${u.category.name.lowercase()}"),texts.getValue("unit_${u.id.lowercase()}"),u.symbol,listOf(aliases.getValue("aliases_${u.id.lowercase()}")))}
 }
}
