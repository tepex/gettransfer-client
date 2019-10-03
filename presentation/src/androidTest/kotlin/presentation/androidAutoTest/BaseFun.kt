package com.kg.gettransfer.androidAutoTest

import android.widget.DatePicker

import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.assertion.ViewAssertions

import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withContentDescription
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withClassName

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.NoMatchingViewException
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.contrib.PickerActions

import com.kg.gettransfer.R

import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.equalTo

@Suppress("MagicNumber")
object BaseFun {

    fun ViewInteraction.isDisplayed() = try {
        check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        true
    } catch (e: NoMatchingViewException) {
        false
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

    fun chooseData() {
        onView(withId(R.id.transfer_date_time_field)).perform(click())
        onView(withClassName(equalTo(DatePicker::class.java.name))).perform(PickerActions.setDate(2020, 10, 9))
        onView(allOf(withId(android.R.id.button1), withText("ОК"))).perform(click())
        onView(allOf(withId(android.R.id.button1), withText("ОК"))).perform(click())
    }

    fun okCancel() {
        onView(allOf(withId(android.R.id.button1), withText("YES"))).perform(click())
    }

    fun okCancel() {
        onView(allOf(withId(android.R.id.button1), withText("YES"))).perform(click())
    }
}
