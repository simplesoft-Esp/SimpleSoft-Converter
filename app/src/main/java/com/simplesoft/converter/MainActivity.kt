package com.simplesoft.converter

import android.os.Bundle
import android.content.Context
import android.content.res.Configuration
import com.simplesoft.converter.localization.AppLanguage
import com.simplesoft.converter.widget.ConverterMiniProvider
import android.app.Application
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.simplesoft.converter.data.LocalRepository
import com.simplesoft.converter.feature.ConverterViewModel
import com.simplesoft.converter.ui.ConverterApp

class ConverterApplication: Application() {
 val repository by lazy { LocalRepository(this) }
 override fun onConfigurationChanged(newConfig: Configuration) {super.onConfigurationChanged(newConfig);ConverterMiniProvider.refreshAll(this)}
}

class MainActivity: ComponentActivity() {
 override fun attachBaseContext(newBase: Context) {super.attachBaseContext(AppLanguage.context(newBase))}
 override fun onCreate(savedInstanceState: Bundle?) {
  val splash=installSplashScreen()
  super.onCreate(savedInstanceState)
  enableEdgeToEdge()
  val repository=(application as ConverterApplication).repository
  val model=ViewModelProvider(this,object: ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T: ViewModel> create(modelClass: Class<T>): T = ConverterViewModel(repository) as T
  })[ConverterViewModel::class.java]
  splash.setKeepOnScreenCondition { !model.state.value.ready && model.state.value.storageError==null }
  setContent { ConverterApp(model,this) }
 }
}
