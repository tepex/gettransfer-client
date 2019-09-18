package com.kg.gettransfer.presentation

import android.widget.DatePicker
import android.widget.TimePicker

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.NoMatchingViewException
import androidx.test.espresso.ViewInteraction

import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.PickerActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withClassName

import androidx.test.rule.ActivityTestRule

import com.kg.gettransfer.presentation.screenelements.Calendar
import com.kg.gettransfer.presentation.screenelements.DialogWindow
import com.kg.gettransfer.presentation.screenelements.NavBar
import com.kg.gettransfer.presentation.screenelements.OffersScreen
import com.kg.gettransfer.presentation.screenelements.Onboarding
import com.kg.gettransfer.presentation.screenelements.OrderScreen
import com.kg.gettransfer.presentation.screenelements.OrdersDetails
import com.kg.gettransfer.presentation.screenelements.Profile
import com.kg.gettransfer.presentation.screenelements.SatisfactionBox
import com.kg.gettransfer.presentation.screenelements.SearchForm
import com.kg.gettransfer.presentation.screenelements.SettingsScreen

import com.kg.gettransfer.presentation.ui.SplashActivity

import org.junit.Rule
import org.junit.Test

import org.hamcrest.Matchers

@Suppress("MagicNumber")
class Passenger {
    @get:Rule
    var activityRule: ActivityTestRule<SplashActivity> = ActivityTestRule(SplashActivity::class.java)

    val satisfactionBox = SatisfactionBox()
    val onboarding = Onboarding()
    val navBar = NavBar()
    val settingsScr = SettingsScreen()
    val orderSrc = OrderScreen()
    val searchForm = SearchForm()
    val ordersDetails = OrdersDetails()
    val calendar = Calendar()
    val offersScreen = OffersScreen()
    val profile = Profile()
    val dialogWindow = DialogWindow()

    @Test
    fun passengerCreateTrasfer() {
        // Assertion onboarging exist
        passOnboardingAnyway()
        Thread.sleep(27000)

        checkform()

        navBar.settingsItem.perform(ViewActions.click())
        settingsScr.profileCell.perform(click())

        // Assertion user exist
        loginAnyway()
        Thread.sleep(1000)

        orderSrc.searchFrom.perform(ViewActions.click())
        Thread.sleep(700)

        searchForm.addressFrom.perform(ViewActions.replaceText("Moscow"), ViewActions.closeSoftKeyboard())
        Thread.sleep(700)

        searchForm.mskAddressItem.perform(click())
        Thread.sleep(700)

        searchForm.addressTo.perform(ViewActions.replaceText("Saint Petersburg"), ViewActions.closeSoftKeyboard())
        Thread.sleep(2000)

        searchForm.spbAddressItem.perform(click())
        Thread.sleep(2000)

        ordersDetails.content.perform(ViewActions.swipeUp())
        Thread.sleep(700)
        // Select date
        calendar.transferDate.perform(ViewActions.click())
        onView(withClassName(Matchers.equalTo(DatePicker::class.java.name))).perform(PickerActions.setDate(2019, 9, 15))
        Thread.sleep(700)
        calendar.btnOk.perform(click())
        // The end code select date
        Thread.sleep(700)
        // Select time
        onView(withClassName(Matchers.equalTo(TimePicker::class.java.name))).perform(PickerActions.setTime(15, 15))
        Thread.sleep(700)

        calendar.btnOk.perform(click())
        // The end code select time
        Thread.sleep(700)

        ordersDetails.plusPassenger.perform(ViewActions.click())
        Thread.sleep(700)

        ordersDetails.btnGetOffers.perform(ViewActions.click())

        // Assertion dialog-window exist
        carTypeEnableAnyway()
        Thread.sleep(700)
        // Assertion dialog-window exist
        switchEnableAnyway()
        Thread.sleep(700)

        offersScreen.topOffer.perform(click())
        Thread.sleep(700)

        offersScreen.btnBook.perform(ViewActions.click())
        Thread.sleep(700)

        offersScreen.btnBack.perform(ViewActions.click())
        Thread.sleep(700)
    }

    fun ViewInteraction.isDisplayed() = try {
        check(matches(ViewMatchers.isDisplayed()))
        true
    } catch (e: NoMatchingViewException) {
        false
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
            // Go to  order's create
            navBar.orderItem.perform(ViewActions.click())
        } else {
            // Input email
            profile.email.perform(ViewActions.replaceText("mygtracc1@gmail.com"), ViewActions.closeSoftKeyboard())
            Thread.sleep(700)
            // Input password
            profile.pwd.perform(typeText("PassRR11"))
            // Sign in
            profile.login.perform(ViewActions.click())
            Thread.sleep(700)
            // Go to  order's create
            navBar.orderItem.perform(ViewActions.click())
        }
    }

    fun carTypeEnableAnyway() {
        if (dialogWindow.msgNoTransport.isDisplayed()) {
            dialogWindow.btnOk.perform(ViewActions.click())
            Thread.sleep(700)
            // Select car
            ordersDetails.transportType.perform(ViewActions.click())
            Thread.sleep(700)
            // Click btn
            ordersDetails.btnGetOffers.perform(ViewActions.click())
            Thread.sleep(700)
        }
    }

    fun switchEnableAnyway() {
        if (dialogWindow.msgNoTerms.isDisplayed()) {
            dialogWindow.btnOk.perform(ViewActions.click())
            Thread.sleep(700)
            ordersDetails.bottomContent.perform(ViewActions.swipeUp())
            Thread.sleep(700)
            // Activate switcher
            ordersDetails.termsOfUse.perform(ViewActions.click())
            Thread.sleep(700)
            ordersDetails.btnGetOffers.perform(ViewActions.click())
        }
    }

    fun checkform() {
        if (satisfactionBox.satisfaction.isDisplayed()) {
            satisfactionBox.btnClose.perform(click())
        }
    }
}
