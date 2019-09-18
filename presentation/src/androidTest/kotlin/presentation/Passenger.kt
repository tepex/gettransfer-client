package com.kg.gettransfer.presentation

import android.widget.DatePicker
import android.widget.TimePicker

import androidx.test.core.app.ActivityScenario

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.NoMatchingViewException
import androidx.test.espresso.ViewInteraction

import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.PickerActions
import androidx.test.espresso.matcher.ViewMatchers

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

import org.hamcrest.Matchers

import org.junit.Rule
import org.junit.Test

@Suppress("MagicNumber")
class Passenger {
    @Rule
    @JvmField
    var activityTestRule = ActivityTestRule(SplashActivity::class.java)

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
        Thread.sleep(8000)
        // Assertion onboarging exist
        passOnboardingAnyway()
        Thread.sleep(2000)

        checkform()

        navBar.settingsItem.perform(ViewActions.click())
        settingsScr.profileCell.perform(ViewActions.click())

        // Assertion user exist
        loginAnyway()
        Thread.sleep(1000)

        orderSrc.searchFrom.perform(ViewActions.click())
        Thread.sleep(700)

        searchForm.addressFrom.perform(ViewActions.replaceText("Moscow"), ViewActions.closeSoftKeyboard())
        Thread.sleep(700)

        searchForm.mskAddressItem.perform(ViewActions.click())
        Thread.sleep(700)

        searchForm.addressTo.perform(ViewActions.replaceText("Saint Petersburg"), ViewActions.closeSoftKeyboard())
        Thread.sleep(2000)

        searchForm.spbAddressItem.perform(ViewActions.click())
        Thread.sleep(2000)

        ordersDetails.content.perform(ViewActions.swipeUp())
        Thread.sleep(700)
        // Select date
        calendar.transferDate.perform(ViewActions.click())
        onView(withClassName(Matchers.equalTo(DatePicker::class.java.name)))
            .perform(PickerActions.setDate(2019, 10, 15))
        Thread.sleep(700)
        calendar.btnOk.perform(ViewActions.click())
        // The end code select date
        Thread.sleep(700)
        // Select time
        onView(ViewMatchers.withClassName(Matchers.equalTo(TimePicker::class.java.name)))
            .perform(PickerActions.setTime(15, 15))
        Thread.sleep(700)

        calendar.btnOk.perform(ViewActions.click())
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

        offersScreen.topOffer.perform(ViewActions.click())
        Thread.sleep(700)

        offersScreen.btnBook.perform(ViewActions.click())
        Thread.sleep(700)

        offersScreen.btnBack.perform(ViewActions.click())
        Thread.sleep(700)
    }

    @Test
    fun passengerLoginWithPhone() {
        Thread.sleep(8000)
        passOnboardingAnyway()
        waitElementClick(navBar.settingsItem, 9000)
        waitElementClick(settingsScr.profileCell, 2000)
        if (profile.logout.isDisplayed()) {
            // Go to settings screen
            profile.logout.perform(ViewActions.click())
            // Go to  order's create
            waitElementClick(settingsScr.profileCell, 2000)
            // Input email
            profile.email.perform(ViewActions.replaceText("+79992223838"), ViewActions.closeSoftKeyboard())
            // Input password
            waitElementReplaceTextPwd(profile.pwd, 700, "PassRR11")
            // Sign in
            profile.login.perform(ViewActions.click())
            // Go to  order's create
            waitElementClick(navBar.orderItem, 700)
        } else {
            // Input email
            profile.email.perform(ViewActions.replaceText("+79992223838"), ViewActions.closeSoftKeyboard())
            // Input password
            waitElementReplaceTextPwd(profile.pwd, 700, "PassRR11")
            // Sign in
            profile.login.perform(ViewActions.click())
            // Go to  order's create
            waitElementClick(navBar.orderItem, 700)
        }
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
            satisfactionBox.btnClose.perform(ViewActions.click())
        }
    }

    fun waitElementClick(whatToClick: ViewInteraction, wait: Long) {
        Thread.sleep(wait)
        whatToClick.perform(ViewActions.click())
    }

    fun waitElementReplaceTextPwd(whatToReplaceTextPwd: ViewInteraction, wait: Long, typetxt: String) {
        Thread.sleep(wait)
        whatToReplaceTextPwd.perform(typeText(typetxt))
    }
}
