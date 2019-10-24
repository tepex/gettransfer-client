package com.kg.gettransfer.presentation

import android.view.View
import android.widget.Checkable

import androidx.test.espresso.NoMatchingViewException
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.ViewInteraction

import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.swipeDown
import androidx.test.espresso.action.ViewActions.swipeUp

import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withText

import androidx.test.rule.ActivityTestRule

import com.kg.gettransfer.presentation.screenelements.*
import com.kg.gettransfer.presentation.ui.SplashActivity

import org.hamcrest.BaseMatcher
import org.hamcrest.Description
import org.hamcrest.CoreMatchers.isA

import org.junit.Rule
import org.junit.Test

@Suppress("MagicNumber")
class Distance {

    @get:Rule
    var activityRule: ActivityTestRule<SplashActivity> = ActivityTestRule(SplashActivity::class.java)

    val satisfactionBox = SatisfactionBox()
    val onboarding = Onboarding()
    val navBar = NavBar()
    val settingsScr = SettingsScreen()
    val profile = Profile()
    val tripsScr = TripsScreen()
    val orderSrc = OrderScreen()
    val searchForm = SearchForm()
    val ordersDetails = OrdersDetails()

    @Test
    @Suppress("LongMethod")
    fun distanceCheckValue() {
        Thread.sleep(5000)
        // Assertion onboarging exist
        passOnboardingAnyway()
        Thread.sleep(5000)
        checkform()
        navBar.settingsItem.waitElementClick(5000)
        settingsScr.profileCell.perform(ViewActions.click())
        // Assertion user exist
        loginAnyway()
        Thread.sleep(5000)
        // choose КМ
        settingsScr.distanceSwitcher.perform(click(), setChecked(false))
        navBar.tripsItem.waitElementClick(5000)
        Thread.sleep(14000)
        tripsScr.distance.check(matches(withText("Distance: 712 KM")))
        tripsScr.upcomingTripCell.waitElementClick(2000)
        Thread.sleep(1000)
        tripsScr.content.perform(swipeUp())
        Thread.sleep(1000)
        tripsScr.distanceOnTransferInfo.check(matches(withText("712 KM")))
        tripsScr.btnBack.perform(click())
        Thread.sleep(1000)
        navBar.orderItem.perform(ViewActions.click())
        orderSrc.searchFrom.perform(ViewActions.click())
        Thread.sleep(1000)
        searchForm.addressFrom.perform(ViewActions.replaceText("Moscow"), ViewActions.closeSoftKeyboard())
        Thread.sleep(1000)
        searchForm.mskAddressItem.perform(click())
        Thread.sleep(1000)
        searchForm.addressTo.perform(ViewActions.replaceText("Saint Petersburg"), ViewActions.closeSoftKeyboard())
        Thread.sleep(1000)
        searchForm.spbAddressItem.perform(click())
        Thread.sleep(1000)
        ordersDetails.btnBack.perform(click())
        Thread.sleep(1000)
        navBar.settingsItem.perform(ViewActions.click())
        Thread.sleep(1000)
        // choose MILES
        settingsScr.distanceSwitcher.perform(click(), setChecked(true))
        Thread.sleep(2000)
        navBar.tripsItem.perform(click())
        Thread.sleep(7000)
        tripsScr.contentList.perform(swipeDown())
        Thread.sleep(7000)
        tripsScr.distance.check(matches(withText("Distance: 442 MI")))
        Thread.sleep(7000)
        tripsScr.nxt.perform(click())
        Thread.sleep(7000)
        tripsScr.content.perform(swipeUp())
        Thread.sleep(7000)
        tripsScr.distanceOnTransferInfo.check(matches(withText("442 MI")))
        tripsScr.btnBack.perform(click())
        Thread.sleep(7000)
        navBar.orderItem.perform(ViewActions.click())
        orderSrc.searchFrom.perform(ViewActions.click())
        Thread.sleep(7000)
        searchForm.addressFrom.perform(ViewActions.replaceText("Moscow"), ViewActions.closeSoftKeyboard())
        Thread.sleep(7000)
        searchForm.mskAddressItem.perform(click())
        Thread.sleep(7000)
        searchForm.addressTo.perform(ViewActions.replaceText("Saint Petersburg"), ViewActions.closeSoftKeyboard())
        Thread.sleep(7000)
        searchForm.spbAddressItem.perform(click())
        Thread.sleep(7000)
        ordersDetails.btnBack.perform(click())
        Thread.sleep(7000)
        navBar.settingsItem.perform(ViewActions.click())
        Thread.sleep(7000)
    }

    fun ViewInteraction.isDisplayed() = try {
        check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        true
    } catch (e: NoMatchingViewException) {
        false
    }

    fun setChecked(checked: Boolean) = object : ViewAction {

        override fun getConstraints() = object : BaseMatcher<View>() {

            override fun matches(item: Any) = isA(Checkable::class.java).matches(item)
            override fun describeMismatch(item: Any, mismatchDescription: Description) {}
            override fun describeTo(description: Description) {}
        }

        override fun getDescription(): String? = null

        override fun perform(uiController: UiController, view: View) {
            if (view is Checkable) {
                view.isChecked = checked
            }
        }
    }

    fun passOnboardingAnyway() {
        if (onboarding.btnNext.isDisplayed()) {
            // Go next onboarding
            onboarding.btnNext.perform(ViewActions.click())
            // Click next and go to order's screen
            onboarding.btnNext.perform(ViewActions.click())
        }
    }

    fun loginAnyway() {
        if (profile.logout.isDisplayed()) {
            // Go to settings screen
            profile.btnBack.perform(ViewActions.click())
        } else {
            // Input email
            profile.email.perform(ViewActions.replaceText("mygtracc1@gmail.com"), ViewActions.closeSoftKeyboard())
            Thread.sleep(700)
            // Input password
            profile.pwd.perform(ViewActions.typeText("PassRR11"))
            // Sign in
            profile.login.perform(ViewActions.click())
            Thread.sleep(27000)
            checkform()
        }
    }

    fun checkform() {
        if (satisfactionBox.satisfaction.isDisplayed()) {
            satisfactionBox.btnClose.perform(ViewActions.click())
        }
    }
}
