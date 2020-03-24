package com.kg.gettransfer.presentation.screenelements

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import com.agoda.kakao.common.views.KView
import com.agoda.kakao.screen.Screen

import com.kg.gettransfer.R
import com.kg.gettransfer.presentation.data.Constants
import org.hamcrest.Matchers
import org.hamcrest.Matchers.allOf

object OrderScreen : Screen<OrderScreen>() {
    val searchFrom = KView {
        withId(R.id.sub_title)
        withText(R.string.LNG_FIELD_SOURCE_PICKUP)
        isDisplayed()
    }
    val duration = KView {
        withId(R.id.rl_hourly)
    }

    fun goToSearchScreen() {
        Screen.idle(Constants.small)
        onView(withId(R.id.nav_order)).perform(click())
        onView(allOf(withId(R.id.sub_title), withText(R.string.LNG_FIELD_SOURCE_PICKUP))).perform(click())
    }
}
