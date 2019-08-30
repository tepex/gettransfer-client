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

//import android.R



@RunWith(AndroidJUnit4::class)
class formTransferTest{
    @Test
    fun inputField (){

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

        Thread.sleep(7000)

        onView(Matchers.allOf(ViewMatchers.withId(R.id.sub_title),withText("Pickup location"),isDisplayed())).perform(ViewActions.click())

        Thread.sleep(7000)

        val appCompatEditText3 = onView(
                allOf(withId(R.id.addressField),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.searchFrom),
                                        0),
                                1),
                        isDisplayed()))
        appCompatEditText3.perform(click()).perform(ViewActions.replaceText("Moscow"), ViewActions.closeSoftKeyboard())

        Thread.sleep(7000)
        val linearLayout = onView(
                allOf(childAtPosition(
                        allOf(withId(R.id.rv_addressList),
                                childAtPosition(
                                        withClassName(Matchers.`is`("android.widget.LinearLayout")),
                                        1)),
                        0),
                        isDisplayed()))
        linearLayout.perform(click())

        val appCompatEditText4 = onView(
                allOf(withId(R.id.addressField),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.searchTo),
                                        0),
                                1),
                        isDisplayed()))
        appCompatEditText4
                .perform(click())
                .perform(ViewActions.replaceText("Saint Petersburg"))
        Thread.sleep(7000)
        val linearLayout2 = onView(
                allOf(childAtPosition(
                        allOf(withId(R.id.rv_addressList),
                                childAtPosition(
                                        withClassName(Matchers.`is`("android.widget.LinearLayout")),
                                        1)),
                        0),
                        isDisplayed()))
        linearLayout2.perform(click())

        Thread.sleep(7000)
        onView(withId(R.id.rvTransferType)).perform(ViewActions.swipeUp())
        Thread.sleep(500)

//Select date
        onView(withId(R.id.transfer_date_time_field)).perform(ViewActions.click())
    onView(withClassName(Matchers.equalTo(DatePicker::class.java.name)))
        .perform(PickerActions.setDate(2019, 9,15))
        Thread.sleep(7000)

        val appCompatButton = onView(
                allOf(withId(android.R.id.button1), withText("OK"),
                        childAtPosition(
                                allOf(withClassName(Matchers.`is`("com.android.internal.widget.ButtonBarLayout")),
                                        childAtPosition(
                                                withClassName(Matchers.`is`("android.widget.LinearLayout")),
                                                3)),
                                3),
                        isDisplayed()))
        appCompatButton.perform(click())
        Thread.sleep(7000)
// The end code select date
// Select time
        onView(withClassName(Matchers.equalTo(TimePicker::class.java.name)))
                .perform(PickerActions.setTime(15,15))
        Thread.sleep(5000)
        val appCompatButton2 = onView(
                allOf(withId(android.R.id.button1), withText("OK"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(Matchers.`is`("android.widget.LinearLayout")),
                                        0),
                                2),
                        isDisplayed()))
        appCompatButton2.perform(click())
        Thread.sleep(2000)
// The end code select time

        onView(withId(R.id.img_plus_seat)).perform(ViewActions.click())
        Thread.sleep(2000)

        onView(withId(R.id.btnGetOffers)).perform(ViewActions.click())

        //Assertion dialog-window exist
        carOnboardingAnyway ()
        switchOnboardingAnyway()
        Thread.sleep(7000)

        val cardOne = onView(withIndex(withId(R.id.tv_car_model_tiny),0))
        val cardTwo = onView(withIndex(withId(R.id.tv_car_model_tiny),1))
        val cardThree = onView(withIndex(withId(R.id.tv_car_model_tiny),2))
        Thread.sleep(500)
        cardOne.perform(click());
        Thread.sleep(7000)
        onView(withId(R.id.btn_book)).perform(ViewActions.click())
        Thread.sleep(7000)
        onView(withId(R.id.btnBack)).perform(ViewActions.click())
        Thread.sleep(7000)

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
            val appCompatEditText2 = onView(
                    allOf(withId(R.id.fieldText),
                            childAtPosition(
                                    childAtPosition(
                                            withId(R.id.fieldLayout),
                                            0),
                                    0),
                            isDisplayed()))
            appCompatEditText2.perform(ViewActions.replaceText("mygtracc1@gmail.com"), ViewActions.closeSoftKeyboard())

            onView(allOf(withId(R.id.etPassword))).perform(typeText("PassRR11"))
            onView(Matchers.allOf(ViewMatchers.withId(R.id.btnLogin))).perform(ViewActions.click())

            Thread.sleep(7000)

            onView(Matchers.allOf(ViewMatchers.withId(R.id.nav_order))).perform(ViewActions.click())
        }
    }

    fun carOnboardingAnyway () {
        if(onView(withText("Please select transport type")).isDisplayed()) {
            onView(Matchers.allOf(ViewMatchers.withId(android.R.id.button1), withText("OK"))).perform(ViewActions.click())
            Thread.sleep(7000)
            //Select car
            onView(withId(R.id.rvTransferType)).perform (ViewActions.click())
            Thread.sleep(7000)
            //Click btn
            onView(withId(R.id.btnGetOffers)).perform (ViewActions.click())
            Thread.sleep(7000)

        }  else {

        }}
       fun switchOnboardingAnyway() {
           if (onView(withText("You should accept terms of use")).isDisplayed()) {
            onView(Matchers.allOf(ViewMatchers.withId(android.R.id.button1), withText("OK"))).perform(ViewActions.click())
            Thread.sleep(7000)
            onView(withId(R.id.scrollContent)).perform(ViewActions.swipeUp())
            Thread.sleep(2000)
            //Activate switcher
            onView(withId(R.id.switchAgreement)).perform(ViewActions.click())
            Thread.sleep(7000)
            onView(withId(R.id.btnGetOffers)).perform(ViewActions.click())
        } else {

        }
    }

    private fun childAtPosition(
            parentMatcher: Matcher<View>, position: Int): Matcher<View> {

        return object : TypeSafeMatcher<View>() {
            override fun describeTo(description: Description) {
                description.appendText("Child at position $position in parent ")
                parentMatcher.describeTo(description)
            }

            public override fun matchesSafely(view: View): Boolean {
                val parent = view.parent
                return parent is ViewGroup && parentMatcher.matches(parent)
                        && view == parent.getChildAt(position)
            }
        }
    }

}
