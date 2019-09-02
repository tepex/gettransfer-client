package com.kg.gettransfer.presentation.ui

import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.TimePicker
import androidx.constraintlayout.widget.ConstraintSet.VISIBLE
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.NoMatchingViewException
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.kg.gettransfer.R
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers
import org.hamcrest.Matchers.allOf
import org.hamcrest.TypeSafeMatcher
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import androidx.test.espresso.contrib.PickerActions
import com.kg.gettransfer.presentation.ui.helpers.DateTimePickerHelper
import kotlinx.android.synthetic.main.search_address.view.*
import org.hamcrest.Matchers.endsWith

//import android.R



@RunWith(AndroidJUnit4::class)
class passenger {
    @Test
    fun passengerCreateTrasfer (){

        Thread.sleep(7000)

        //Launch app
        ActivityScenario.launch(SplashActivity::class.java)

        Thread.sleep(7000)

        //Assertion onboarging exist
        passOnboardingAnyway()

        onView(Matchers.allOf(ViewMatchers.withId(R.id.nav_settings))).perform(ViewActions.click())
        onView(Matchers.allOf(ViewMatchers.withId(R.id.settingsProfile))).perform(ViewActions.click())

        //Assertion user exist
        profileOnboardingAnyway()

        Thread.sleep(700)

        onView(Matchers.allOf(ViewMatchers.withId(R.id.sub_title),withText("Pickup location"),isDisplayed())).perform(ViewActions.click())

        Thread.sleep(700)
        val placeFrom = onView(allOf(withId(R.id.addressField), isDescendantOfA(withId(R.id.searchFrom))))
        Thread.sleep(700)
        placeFrom.perform(ViewActions.replaceText("Moscow"), ViewActions.closeSoftKeyboard())
        Thread.sleep(700)
        val mskAddressItem =   onView(allOf(withId(R.id.addressItem),withText("Moscow, Russia"),isDisplayed()))
        Thread.sleep(700)
        mskAddressItem.perform(click())

        Thread.sleep(700)
        val placeTo = onView(allOf(withId(R.id.addressField), isDescendantOfA(withId(R.id.searchTo))))
        Thread.sleep(700)
        placeTo.perform(ViewActions.replaceText("Saint Petersburg"), ViewActions.closeSoftKeyboard())
        Thread.sleep(2000)
        val spbAddressItem =   onView(allOf(withId(R.id.addressItem),withText("Saint Petersburg, Russia"),isDisplayed()))
        Thread.sleep(2000)
        spbAddressItem.perform(click())
        Thread.sleep(2000)

        onView(withId(R.id.rvTransferType)).perform(ViewActions.swipeUp())
        Thread.sleep(700)

        //Select date
        onView(withId(R.id.transfer_date_time_field)).perform(ViewActions.click())
        onView(withClassName(Matchers.equalTo(DatePicker::class.java.name)))
        .perform(PickerActions.setDate(2019, 9,15))
        Thread.sleep(700)
        val okButton = onView(withId(android.R.id.button1))
        okButton.perform(click())
        Thread.sleep(700)
// The end code select date
// Select time
        onView(withClassName(Matchers.equalTo(TimePicker::class.java.name)))
                .perform(PickerActions.setTime(15,15))
        Thread.sleep(700)
        okButton.perform(click())
        Thread.sleep(700)
// The end code select time

        onView(withId(R.id.img_plus_seat)).perform(ViewActions.click())
        Thread.sleep(700)

        onView(withId(R.id.btnGetOffers)).perform(ViewActions.click())

        //Assertion dialog-window exist
        carOnboardingAnyway ()
        switchOnboardingAnyway()
        Thread.sleep(700)

        val cardOne = onView(withIndex(withId(R.id.tv_car_model_tiny),0))
        val cardTwo = onView(withIndex(withId(R.id.tv_car_model_tiny),1))
        val cardThree = onView(withIndex(withId(R.id.tv_car_model_tiny),2))
        Thread.sleep(700)
        cardOne.perform(click());
        Thread.sleep(700)
        onView(withId(R.id.btn_book)).perform(ViewActions.click())
        Thread.sleep(700)
        onView(withId(R.id.btnBack)).perform(ViewActions.click())
        Thread.sleep(700)

    }

    fun withIndex(matcher: Matcher<View>, index: Int): Matcher<View> {
        return object : TypeSafeMatcher<View>() {
            internal var currentIndex = 0

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

    fun ViewInteraction.isDisplayed(): Boolean {
        try {
            check(matches(ViewMatchers.isDisplayed()))
            return true
        } catch (e: NoMatchingViewException) {
            return false
        }
    }

    fun passOnboardingAnyway () {
        if(onView(withId(R.id.btnNext)).isDisplayed()) {
            //view is displayed logic

            onView(Matchers.allOf(ViewMatchers.withId(R.id.btnNext))).perform(ViewActions.click())
            onView(Matchers.allOf(ViewMatchers.withId(R.id.btnNext))).perform(ViewActions.click())

        } else {

        }
    }
    fun profileOnboardingAnyway () {
        if(onView(withId(R.id.btnLogout)).isDisplayed()) {
            //view is displayed logic

            onView(Matchers.allOf(withContentDescription("Navigate up"))).perform(ViewActions.click())
            onView(Matchers.allOf(ViewMatchers.withId(R.id.nav_order))).perform(ViewActions.click())

        } else {

            val email = onView(allOf(withId(R.id.fieldText), isDescendantOfA(withId(R.id.fieldLayout)), isDisplayed()))

            email.perform(ViewActions.replaceText("mygtracc1@gmail.com"), ViewActions.closeSoftKeyboard())

            Thread.sleep(700)
            onView(allOf(withId(R.id.etPassword))).perform(typeText("PassRR11"))
            onView(Matchers.allOf(ViewMatchers.withId(R.id.btnLogin))).perform(ViewActions.click())

            Thread.sleep(700)

            onView(Matchers.allOf(ViewMatchers.withId(R.id.nav_order))).perform(ViewActions.click())
        }
    }

    fun carOnboardingAnyway () {
        if(onView(withText("Please select transport type")).isDisplayed()) {
            onView(Matchers.allOf(ViewMatchers.withId(android.R.id.button1), withText("OK"))).perform(ViewActions.click())
            Thread.sleep(700)
            //Select car
            onView(withId(R.id.rvTransferType)).perform (ViewActions.click())
            Thread.sleep(700)
            //Click btn
            onView(withId(R.id.btnGetOffers)).perform (ViewActions.click())
            Thread.sleep(700)

        }  else {

        }
    }
       fun switchOnboardingAnyway() {
           if (onView(withText("You should accept terms of use")).isDisplayed()) {
            onView(Matchers.allOf(ViewMatchers.withId(android.R.id.button1), withText("OK"))).perform(ViewActions.click())
            Thread.sleep(700)
            onView(withId(R.id.scrollContent)).perform(ViewActions.swipeUp())
            Thread.sleep(700)
            //Activate switcher
            onView(withId(R.id.switchAgreement)).perform(ViewActions.click())
            Thread.sleep(700)
            onView(withId(R.id.btnGetOffers)).perform(ViewActions.click())
        } else {

        }
    }



}
