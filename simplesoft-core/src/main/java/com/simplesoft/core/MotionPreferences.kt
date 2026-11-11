package com.simplesoft.core

import android.content.Context
import android.database.ContentObserver
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.view.accessibility.AccessibilityManager
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.currentStateAsState

@Composable fun rememberBrandMotionAllowed(userReduced: Boolean): Boolean {
 val context=LocalContext.current
 val manager=remember(context) {context.getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager}
 fun scale()=runCatching {Settings.Global.getFloat(context.contentResolver,Settings.Global.ANIMATOR_DURATION_SCALE,1f)}.getOrDefault(0f)
 var systemScale by remember {mutableFloatStateOf(scale())}
 var exploration by remember {mutableStateOf(manager.isTouchExplorationEnabled)}
 val lifecycle=LocalLifecycleOwner.current
 val state by lifecycle.lifecycle.currentStateAsState()
 LaunchedEffect(state) {systemScale=scale();exploration=manager.isTouchExplorationEnabled}
 DisposableEffect(context,manager) {
  val observer=object: ContentObserver(Handler(Looper.getMainLooper())) {override fun onChange(selfChange: Boolean) {systemScale=scale()}}
  context.contentResolver.registerContentObserver(Settings.Global.getUriFor(Settings.Global.ANIMATOR_DURATION_SCALE),false,observer)
  val listener=AccessibilityManager.TouchExplorationStateChangeListener {exploration=it}
  manager.addTouchExplorationStateChangeListener(listener)
  onDispose {context.contentResolver.unregisterContentObserver(observer);manager.removeTouchExplorationStateChangeListener(listener)}
 }
 return BannerPolicy.animate(userReduced,systemScale,exploration,state.isAtLeast(Lifecycle.State.RESUMED))
}
