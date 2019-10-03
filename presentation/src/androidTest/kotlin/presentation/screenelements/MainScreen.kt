package com.kg.gettransfer.presentation.screenelements

import com.agoda.kakao.common.views.KSwipeView
import com.agoda.kakao.common.views.KView

import com.agoda.kakao.screen.Screen

import com.agoda.kakao.text.KButton
import com.agoda.kakao.text.KTextView

import com.kg.gettransfer.R

object MainScreen : Screen<MainScreen>() {

    val switchHourly = KView { withId(R.id.switch_mode_) }
    val btnNext = KButton { withId(R.id.btnNextFragment) }
    val subTitle = KTextView {
        withId(R.id.sub_title)
        withText("To: airport, train station, city, hotel, other place")
        isDisplayed()
    }
    val tvBestPriceLogo = KView { withId(R.id.bestPriceLogo) }
    val tvLayoutBestPriceText = KView { withId(R.id.layoutBestPriceText) }
    val btnClose = KView { withId(R.id.btnClose) }
    val tvReadMoreTitle = KView { withId(R.id.tv_read_more_title) }
    val tvSelectFrom = KView { withId(R.id.ivSelectFieldFrom) }
    val tvHourlyWindow = KSwipeView { withId(R.id.np_hours) }
    val tvHourly = KView { withId(R.id.hourly_title) }
    val okBtn = KButton { withId(R.id.tv_okBtn) }
}
