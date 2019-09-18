package com.kg.gettransfer.androidAutoTest

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.swipeUp
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.isRoot

import androidx.test.rule.ActivityTestRule
import androidx.test.rule.GrantPermissionRule

import com.kg.gettransfer.R
import com.kg.gettransfer.androidAutoTest.BaseFun.isDisplayed
import com.kg.gettransfer.presentation.ui.SplashActivity

import java.util.concurrent.TimeUnit

import org.hamcrest.Matchers.allOf

import org.junit.Rule
import org.junit.Test

import presentation.androidAutoTest.WaitObject.waitId

@Suppress("MagicNumber")
class TransferCreateSmokeSuite {

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
    fun createHourlyTransfer() {
        val baseFun = BaseFun
        Thread.sleep(1_000)

        baseFun.passOnboardingAnyway()
        baseFun.goTransferLater()
        onView(withId(R.id.switch_mode_)).perform(click())
        onView(withId(R.id.btnNextFragment)).perform(click())
        onView(withId(R.id.rvTransferType)).perform(click())
        onView(withId(R.id.rvTransferType)).perform(swipeUp())
        Thread.sleep(500)

        baseFun.chooseData()
        onView(withId(R.id.btnGetOffers)).perform(click())
        baseFun.goSwitchAgreement()
        onView(allOf(withId(R.id.tv_drivers_count))).isDisplayed()
        onView(allOf(withId(R.id.ivClock))).isDisplayed()
        onView(allOf(withId(R.id.tvWait))).isDisplayed()
    }

    @Test
    fun payTransferBalance() {
        val baseFun = BaseFun
        val searchScreen = SearchScreen()
        Thread.sleep(1_000)

        baseFun.passOnboardingAnyway()
        baseFun.goPassLogin()
        searchScreen.addressGo.perform(click())
        onView(isRoot()).perform(waitId(R.id.search, TimeUnit.SECONDS.toMillis(15)))

        baseFun.goCreateTransferForPay()
        onView(withId(R.id.layoutBalance)).perform(click())
        onView(withId(R.id.btnGetPayment)).perform(click())
        baseFun.checkSuccessfulPayment()
    }

    @Test
    fun payTransferCard() {
        val baseFun = BaseFun
        val payFun = PayFun
        val searchScreen = SearchScreen()
        Thread.sleep(1_000)

        baseFun.passOnboardingAnyway()
        baseFun.goPassLogin()
        searchScreen.addressGo.perform(click())
        onView(isRoot()).perform(waitId(R.id.search, TimeUnit.SECONDS.toMillis(15)))

        baseFun.goCreateTransferForPay()
        onView(withId(R.id.layoutCard)).perform(click())
        onView(withId(R.id.btnGetPayment)).perform(click())
        Thread.sleep(1_000)

        payFun.goPayCard()
        Thread.sleep(5_000)

        baseFun.checkSuccessfulPayment()
    }

    @Test
    fun checkTrips() {
        val baseFun = BaseFun
        Thread.sleep(1_000)

        baseFun.passOnboardingAnyway()
        baseFun.goPassLogin()
        onView(withId(R.id.nav_trips)).perform(click())
        Thread.sleep(1_000)

        onView(withId(R.id.vpRequests)).perform(click())
        Thread.sleep(1_000)

        onView(withId(R.id.transfer_details_header)).perform(swipeUp())
        Thread.sleep(1_000)

        onView(withId(R.id.tv_bookNow_info)).perform(swipeUp())
        Thread.sleep(500)

        onView(allOf(withId(R.id.transfer_details_main))).isDisplayed()
        onView(allOf(withId(R.id.bottomCommunicationButtons))).isDisplayed()
    }

    @Test
    fun checkHelp() {
        val baseFun = BaseFun
        Thread.sleep(1_000)

        baseFun.passOnboardingAnyway()
        Thread.sleep(500)

        onView(withId(R.id.nav_help)).perform(click())
        onView(allOf(withId(R.id.tvWeSpeak))).isDisplayed()
        onView(allOf(withId(R.id.aboutUs))).isDisplayed()
        onView(allOf(withId(R.id.becomeCarrier))).isDisplayed()
        onView(allOf(withId(R.id.tvMessageUs))).isDisplayed()
        onView(allOf(withId(R.id.ourLanguages))).isDisplayed()
        onView(allOf(withId(R.id.socialNetwork))).isDisplayed()
        onView(allOf(withId(R.id.socialNetwork))).isDisplayed()
        onView(withId(R.id.ourLanguages)).perform(swipeUp())
        Thread.sleep(500)

        onView(allOf(withId(R.id.tvWriteUs))).isDisplayed()
        onView(allOf(withId(R.id.include))).isDisplayed()
        onView(withId(R.id.socialNetwork)).perform(swipeUp())
        Thread.sleep(500)

        onView(allOf(withId(R.id.fabEmail))).isDisplayed()
        onView(allOf(withId(R.id.tvCallUs))).isDisplayed()
    }

    @Test
    fun checkSettings() {
        val baseFun = BaseFun
        Thread.sleep(1_000)

        baseFun.passOnboardingAnyway()
        Thread.sleep(500)

        onView(withId(R.id.nav_settings)).perform(click())
        onView(allOf(withId(R.id.settingsCurrency))).isDisplayed()
        onView(allOf(withId(R.id.settingsLanguage))).isDisplayed()
        onView(allOf(withId(R.id.settingsDistanceUnit))).isDisplayed()
        onView(allOf(withId(R.id.settingsProfile))).isDisplayed()
        onView(allOf(withId(R.id.layoutAboutApp))).isDisplayed()
    }
}
