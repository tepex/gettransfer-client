package com.kg.gettransfer.presentation.androidAutoTest

import android.view.View
import android.widget.DatePicker

import androidx.test.espresso.assertion.ViewAssertions

import androidx.test.espresso.matcher.ViewMatchers

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.NoMatchingViewException
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.contrib.PickerActions
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.matcher.ViewMatchers.*
import com.agoda.kakao.screen.Screen

import com.kg.gettransfer.R
import com.kg.gettransfer.presentation.adapter.PopularAddressAdapter

import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.equalTo
import com.kg.gettransfer.presentation.data.Constants
import com.kg.gettransfer.presentation.screenelements.*
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeMatcher
import presentation.androidAutoTest.Capture
import presentation.androidAutoTest.DateHelper
import presentation.androidAutoTest.NetworkRequests
import presentation.androidAutoTest.RegEx

object BaseFun {

    fun ViewInteraction.isDisplayed() = try {
        check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        true
    } catch (e: NoMatchingViewException) {
        false
    }

    fun goStart() {
        // checkDistanceHasValue onbording
        Screen.idle(Constants.big)
        if (onView(allOf(withId(R.id.btnNext))).isDisplayed()) {
            onView(withId(R.id.btnClose)).perform(click())
        }
        // go transfer later
        Screen.idle(Constants.medium)
        if (onView(allOf(withId(R.id.tv_transfer_number_rate))).isDisplayed()) {
            onView(withId(R.id.ivClose)).perform(click())
        }
        // go app passenger
        Screen.idle(Constants.medium)
        if (onView(allOf(withId(R.id.iv_close))).isDisplayed()) {
            onView(withId(R.id.iv_close)).perform(click())
        }
    }

    fun goSwitchAgreement() {
        if (onView(allOf(withId(android.R.id.button1), withText(Constants.TEXT_OK))).isDisplayed()) {
            onView(allOf(withId(android.R.id.button1), withText(Constants.TEXT_OK))).perform(click())
            onView(withId(R.id.scrollContent)).perform(swipeUp())
            onView(withId(R.id.switchAgreement)).perform(click())
            onView(withId(R.id.btnGetOffers)).perform(click())
        }
    }

    fun switchDistanceOn() {
        onView(withId(R.id.nav_settings)).perform(click())
        SettingsScreen {
            tvDistanceSwitcher {
                setChecked(checked = false)
                click()
            }
        }
    }
    fun switchDistanceOff() {
        onView(withId(R.id.nav_settings)).perform(click())
        SettingsScreen {
            tvDistanceSwitcher {
                setChecked(checked = true)
                click()
            }
        }
    }

    fun checkSwitcherChanging(inputDistanceUnit: Array<String>) {
            NavBar {
                tripsItem { click() }
            }
            TripsScreen {
                recycler {
                    isVisible()
                    firstChild<TripsScreen.Item> {
                        isVisible()
                        distance {
                            isVisible()
                            val distanceCount = Capture.getText(this)
                            RegEx().checkDistanceHasValue(distanceCount, Constants.REGEXDISTANCE, Constants.small)
                            RegEx().checkDistanceHasUnit(
                                distanceCount,
                                inputDistanceUnit,
                                Constants.REGEXKM,
                                Constants.small)
                        }
                        click()
                        goTransfer()
                        content { swipeUp() }
                        distanceOnTransferInfo {
                            isVisible()
                            val distanceCount = Capture.getText(this)
                            RegEx().checkDistanceHasValue(distanceCount, Constants.REGEXDISTANCE, Constants.small)
                            RegEx().checkDistanceHasUnit(
                                distanceCount,
                                inputDistanceUnit,
                                Constants.REGEXKM,
                                Constants.small)
                        }
                        goBackToTrips()
                    }
            }
        }
    }

    fun goBackToTrips() {
        if (onView(allOf(withId(R.id.btnBack))).isDisplayed()) {
            onView(withId(R.id.btnBack)).perform(click())
        if (onView(allOf(withId(R.id.btn_back))).isDisplayed()) {
            onView(withId(R.id.btn_back)).perform(click())
        }
        }
    }

    fun goTransferType() {
        if (onView(allOf(withId(android.R.id.button1), withText(Constants.TEXT_OK))).isDisplayed()) {
            onView(allOf(withId(android.R.id.button1), withText(Constants.TEXT_OK))).perform(click())
            onView(withId(R.id.rvTransferType)).perform(click())
            onView(withId(R.id.btnGetOffers)).perform(click())
        }
    }

