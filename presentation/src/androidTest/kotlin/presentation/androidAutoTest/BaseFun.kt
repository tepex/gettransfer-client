package com.kg.gettransfer.androidAutoTest

import android.widget.DatePicker

import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.action.ViewActions.swipeUp
import androidx.test.espresso.assertion.ViewAssertions

import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.isRoot
import androidx.test.espresso.matcher.ViewMatchers.withContentDescription
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withClassName

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.NoMatchingViewException
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.contrib.PickerActions

import com.kg.gettransfer.R

import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.equalTo

import presentation.androidAutoTest.WaiteObject.waitId

import java.util.concurrent.TimeUnit

@Suppress("MagicNumber")
object BaseFun {

    fun ViewInteraction.isDisplayed() = try {
        check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        true
    } catch (e: NoMatchingViewException) {
        false
    }

    fun passOnboardingAnyway() {
        if (onView(withId(R.id.btnNext)).isDisplayed()) {
            onView(allOf(withId(R.id.btnClose))).perform(click())
            val baseFun = BaseFun
            baseFun.goTransferLater()
        } else {
            val baseFun = BaseFun
            baseFun.goTransferLater()
        }
    }

    fun goSwitchAgreement() {
        if (onView(allOf(withId(android.R.id.button1), withText("OK"))).isDisplayed()) {
            onView(allOf(withId(android.R.id.button1), withText("OK"))).perform(click())
            onView(withId(R.id.switchAgreement)).perform(click())
            onView(withId(R.id.btnGetOffers)).perform(click())
        }
    }

    fun goTransferLater() {
        Thread.sleep(1_000)
        if (onView(allOf(withId(R.id.tv_transfer_number_rate))).isDisplayed()) {
            onView(withId(R.id.ivClose)).perform(click())
        }
    }

    fun goPassLogin() {
        onView(withId(R.id.nav_settings)).perform(click())
        onView(withId(R.id.titleText)).perform(click())
        if (onView(allOf(withId(R.id.btnLogin))).isDisplayed()) {
            onView(allOf(withId(R.id.fieldText), isDisplayed()))
                .perform(replaceText("i.marchenkov+42@gettransfer.com"), closeSoftKeyboard())

            onView(withId(R.id.etPassword)).perform(click())
            onView(allOf(withId(R.id.etPassword), isDisplayed())).perform(replaceText("3000000"), closeSoftKeyboard())

            onView(withId(R.id.btnLogin)).perform(click())
            val baseFun = BaseFun
            baseFun.goTransferLater()
            onView(withId(R.id.nav_order)).perform(click())
        } else {
            onView(allOf(withContentDescription("Перейти вверх"))).perform(click())
            onView(withId(R.id.nav_order)).perform(click())
        }
    }

    fun unLoginGo() {
        onView(withId(R.id.ivClose)).perform(click())
        onView(withId(R.id.nav_settings)).perform(click())
        onView(withId(R.id.titleText)).perform(click())
        onView(withId(R.id.btnLogout)).perform(click())
    }

    fun chooseData() {
        onView(withId(R.id.transfer_date_time_field)).perform(click())
        onView(withClassName(equalTo(DatePicker::class.java.name))).perform(PickerActions.setDate(2020, 10, 9))
        onView(allOf(withId(android.R.id.button1), withText("ОК"))).perform(click())
        onView(allOf(withId(android.R.id.button1), withText("ОК"))).perform(click())
    }

    fun goCreateTransferForPay() {
        val baseFun = BaseFun
        val searchScreen = SearchScreen()
        searchScreen.addressToTextField.perform(click())
            .perform(replaceText("Санкт-Петербург"))
            .perform(closeSoftKeyboard())
        onView(isRoot()).perform(waitId(R.id.rv_addressList, TimeUnit.SECONDS.toMillis(15)))
        searchScreen.spbAddressItem.perform(click())
        Thread.sleep(800)
        onView(withId(R.id.rvTransferType)).perform(click())
        onView(withId(R.id.rvTransferType)).perform(swipeUp())
        Thread.sleep(500)

        baseFun.chooseData()
        onView(withId(R.id.btnGetOffers)).perform(click())
        baseFun.goSwitchAgreement()

        onView(withId(R.id.rvOffers)).perform(click())
        Thread.sleep(500)
        onView(withId(R.id.btn_book)).perform(click())
        onView(withId(R.id.background)).perform(swipeUp())
    }

    fun checkSuccessfulPayment() {
        onView(allOf(withId(R.id.tvPaidSuccessfully))).isDisplayed()
        onView(allOf(withId(R.id.tvBookingNumber))).isDisplayed()
        onView(allOf(withId(R.id.mapViewRoute))).isDisplayed()
        onView(withId(R.id.mapViewRoute)).perform(swipeUp())
        onView(isRoot()).perform(waitId(R.id.tvRemainTime, TimeUnit.SECONDS.toMillis(15)))
        onView(allOf(withId(R.id.tvRemainTime))).isDisplayed()
    }

    fun openPastTrips() {
        onView(withId(R.id.nav_trips)).perform(click())
        onView(isRoot()).perform(waitId(R.id.vpRequests, TimeUnit.SECONDS.toMillis(15)))
        onView(withId(R.id.vpRequests)).perform(ViewActions.swipeLeft())
        Thread.sleep(500)
        onView(withId(R.id.vpRequests)).perform(click())

        onView(isRoot()).perform(waitId(R.id.tvTransferTime, TimeUnit.SECONDS.toMillis(15)))
        onView(withId(R.id.tvTransferTime)).perform(swipeUp())

        onView(isRoot()).perform(waitId(R.id.transfer_details_main, TimeUnit.SECONDS.toMillis(15)))
        onView(withId(R.id.transfer_details_main)).perform(swipeUp())

        onView(isRoot()).perform(waitId(R.id.flexboxTransportTypes, TimeUnit.SECONDS.toMillis(15)))
    }
}
