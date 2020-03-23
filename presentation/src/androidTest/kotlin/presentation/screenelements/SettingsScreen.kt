package com.kg.gettransfer.presentation.screenelements

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.swipeUp
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import com.agoda.kakao.check.KCheckBox
import com.agoda.kakao.common.views.KSwipeView
import com.agoda.kakao.common.views.KView
import com.agoda.kakao.screen.Screen
import com.agoda.kakao.text.KTextView

import com.kg.gettransfer.R
import com.kg.gettransfer.presentation.androidAutoTest.BaseFun.isDisplayed
import com.kg.gettransfer.presentation.data.Constants
import org.hamcrest.Matchers.allOf

object SettingsScreen : Screen<SettingsScreen>() {
    val tvProfileCell = KView {
        withId(R.id.settingsProfile)
        isDisplayed()
    }
    val tvCurrency = KView { withId(R.id.settingsCurrency) }
    val tvLanguage = KView { withId(R.id.settingsLanguage) }
    val tvDistanceUnit = KView { withId(R.id.settingsDistanceUnit) }
    val tvProfile = KView { withId(R.id.settingsProfile) }
    val tvAboutApp = KSwipeView { withId(R.id.scrollViewSettings) }
    val tvDistanceSwitcher = KCheckBox {
        withId(R.id.switch_button)
        isDescendantOfA { withId(R.id.settingsDistanceUnit) }
        isDisplayed()
    }
    var endpointDev = KTextView {
        withId(R.id.field_text)
        withText(R.string.endpoint_demo)
    }
    var endpointDemo = KTextView {
        withId(R.id.field_text)
        withText(R.string.endpoint_dev)
    }
    var checkoutDev = KTextView {
        withText(R.string.endpoint_dev)
    }
    var checkoutDemo = KTextView {
        withText(R.string.endpoint_demo)
    }

    fun changeLanguage() {
        Screen.idle(Constants.small)
        onView(withId(R.id.nav_settings)).perform(click())
        onView(withId(R.id.settingsLanguage)).perform(click())
        onView(allOf(withId(R.id.languageName), withText(Constants.TEXT_RUSSIANLANGUAGE))).perform(click())
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
}
