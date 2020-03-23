package com.kg.gettransfer.presentation.screenelements

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.action.ViewActions.clearText
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.isDescendantOfA
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withText
import com.agoda.kakao.edit.KEditText
import com.agoda.kakao.screen.Screen
import com.agoda.kakao.text.KTextView

import com.kg.gettransfer.R
import com.kg.gettransfer.presentation.adapter.PopularAddressAdapter
import com.kg.gettransfer.presentation.androidAutoTest.BaseFun.isDisplayed
import com.kg.gettransfer.presentation.data.Constants
import org.hamcrest.Matchers.allOf

object SearchForm : Screen<SearchForm>() {
    val addressFrom = KEditText { withId(R.id.addressField)
        isDescendantOfA { withId(R.id.searchFrom) }
    }
    val mskAddressItem = KTextView {
        withId(R.id.addressItem)
        withText(Constants.TEXT_MOSCOW_SELECT)
        isDisplayed()
    }
    val addressTo = KEditText {
        withId(R.id.addressField)
        isDescendantOfA { withId(R.id.searchTo) }
    }
    val spbAddressItem = KTextView {
        withId(R.id.addressItem)
        withText(Constants.TEXT_PETERSBURG_SELECT)
        isDisplayed()
    }
    fun inputAddress() {
        Thread.sleep(Constants.big)
        onView(allOf(withId(R.id.addressField), isDescendantOfA(withId(R.id.searchFrom))))
                .perform(clearText())
        onView(allOf(withId(R.id.addressField), isDescendantOfA(withId(R.id.searchFrom))))
                .perform(typeText(Constants.TEXT_MOSCOW))
        Thread.sleep(Constants.small)
        onView(allOf(withId(R.id.addressItem), withText(Constants.TEXT_MOSCOW_SELECT), isDisplayed()))
                .perform(click())

        onView(allOf(withId(R.id.addressField), isDescendantOfA(withId(R.id.searchTo)))).perform(clearText())
        onView(allOf(withId(R.id.addressField), isDescendantOfA(withId(R.id.searchTo))))
                .perform(typeText(Constants.TEXT_PETERSBURG))
        Thread.sleep(Constants.small)
        onView(allOf(withId(R.id.addressItem), withText(Constants.TEXT_PETERSBURG_SELECT), isDisplayed()))
                .perform(click())
    }

    fun  checkTips() {
        for (pos in 1..Constants.POSITION_THIRD) {
            onView(allOf(withId(R.id.rv_popularList))).isDisplayed()
            onView(withId(R.id.rv_popularList))
                    .perform(RecyclerViewActions.actionOnItemAtPosition<PopularAddressAdapter.ViewHolder>(pos, click()))
        }
    }
}
