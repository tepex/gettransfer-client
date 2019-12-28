package com.kg.gettransfer.presentation.screenelements

import com.agoda.kakao.common.views.KSwipeView
import com.agoda.kakao.common.views.KView
import com.agoda.kakao.screen.Screen
import com.agoda.kakao.text.KButton

import com.kg.gettransfer.R

object Payment : Screen<Payment>() {
    val tvPaidSuccessfully = KView { withId(R.id.tvPaidSuccessfully) }
    val tvBookingNumber = KView { withId(R.id.tvBookingNumber) }
    val mapRoute = KView { withId(R.id.mapViewRoute) }
    val mapViewRoute = KSwipeView { withId(R.id.mapViewRoute) }
    val tvRemainTime = KView { withId(R.id.tvRemainTime) }
    val btnSupport = KButton { withId(R.id.btnSupport) }
    val btnTryAgain = KButton { withId(R.id.btnTryAgain) }
    val tvTransferNotPaid = KView { withId(R.id.tvNotPaid) }
    val tvImageFail = KView { withId(R.id.imageError) }
}
