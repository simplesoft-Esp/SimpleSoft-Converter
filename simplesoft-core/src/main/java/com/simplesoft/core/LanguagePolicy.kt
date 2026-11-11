package com.simplesoft.core
object LanguagePolicy {
 fun select(primaryLanguage: String): String = if(primaryLanguage.substringBefore('-').substringBefore('_').equals("es",true)) "es" else "en"
}
