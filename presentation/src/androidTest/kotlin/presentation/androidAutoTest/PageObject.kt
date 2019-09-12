package com.kg.gettransfer.androidAutoTest

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.matcher.ViewMatchers.*

import com.kg.gettransfer.R

import org.hamcrest.Matchers.allOf

data class MainScreen(
    val addressFromTextField: ViewInteraction =
        onView(allOf(withId(R.id.sub_title), withText("Pickup location"), isDisplayed()))
)

data class SearchScreen(
    val clearBtnAtField: ViewInteraction = onView(allOf(withId(R.id.im_clearBtn), isDisplayed())),
    val addressToTextField: ViewInteraction = onView(allOf(withId(R.id.addressField), withHint("Where to?"))),
    val spbAddressItem: ViewInteraction =
        onView(allOf(withId(R.id.addressItem), withText("Санкт-Петербург, Россия"), isDisplayed())),
    val addressGo: ViewInteraction =
        onView(
            allOf(
                withId(R.id.sub_title),
                withText("To: airport, train station, city, hotel, other place"),
                isDisplayed()
            )
        )
)
