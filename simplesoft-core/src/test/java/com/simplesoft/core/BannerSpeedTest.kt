package com.simplesoft.core
import org.junit.Assert.*
import org.junit.Test
class BannerSpeedTest {
 @Test fun speedIsFiftyPercentFasterThanAndroid005() {assertEquals(30f*1.5f,BannerPolicy.SCROLL_SPEED_DP_PER_SECOND,0f)}
 @Test fun sameFrameIntervalMovesFiftyPercentFarther() {
  val old=100f-BannerPolicy.advance(100f,30f,0.02f)
  val current=100f-BannerPolicy.advance(100f,BannerPolicy.SCROLL_SPEED_DP_PER_SECOND,0.02f)
  assertEquals(old*1.5f,current,0.00001f)
 }
}
