package com.kg.gettransfer.presentation.ui

import androidx.constraintlayout.widget.ConstraintSet.VISIBLE
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.kg.gettransfer.R
import org.hamcrest.Matchers
import org.hamcrest.Matchers.allOf
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
        onView(Matchers.allOf(ViewMatchers.withId(R.id.nav_settings))).perform(ViewActions.click())
        Thread.sleep(7000)
//        onView(withId(R.id.settingsProfile))
//                .check(matches(isDisplayed()))
//fieldText
    onView(Matchers.allOf(ViewMatchers.withId(R.id.settingsProfile))).perform(ViewActions.click())
        Thread.sleep(7000)
//        onView(allOf(withText(R.string.LNG_LOGIN_EMAIL_SECTION))).perform(typeText("r.abdullina@gettransfer.com"))
//        onView(allOf(withId(R.id.fieldText))).perform(typeText("r.abdullina@gettransfer.com"))
        onView(allOf(withId(R.id.fieldText), isDisplayed())).perform(click())
        onView(allOf(withId(R.id.fieldText))).perform(typeText("r.abdullina@gettransfer.com"))
        Thread.sleep(7000)
    }
}