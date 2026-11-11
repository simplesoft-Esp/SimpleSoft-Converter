package com.simplesoft.converter.localization
import com.simplesoft.core.LanguagePolicy
import com.simplesoft.converter.domain.*
import java.math.BigDecimal
import org.junit.Test
import org.junit.Assert.*
class LocalizationTest {
 @Test fun officialLanguagesAndFallback() {assertEquals("es",LanguagePolicy.select("es-ES"));for(lang in listOf("en","eu","fr","de","zh","")) assertEquals("en",LanguagePolicy.select(lang))}
 @Test fun completeResourceSets() {val en=ResourceFixtures.strings("en");val es=ResourceFixtures.strings("es");assertEquals(en.keys,es.keys);assertTrue(en.values.all {it.isNotBlank()});assertTrue(es.values.all {it.isNotBlank()});assertEquals("Favorites",en["favorites"]);assertEquals("Favoritos",es["favorites"])}
 @Test fun allUnitsLocalizedWithoutNewFactors() {for(lang in listOf("es","en")) {val indexed=ResourceFixtures.units(lang);assertEquals(Catalog.all.size,indexed.size);assertEquals(Catalog.all.map {it.id},indexed.map {it.id});assertTrue(indexed.all {it.name.isNotBlank() && it.categoryName.isNotBlank()})}}
 @Test fun decimalFormattingFollowsLanguage() {val value=BigDecimal("2.5");assertEquals("2,5",Engine.display(value,"es").text);assertEquals("2.5",Engine.display(value,"en").text)}
 @Test fun changingLanguageDoesNotChangeNumber() {val base=Engine.toBase(BigDecimal.ONE,"m");val v=Engine.fromBase(base,"ft");assertEquals(Engine.display(v,"en").text.replace('.',','),Engine.display(v,"es").text);assertEquals(0,Engine.fromBase(base,"m").compareTo(BigDecimal.ONE))}
 @Test fun translatedFormatArgumentsMatch() {
  val pattern=Regex("%[0-9]+\\$[sd]");val en=ResourceFixtures.strings("en");val es=ResourceFixtures.strings("es")
  for(key in en.keys) assertEquals(key,pattern.findAll(en.getValue(key)).map {it.value}.toList(),pattern.findAll(es.getValue(key)).map {it.value}.toList())
 }
}
