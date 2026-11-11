package com.simplesoft.core
import org.junit.Test
import org.junit.Assert.*
import java.io.File
class MessageLocalizationTest {
 @Test fun stableIdsAndBothOfficialLanguages() {
  val c=BrandMessages.parse(File("src/main/assets/simplesoft_messages.json").readText())
  assertEquals(5,c.forLanguage("es").size);assertEquals(5,c.forLanguage("en").size)
  assertEquals(c.forLanguage("en").map {it.id},c.forLanguage("es").map {it.id})
  assertTrue(c.forLanguage("es").first().text.startsWith("EL SOFTWARE"))
 }
 @Test fun unsupportedMessageLanguageFallsBackToEnglish() {
  val c=BrandMessages.parse(File("src/main/assets/simplesoft_messages.json").readText())
  assertEquals(c.forLanguage("en"),c.forLanguage("eu"));assertEquals(c.forLanguage("en"),c.forLanguage("fr"))
 }
}
