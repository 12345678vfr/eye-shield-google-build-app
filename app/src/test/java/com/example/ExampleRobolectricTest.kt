package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.model.FilterProfiles
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Eye Shield", appName)
  }

  @Test
  fun `verify filter profiles exist`() {
    val profiles = FilterProfiles.ALL_PRESETS
    assertEquals(6, profiles.size)
    assertNotNull(FilterProfiles.getById("candlelight"))
  }
}