    fun goToSearchScreen() {
        Screen.idle(Constants.small)
        onView(withId(R.id.nav_order)).perform(click())
        onView(allOf(withId(R.id.sub_title), withText(R.string.LNG_FIELD_SOURCE_PICKUP))).perform(click())
    }

    fun goTransfer() {
        if (onView(allOf(withId(R.id.btn_request_info))).isDisplayed()) {
            onView(withId(R.id.btn_request_info)).perform(click())
        }
    }

    fun inputAddress() {
        Thread.sleep(Constants.big)
        onView(allOf(withId(R.id.addressField), isDescendantOfA(withId(R.id.searchFrom))))
            .perform(clearText())
        onView(allOf(withId(R.id.addressField), isDescendantOfA(withId(R.id.searchFrom))))
            .perform(typeText(Constants.TEXT_MOSCOW))
        Thread.sleep(Constants.small)
        onView(allOf(withId(R.id.addressItem), withText(Constants.TEXT_MOSCOW_SELECT), isDisplayed()))
            .perform(click())

        onView(allOf(withId(R.id.addressField), isDescendantOfA(withId(R.id.searchTo)))).perform(clearText())
        onView(allOf(withId(R.id.addressField), isDescendantOfA(withId(R.id.searchTo))))
            .perform(typeText(Constants.TEXT_PETERSBURG))
        Thread.sleep(Constants.small)
        onView(allOf(withId(R.id.addressItem), withText(Constants.TEXT_PETERSBURG_SELECT), isDisplayed()))
            .perform(click())
    }

    fun goProfilePartner() {
        onView(withId(R.id.nav_settings)).perform(click())
        onView(withId(R.id.scrollViewSettings)).perform(swipeDown())
        onView(withId(R.id.titleText)).perform(click())
        if (onView(allOf(withId(R.id.btnLogin))).isDisplayed()) {
            onView(allOf(withId(R.id.fieldText), isDisplayed()))
                .perform(replaceText(Constants.TEXT_EMAIL_BALANCE), closeSoftKeyboard())

            onView(withId(R.id.etPassword)).perform(click())
            onView(allOf(withId(R.id.etPassword), isDisplayed()))
                .perform(replaceText(Constants.TEXT_PWD_BALANCE), closeSoftKeyboard())

            onView(withId(R.id.btnLogin)).perform(click())
            goStart()
            onView(withId(R.id.nav_order)).perform(click())
        } else {
            onView(allOf(withId(R.id.toolbar_btnBack))).perform(click())
            onView(withId(R.id.nav_order)).perform(click())
        }
        if (onView(allOf(withId(R.id.ivClock))).isDisplayed()) {
            onView(withId(R.id.ivClock)).perform(click())
        }
        if (onView(allOf(withId(R.id.iv_close))).isDisplayed()) {
            onView(withId(R.id.ivClock)).perform(click())
        }
    }

    fun goProfilePassenger() {
        onView(withId(R.id.nav_settings)).perform(click())
        onView(withId(R.id.scrollViewSettings)).perform(swipeDown())
        onView(withId(R.id.titleText)).perform(click())
        if (onView(allOf(withId(R.id.btnLogin))).isDisplayed()) {
            onView(allOf(withId(R.id.fieldText), isDisplayed()))
                .perform(replaceText(Constants.TEXT_EMAIL_PASSENGER), closeSoftKeyboard())

            onView(withId(R.id.etPassword)).perform(click())
            onView(allOf(withId(R.id.etPassword), isDisplayed()))
                .perform(replaceText(Constants.TEXT_PWD_PASSENGER), closeSoftKeyboard())

            onView(withId(R.id.btnLogin)).perform(click())
            goStart()
        } else {
            onView(allOf(withId(R.id.toolbar_btnBack))).perform(click())
        }
        if (onView(allOf(withId(R.id.ivClock))).isDisplayed()) {
            onView(withId(R.id.ivClock)).perform(click())
        }
        if (onView(allOf(withId(R.id.iv_close))).isDisplayed()) {
            onView(withId(R.id.ivClock)).perform(click())
        }
    }

    fun changeLanguage() {
        Screen.idle(Constants.small)
        onView(withId(R.id.nav_settings)).perform(click())
        onView(withId(R.id.settingsLanguage)).perform(click())
        onView(allOf(withId(R.id.languageName), withText(Constants.TEXT_RUSSIANLANGUAGE))).perform(click())
    }

    fun unLogin() {
        onView(withId(R.id.nav_settings)).perform(click())
        onView(withId(R.id.scrollViewSettings)).perform(swipeDown())
        onView(withId(R.id.titleText)).perform(click())
        if (onView(allOf(withId(R.id.btnLogout))).isDisplayed()) {
            onView(withId(R.id.btnLogout)).perform(click())
            Screen.idle(Constants.medium)
            onView(withId(R.id.titleText)).perform(click())
        }
    }

