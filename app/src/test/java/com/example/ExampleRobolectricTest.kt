package com.example

import android.content.Context
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performScrollToIndex
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @get:Rule
  val composeTestRule = createAndroidComposeRule<MainActivity>()

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Artemis Resource Navigator", appName)
  }

  @Test
  fun `verify MainActivity displays root UI and content`() {
    assertNotNull(composeTestRule.activity)
    composeTestRule.waitForIdle()
  }

  @Test
  fun `verify interactive polar map zoom, reset, and layer switching`() {
    composeTestRule.onNodeWithTag("nav_map").performClick()
    composeTestRule.waitForIdle()

    composeTestRule.onNodeWithTag("lunar_map_screen").assertIsDisplayed()
    composeTestRule.onNodeWithTag("interactive_polar_map_canvas").assertIsDisplayed()
    composeTestRule.onNodeWithText("ZOOM 1.0x").assertIsDisplayed()

    // Tap Zoom In and verify zoom level updates from 1.0x
    composeTestRule.onNodeWithTag("map_zoom_in_button").performScrollTo().performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithText("1.0x").assertDoesNotExist()

    // Tap Center on Selected Site and verify docked bar displays 2.6x
    composeTestRule.onNodeWithTag("map_center_site_button").performScrollTo().performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithText("2.6x").assertIsDisplayed()

    // Tap Reset View and verify zoom returns to 1.0x
    composeTestRule.onNodeWithTag("map_reset_view_button").performScrollTo().performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithText("1.0x").assertIsDisplayed()

    // Scroll back to layer tabs, switch to Neutron Hydrogen, and verify overlay chip remains active
    composeTestRule.onNodeWithTag("lunar_map_screen").performScrollToIndex(1)
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithText("Neutron Hydrogen").performScrollTo().performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithText("Routes").assertExists()
  }
}
