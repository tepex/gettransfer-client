package com.kg.gettransfer.presentation.screenelements

import com.agoda.kakao.common.views.KSwipeView
import com.agoda.kakao.common.views.KView

import com.agoda.kakao.screen.Screen
import com.agoda.kakao.text.KButton

import com.kg.gettransfer.R

object BookNow : Screen<BookNow>() {

    val tvDriversCount = KView { withId(R.id.tv_drivers_count) }
    val tvClock = KView { withId(R.id.ivClock) }
    val tvWait = KView { withId(R.id.tvWait) }
    val rvOffers = KView { withId(R.id.rvOffers) }
    val btnBook = KView { withId(R.id.btn_book) }
    val tvBackground = KSwipeView { withId(R.id.background) }
    val tvBalance = KView { withId(R.id.layoutBalance) }
    val tvCard = KView { withId(R.id.layoutCard) }
    val tvPayment = KView { withId(R.id.btnGetPayment) }
    val btnRequestInfo = KButton { withId(R.id.btn_request_info) }
}