    fun checkoutDev() {
        onView(withId(R.id.nav_settings)).perform(click())
        onView(withId(R.id.scrollViewSettings)).perform(swipeUp())
        if (onView(allOf(withId(R.id.field_text), withText(R.string.endpoint_demo))).isDisplayed()) {
            onView(allOf(withId(R.id.field_text), withText(R.string.endpoint_demo))).perform(click())
            Screen.idle(Constants.medium)
            onView(withText(R.string.endpoint_dev)).perform(click())
        }
    }
    fun checkoutDemo() {
        onView(withId(R.id.nav_settings)).perform(click())
        onView(withId(R.id.scrollViewSettings)).perform(swipeUp())
        if (onView(allOf(withId(R.id.field_text), withText(R.string.endpoint_dev))).isDisplayed()) {
            onView(allOf(withId(R.id.field_text), withText(R.string.endpoint_dev))).perform(click())
            Screen.idle(Constants.medium)
            onView(withText(R.string.endpoint_demo)).perform(click())
        }
    }

    fun changeEmail(new: String, url: String, api: String) {
        ProfileScreen {
            emailField {   click()  }
        }
        Screen.idle(Constants.medium)
        ChangingEmailScreen {
            emailField { typeText(new) }
            changeBtn { click() }
            Screen.idle(Constants.big)
            val code: String? = RegEx().parse(NetworkRequests().receiveEmail(
                urlChangeEmail = url,
                apiKey = api),
                Constants.REGEXCODE,
                Constants.big
            )
            Screen.idle(Constants.big)
            emailCode {
                code?.let {  typeText(it) }
            }
            doneBtn {
                click()
            }
        }
    }

    fun chooseData() {
        onView(withId(R.id.transfer_date_time_field)).perform(click())
        Screen.idle(Constants.medium)
        val year = DateHelper().dateFormattedYear
        val month = DateHelper().dateFormattedMonth
        val day = DateHelper().dateFormatted4DayForward

        if (year != null && month != null && day != null) {
            onView(withClassName(equalTo(DatePicker::class.java.name)))
                .perform(PickerActions.setDate(year, month, day))
        }

        onView(allOf(withId(android.R.id.button1))).perform(click())
        onView(allOf(withId(android.R.id.button1))).perform(click())
    }

    fun okCancel() {
        onView(allOf(withId(R.id.reasonName), withText(Constants.TEXT_REASON))).perform(click())
        onView(allOf(withId(R.id.btnCancelRequest), withText(Constants.TEXT_CANCEL))).perform(click())
        onView(allOf(withId(R.id.ivClose))).perform(click())
        onView(allOf(withId(R.id.btnBack))).perform(click())
    }

    fun checkTips() {
        onView(allOf(withId(R.id.rv_popularList))).isDisplayed()
        onView(withId(R.id.rv_popularList))
            .perform(actionOnItemAtPosition<PopularAddressAdapter.ViewHolder>(Constants.POSITION_FIRST, click()))
        onView(allOf(withId(R.id.rv_addressList))).isDisplayed()
        onView(withId(R.id.rv_popularList))
            .perform(actionOnItemAtPosition<PopularAddressAdapter.ViewHolder>(Constants.POSITION_SECOND, click()))
        onView(allOf(withId(R.id.rv_addressList))).isDisplayed()
        onView(withId(R.id.rv_popularList))
            .perform(actionOnItemAtPosition<PopularAddressAdapter.ViewHolder>(Constants.POSITION_THIRD, click()))
        onView(allOf(withId(R.id.rv_addressList))).isDisplayed()
    }

    fun withIndex(matcher: Matcher<View>, index: Int): Matcher<View> {
        return object : TypeSafeMatcher<View>() {
            var currentIndex = 0

            override fun describeTo(description: Description) {
                description.appendText("with index: ")
                description.appendValue(index)
                matcher.describeTo(description)
            }

            public override fun matchesSafely(view: View): Boolean {
                return matcher.matches(view) && currentIndex++ == index
            }
        }
    }

    fun openPastTrips() {
        onView(allOf(withId(R.id.nav_trips))).perform(click())
        onView(allOf(withId(R.id.vpRequests))).perform(swipeLeft())
        Thread.sleep(Constants.big)
        onView(allOf(withIndex(withId(R.id.requestInfo), 0),
            isDescendantOfA(withIndex(withId(R.id.rvRequests), 0)),
            isDisplayed()))
            .perform(click())
    }
}
