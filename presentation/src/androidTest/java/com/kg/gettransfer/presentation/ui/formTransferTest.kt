package com.kg.gettransfer.presentation.ui

import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintSet.VISIBLE
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.NoMatchingViewException
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
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

@RunWith(AndroidJUnit4::class)
class formTransferTest{
    @Test
    fun inputField (){

        Thread.sleep(7000)

        ActivityScenario.launch(SplashActivity::class.java)

        Thread.sleep(7000)

        passOnboardingAnyway()

        onView(Matchers.allOf(ViewMatchers.withId(R.id.nav_settings))).perform(ViewActions.click())
        onView(Matchers.allOf(ViewMatchers.withId(R.id.settingsProfile))).perform(ViewActions.click())

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

        Thread.sleep(7000)
        val appCompatEditText = onView(
                allOf(withId(R.id.addressField),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.searchFrom),
                                        0),
                                1),
                        isDisplayed()))
        appCompatEditText.perform(click())
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
