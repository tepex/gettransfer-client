package com.kg.gettransfer.presentation.screenelements

import com.agoda.kakao.common.views.KSwipeView
import com.agoda.kakao.common.views.KView
import com.agoda.kakao.screen.Screen
import com.agoda.kakao.text.KButton

import com.kg.gettransfer.R

object OrdersDetails : Screen<OrdersDetails>() {
    val content = KSwipeView { withId(R.id.rvTransferType) }
    val plusPassenger = KView { withId(R.id.img_plus_seat) }
    val btnGetOffers = KView { withId(R.id.btnGetOffers) }
    val transportType = KView { withId(R.id.rvTransferType) }
    val bottomContent = KView { withId(R.id.scrollContent) }
    val termsOfUse = KView { withId(R.id.switchAgreement) }
    val btnBack = KButton {
        withId(R.id.btnBack)
        isDisplayed()
    }
}
