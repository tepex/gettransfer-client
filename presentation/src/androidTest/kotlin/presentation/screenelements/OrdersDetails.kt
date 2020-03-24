package com.kg.gettransfer.presentation.screenelements

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.NoMatchingViewException
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.swipeUp
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import com.agoda.kakao.common.views.KSwipeView
import com.agoda.kakao.common.views.KView
import com.agoda.kakao.screen.Screen
import com.agoda.kakao.text.KButton

import com.kg.gettransfer.R
import com.kg.gettransfer.presentation.data.Constants
import org.hamcrest.Matchers.allOf

object OrdersDetails : Screen<OrdersDetails>() {
    val content = KSwipeView { withId(R.id.rvTransferType) }
    val plusPassenger = KView { withId(R.id.img_plus_seat) }
    val btnGetOffers = KView { withId(R.id.btnGetOffers) }
    val transportType = KSwipeView { withId(R.id.rvTransferType) }
    val transportTypeBuisness = KView {
        withId(R.id.tvTransferType)
        withText(R.string.LNG_TRANSPORT_BUSINESS)
        isDescendantOfA {
            withId(R.id.cardTransferType)
        }
    }
    val bottomContent = KView { withId(R.id.scrollContent) }
    val termsOfUse = KView { withId(R.id.switchAgreement) }
    val btnBack = KButton {
        withId(R.id.btnBack)
        isDisplayed()
    }

    fun ViewInteraction.isDisplayed() = try {
        check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        true
    } catch (e: NoMatchingViewException) {
        false
    }

    fun goSwitchAgreement() {
        if (onView(allOf(withId(android.R.id.button1), withText(Constants.TEXT_OK))).isDisplayed()) {
            onView(allOf(withId(android.R.id.button1), withText(Constants.TEXT_OK))).perform(click())
            onView(withId(R.id.scrollContent)).perform(swipeUp())
            onView(withId(R.id.switchAgreement)).perform(click())
            onView(withId(R.id.btnGetOffers)).perform(click())
        }
    }

    fun goTransferType() {
        if (onView(allOf(withId(android.R.id.button1), withText(Constants.TEXT_OK))).isDisplayed()) {
            onView(allOf(withId(android.R.id.button1), withText(Constants.TEXT_OK))).perform(click())
            onView(withId(R.id.rvTransferType)).perform(click())
            onView(withId(R.id.btnGetOffers)).perform(click())
        }
    }
}
