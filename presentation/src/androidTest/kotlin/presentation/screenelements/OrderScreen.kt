package com.kg.gettransfer.presentation.screenelements

import com.agoda.kakao.common.views.KView
import com.agoda.kakao.screen.Screen

import com.kg.gettransfer.R

object OrderScreen : Screen<OrderScreen>() {
    val searchFrom = KView {
        withId(R.id.sub_title)
        withText(R.string.LNG_FIELD_SOURCE_PICKUP)
        isDisplayed()
    }
    val duration = KView {
        withId(R.id.rl_hourly)
    }
}
