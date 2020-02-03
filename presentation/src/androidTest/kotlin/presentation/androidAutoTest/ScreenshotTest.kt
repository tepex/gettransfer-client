package com.kg.gettransfer.presentation.androidAutoTest

import android.app.Activity
import android.view.LayoutInflater
import androidx.annotation.LayoutRes

import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import com.facebook.testing.screenshot.Screenshot

import com.facebook.testing.screenshot.ViewHelpers
import com.kg.gettransfer.R

object ScreenshotTest {
    /*
     * Take the actual screenshot. At the end of this call, the screenshot
     * is stored on the device and the gradle plugin takes care of
     * pulling it and displaying it to you in nice ways.
     */
    fun takeScreenshot(@LayoutRes layout: Int) {
        val context = getInstrumentation().targetContext

        context.setTheme(R.style.AppTheme)

        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(layout, null, false)
        ViewHelpers.setupView(view).layout()
        Screenshot.snap(view).record()
    }

    fun takeScreenshot(activity: Activity) {
        Screenshot.snapActivity(activity).record()
    }
}
