package com.simplesoft.core

import org.json.JSONObject

data class BrandMessage(val id: String,val language: String,val text: String)
data class MessageCatalog(val catalogVersion: String="",val messages: List<BrandMessage> = emptyList(),val issue: String?=null) {
 fun forLanguage(language: String): List<BrandMessage> {
  val selected=LanguagePolicy.select(language)
  return messages.groupBy {it.id}.values.mapNotNull {variants->variants.firstOrNull {it.language==selected}?:variants.firstOrNull {it.language=="en"}}
 }
}
object BrandMessages {
 const val ASSET="simplesoft_messages.json"
 const val MAX_BYTES=65536
 /** No network or hardcoded promotional fallback. Invalid catalogs simply produce no footer. */
 fun parse(json: String?): MessageCatalog {
  if(json.isNullOrBlank()) return MessageCatalog(issue="missing-or-empty")
  if(json.toByteArray(Charsets.UTF_8).size>MAX_BYTES) return MessageCatalog(issue="too-large")
  return try {
   val root=JSONObject(json)
   require(root.opt("schemaVersion")==1)
   val version=(root.opt("catalogVersion") as? String ?: error("version")).also {require(it.isNotBlank())}
   val language=(root.opt("language") as? String ?: error("language")).also {require(it.matches(Regex("[A-Za-z]{2,8}(?:-[A-Za-z0-9]{2,8})*")))}
   val source=root.getJSONArray("messages")
   require(source.length()<=100)
   val ids=mutableSetOf<String>()
   val messages=buildList {
    for(i in 0 until source.length()) {
     val item=source.optJSONObject(i)?:continue
     val id=item.optString("id").trim()
     val text=(item.opt("text") as? String)?.trim()?:continue
     val enabled=item.opt("enabled") as? Boolean?:continue
     val lang=if(item.has("language")) item.optString("language") else language
     if(!id.matches(Regex("[a-z0-9][a-z0-9._-]{0,63}")) || text.isEmpty() || text.length>200 || text.any {it.isISOControl()} || !lang.matches(Regex("[A-Za-z]{2,8}(?:-[A-Za-z0-9]{2,8})*"))) continue
     if(!ids.add("$id|$lang") || !enabled) continue
     add(BrandMessage(id,lang,text))
    }
   }
   MessageCatalog(version,messages)
  } catch(_: Exception) { MessageCatalog(issue="invalid-schema-or-json") }
 }
}
object BannerPolicy {
 const val SCROLL_SPEED_DP_PER_SECOND=45f
 fun animate(userReduced: Boolean, systemScale: Float, touchExploration: Boolean, resumed: Boolean) = !userReduced && systemScale>0f && !touchExploration && resumed
 fun next(current: Int,count: Int): Int = if(count<=0) 0 else (current+1).mod(count)
 fun advance(x: Float,speed: Float,seconds: Float) = x-speed*seconds.coerceIn(0f,0.1f)
}
