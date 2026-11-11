package com.simplesoft.converter.localization
import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.os.LocaleList
import java.util.Locale
import com.simplesoft.core.LanguagePolicy
import com.simplesoft.converter.R
import com.simplesoft.converter.domain.*
object AppLanguage {
 fun current(): String=LanguagePolicy.select(Resources.getSystem().configuration.locales[0].language)
 fun context(base: Context): Context {
  val config=Configuration(base.resources.configuration)
  config.setLocales(LocaleList(Locale.forLanguageTag(current())))
  return base.createConfigurationContext(config)
 }
}
fun InputError.resource()=when(this) {InputError.BELOW_ABSOLUTE_ZERO->R.string.error_below_zero;InputError.INPUT_INVALID->R.string.error_input;InputError.OUT_OF_RANGE->R.string.error_range}
fun StorageError.resource()=if(this==StorageError.READ) R.string.storage_read else R.string.storage_write
