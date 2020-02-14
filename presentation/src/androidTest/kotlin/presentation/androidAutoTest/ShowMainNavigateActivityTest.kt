package com.kg.gettransfer.presentation.androidAutoTest

import androidx.test.rule.ActivityTestRule
import com.kg.gettransfer.presentation.ui.*
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test

@Ignore
class ShowMainNavigateActivityTest {
    @Rule
    @JvmField
    var activityTestRule = ActivityTestRule(MainNavigateActivity::class.java)

    @Test
    fun testActivityIsShownProperly() {
        ScreenshotTest.takeScreenshot(activityTestRule.activity)
    }
}
