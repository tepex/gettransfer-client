package com.kg.gettransfer.presentation.screenelements

import com.agoda.kakao.common.views.KView
import com.agoda.kakao.screen.Screen

import com.kg.gettransfer.R

object OrderScreen : Screen<OrderScreen>() {
    val searchFrom = KView {
        withId(R.id.sub_title)
        withText("Pickup location")
        isDisplayed()
    }
}
