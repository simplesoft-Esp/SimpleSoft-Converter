package com.simplesoft.core
import org.junit.Test
import org.junit.Assert.*

class BrandMessagesTest {
 private fun json(items: String)="""{"schemaVersion":1,"catalogVersion":"1.0","language":"en","messages":[$items]}"""
 private val one="""{"id":"one","enabled":true,"text":"FIRST MESSAGE."}"""
 @Test fun readsActualSharedFile() {
  val source=java.io.File("src/main/assets/simplesoft_messages.json").readText()
  val c=BrandMessages.parse(source)
  assertNull(c.issue);assertEquals(5,c.forLanguage("en").size)
  assertEquals("SOFTWARE SHOULD HELP, NOT INTERRUPT.",c.messages.first().text)
 }
 @Test fun noFileNoCrash() {assertTrue(BrandMessages.parse(null).messages.isEmpty());assertTrue(BrandMessages.parse("").messages.isEmpty())}
 @Test fun malformedSafe() {assertTrue(BrandMessages.parse("{broken").messages.isEmpty())}
 @Test fun emptyListSafe() {assertEquals(emptyList<BrandMessage>(),BrandMessages.parse(json("")).messages)}
 @Test fun futureSchemaSafe() {assertTrue(BrandMessages.parse(json(one).replace("\"schemaVersion\":1","\"schemaVersion\":2")).messages.isEmpty())}
 @Test fun disabledAndDuplicates() {val c=BrandMessages.parse(json("$one,$one,"+"""{"id":"off","enabled":false,"text":"OFF"}"""));assertEquals(1,c.messages.size)}
 @Test fun addingMessageNeedsNoKotlinChange() {assertEquals(2,BrandMessages.parse(json("$one,"+"""{"id":"two","enabled":true,"language":"es","text":"OTRO MENSAJE"}""")).messages.size)}
 @Test fun invalidItemsIgnored() {assertTrue(BrandMessages.parse(json("""{"id":"BAD ID","text":"x","enabled":true},{"id":"empty","text":"","enabled":true},{"id":"bad-bool","text":"x","enabled":"yes"}""")).messages.isEmpty())}
 @Test fun oversizedSafe() {assertTrue(BrandMessages.parse(" ".repeat(70000)).messages.isEmpty())}
 @Test fun rotationCycles() {assertEquals(1,BannerPolicy.next(0,5));assertEquals(0,BannerPolicy.next(4,5));assertEquals(0,BannerPolicy.next(0,0))}
 @Test fun reducedMotionPolicies() {
  assertFalse(BannerPolicy.animate(true,1f,false,true));assertFalse(BannerPolicy.animate(false,0f,false,true))
  assertFalse(BannerPolicy.animate(false,1f,true,true));assertFalse(BannerPolicy.animate(false,1f,false,false))
  assertTrue(BannerPolicy.animate(false,1f,false,true))
 }
 @Test fun animationBoundsFrameDelta() {assertEquals(97f,BannerPolicy.advance(100f,30f,1f),0.0001f)}
 @Test fun glyphsRenderable() {assertTrue(DotMatrix.supported("SOFTWARE SHOULD HELP, NOT INTERRUPT."));assertTrue(DotMatrix.points("ABC").isNotEmpty());assertFalse(DotMatrix.supported("日本語"));assertEquals(18,DotMatrix.columns("ABC"))}
}
