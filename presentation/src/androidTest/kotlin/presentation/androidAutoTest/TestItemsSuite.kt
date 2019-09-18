package com.kg.gettransfer.androidAutoTest

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.withId

import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.matcher.ViewMatchers.isRoot

import androidx.test.rule.ActivityTestRule
import androidx.test.rule.GrantPermissionRule

import com.kg.gettransfer.R
import com.kg.gettransfer.androidAutoTest.BaseFun.isDisplayed
import com.kg.gettransfer.presentation.adapter.PopularAddressAdapter
import com.kg.gettransfer.presentation.ui.SplashActivity

import java.util.concurrent.TimeUnit

import org.hamcrest.Matchers.allOf

import org.junit.Rule
import org.junit.Test

import presentation.androidAutoTest.WaiteObject.waitId

@Suppress("MagicNumber")
class TestItemsSuite {

    @Rule
    @JvmField
    var activityTestRule = ActivityTestRule(SplashActivity::class.java)

    @get:Rule
    val screenshotTestRule = ScreenshotTestRule()

    @Rule
    @JvmField
    var grantPermissionRule = GrantPermissionRule.grant(
        "android.permission.ACCESS_FINE_LOCATION",
        "android.permission.ACCESS_COARSE_LOCATION"
    )

    @Test
    fun checkPastTrips() {
        val baseFun = BaseFun
        baseFun.passOnboardingAnyway()
        baseFun.goPassLogin()
        baseFun.openPastTrips()
        onView(allOf(withId(R.id.flexboxTransportTypes))).isDisplayed()
        onView(allOf(withId(R.id.topCommunicationButtons))).isDisplayed()
    }

    @Test
    fun checkHelpWindows() {
        Thread.sleep(1_000)
        val baseFun = BaseFun
        baseFun.passOnboardingAnyway()
        onView(withId(R.id.nav_help)).perform(click())
        onView(withId(R.id.aboutUs)).perform(click())
        onView(allOf(withId(R.id.viewpager))).isDisplayed()

        onView(withId(R.id.btnNext)).perform(click())
        onView(allOf(withId(R.id.viewpager))).isDisplayed()
        onView(withId(R.id.btnNext)).perform(click())
        onView(withId(R.id.becomeCarrier)).perform(click())

        onView(allOf(withId(R.id.tv_title))).isDisplayed()
        onView(allOf(withId(R.id.btn_continue))).isDisplayed()
        onView(withId(R.id.ivBack)).perform(click())
    }

    @Test
    fun checkReadMore() {
        Thread.sleep(1_000)
        val baseFun = BaseFun
        baseFun.passOnboardingAnyway()
        onView(allOf(withId(R.id.bestPriceLogo))).isDisplayed()
        onView(withId(R.id.layoutBestPriceText)).perform(click())
        onView(allOf(withId(R.id.tv_read_more_title))).isDisplayed()
        onView(withId(R.id.btnClose)).perform(click())
    }

    @Test
    fun checkTipsSearch() {
        Thread.sleep(1_000)
        val baseFun = BaseFun
        val searchScreen = SearchScreen()
        baseFun.passOnboardingAnyway()
        searchScreen.addressGo.perform(click())
        onView(isRoot()).perform(waitId(R.id.rv_popularList, TimeUnit.SECONDS.toMillis(15)))
        onView(allOf(withId(R.id.rv_popularList))).isDisplayed()
        onView(withId(R.id.rv_popularList))
            .perform(actionOnItemAtPosition<PopularAddressAdapter.ViewHolder>(1, click()))
        onView(allOf(withId(R.id.rv_addressList))).isDisplayed()

        onView(withId(R.id.rv_popularList))
            .perform(actionOnItemAtPosition<PopularAddressAdapter.ViewHolder>(2, click()))
        onView(allOf(withId(R.id.rv_addressList))).isDisplayed()

        onView(withId(R.id.rv_popularList))
            .perform(actionOnItemAtPosition<PopularAddressAdapter.ViewHolder>(3, click()))
        onView(allOf(withId(R.id.rv_addressList))).isDisplayed()
    }
}
