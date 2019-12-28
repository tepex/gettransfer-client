package com.kg.gettransfer.presentation.screenelements

import android.view.View

import com.agoda.kakao.common.views.KSwipeView
import com.agoda.kakao.common.views.KView

import com.agoda.kakao.recycler.KRecyclerItem
import com.agoda.kakao.recycler.KRecyclerView

import com.agoda.kakao.screen.Screen

import com.agoda.kakao.text.KButton
import com.agoda.kakao.text.KTextView

import com.kg.gettransfer.R

import org.hamcrest.Matcher

object TripsScreen  : Screen<TripsScreen>() {
    val requests = KView { withId(R.id.rvRequests) }
    val requestStatus = KTextView { withId(R.id.tvTransferRequestStatus) }
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
    val tvChat = KView {
        withId(R.id.btnImg)
        isDescendantOfA { withId(R.id.btnChat) }
        isDisplayed()
    }
    val upcomingTripCell = KView { withId(R.id.requestInfo)
        isDisplayed()
    }
    val nxt = KView {
        withId(R.id.chevron)
        isDescendantOfA { withId(R.id.requestInfo) }
        isDisplayed()
    }
    val past = KView { withContentDescription("Past") }
    val btnInfo = KButton { withId(R.id.btn_request_info) }
    val btnBackToTrips = KButton {
        withId(R.id.btn_back)
        isDisplayed()
    }

    val contentList = KSwipeView { withId(R.id.vpRequests) }
    val recycler: KRecyclerView = KRecyclerView({
        withId(R.id.rvRequests)
        isDescendantOfA { withId(R.id.vpRequests) }
        isDisplayed()
    }, itemTypeBuilder = { itemType(TripsScreen::Item) })

    class Item(parent: Matcher<View>) : KRecyclerItem<Item>(parent) {
        val distance: KTextView = KTextView(parent) { withId(R.id.tvDistance) }
        val distance1: KTextView = KTextView(parent) { withId(R.id.tvDistance) }
        val distanceOnTransferInfo = KTextView {
            withId(R.id.tv_value)
            isDescendantOfA { withId(R.id.distance_view) }
        }
        val content = KSwipeView { withId(R.id.transfer_details_header) }
        val btnBack = KButton {
            withId(R.id.btnBack)
            isDisplayed()
        }
    }
}
