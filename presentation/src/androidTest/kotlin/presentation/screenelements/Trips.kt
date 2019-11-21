package com.kg.gettransfer.presentation.screenelements

import com.agoda.kakao.common.views.KSwipeView
import com.agoda.kakao.common.views.KView

import com.agoda.kakao.screen.Screen

import com.kg.gettransfer.R

object Trips : Screen<Trips>() {

    val requests = KView { withId(R.id.vpRequests) }
    val swRequests = KSwipeView { withId(R.id.vpRequests) }
    val transferDetails = KSwipeView { withId(R.id.transfer_details_header) }
    val bookNowInfo = KSwipeView { withId(R.id.tv_bookNow_info) }
    val tvDetailsMain = KView { withId(R.id.transfer_details_main) }
    val btnCommunication = KView { withId(R.id.bottomCommunicationButtons) }
    val transferTime = KSwipeView { withId(R.id.tvTransferTime) }
    val tvTransfer = KSwipeView { withId(R.id.transfer_details_main) }
    val tvTransportTypes = KView { withId(R.id.flexboxTransportTypes) }
    val topCommunicationButtons = KView { withId(R.id.topCommunicationButtons) }
    val tvCancel = KView {
        withId(R.id.btnImg)
        isDescendantOfA { withId(R.id.btnCancel) }
        isDisplayed()
    }
    val tvRepeat = KView {
        withId(R.id.btnImg)
        isDescendantOfA { withId(R.id.btnRepeatTransfer) }
        isDisplayed()
    }
    val tvRequestNumber = KView {
        withId(R.id.tvTransferRequestNumber)
        withText("Transfer request #8789")
        isDisplayed()
    }
    val tvChat = KView {
        withId(R.id.btnImg)
        isDescendantOfA { withId(R.id.btnChat) }
        isDisplayed() }
}
