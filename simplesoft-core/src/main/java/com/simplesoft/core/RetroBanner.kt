package com.simplesoft.core

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.*
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext

private val Phosphor=Color(0xFF75F58B)
private val Terminal=Color(0xFF030A05)
@Composable fun rememberBrandMessages(): MessageCatalog {
 val context=LocalContext.current.applicationContext
 val language=LocalConfiguration.current.locales[0].language
 val catalog by produceState(MessageCatalog(),context,language) {
  value=withContext(Dispatchers.IO) {
   val text=runCatching {context.assets.open(BrandMessages.ASSET).use {input->
    val bytes=input.readBytesBounded(BrandMessages.MAX_BYTES+1)
    if(bytes.size>BrandMessages.MAX_BYTES) null else bytes.toString(Charsets.UTF_8)
   }}.getOrNull()
   BrandMessages.parse(text).let {it.copy(messages=it.forLanguage(language))}
  }
 }
 return catalog
}
private fun java.io.InputStream.readBytesBounded(limit: Int): ByteArray {
 val out=java.io.ByteArrayOutputStream();val buffer=ByteArray(4096)
 while(out.size()<limit) {val n=read(buffer,0,minOf(buffer.size,limit-out.size()));if(n<0) break;out.write(buffer,0,n)}
 return out.toByteArray()
}
/** A content boundary only: a future ad implementation must be explicitly supplied by the host. */
@Composable fun SimpleSoftFooter(
 messages: List<BrandMessage>,reduceMotion: Boolean,modifier: Modifier=Modifier,
 replacement: (@Composable ()->Unit)?=null
) {
 if(replacement!=null) Box(modifier) {replacement()}
 else if(messages.isNotEmpty()) RetroMessageBanner(messages,reduceMotion,modifier)
}
@Composable fun RetroMessageBanner(messages: List<BrandMessage>,reduceMotion: Boolean,modifier: Modifier=Modifier) {
 if(messages.isEmpty()) return
 val animate=rememberBrandMotionAllowed(reduceMotion)
 var index by remember(messages) {mutableIntStateOf(0)}
 var width by remember {mutableIntStateOf(0)}
 var x by remember {mutableFloatStateOf(0f)}
 val message=messages[index.coerceIn(messages.indices)]
 val density=LocalDensity.current
 val spacing=with(density) {2.dp.toPx()}*density.fontScale.coerceIn(1f,1.5f)
 val speed=with(density) {BannerPolicy.SCROLL_SPEED_DP_PER_SECOND.dp.toPx()}
 val points=remember(message.text) {DotMatrix.points(message.text)}
 LaunchedEffect(messages,animate,width,spacing) {
  index=0;x=width.toFloat()
  if(!animate || width<=0) return@LaunchedEffect
  var previous=0L
  var lastPaint=0L
  while(isActive) withFrameNanos {frame ->
   if(previous==0L) {previous=frame;lastPaint=frame}
   if(frame-lastPaint>=33_000_000L) {
    x=BannerPolicy.advance(x,speed,(frame-lastPaint)/1_000_000_000f)
    lastPaint=frame
    val currentWidth=DotMatrix.columns(messages[index].text)*spacing
    if(x < -currentWidth) {index=BannerPolicy.next(index,messages.size);x=width.toFloat()}
   }
  }
 }
 val semantics=modifier.clipToBounds().fillMaxWidth().background(Terminal).clearAndSetSemantics {contentDescription=message.text}
 // Unsupported scripts use the platform font rather than showing meaningless glyphs.
 if(!animate || !DotMatrix.supported(message.text)) Text(message.text,color=Phosphor,fontFamily=FontFamily.Monospace,fontSize=13.sp,modifier=semantics.padding(horizontal=16.dp,vertical=10.dp))
 else Canvas(semantics.height(40.dp).onSizeChanged {width=it.width}) {
  val y=(size.height-6*spacing)/2
  points.forEach {(column,row)->
   val px=x+column*spacing
   if(px>=-spacing && px<=size.width+spacing) drawCircle(Phosphor,spacing*0.34f,Offset(px,y+row*spacing))
  }
 }
}
