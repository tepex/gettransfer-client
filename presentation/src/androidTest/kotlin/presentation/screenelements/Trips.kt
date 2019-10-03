package presentation.screenelements

import com.agoda.kakao.common.views.KSwipeView
import com.agoda.kakao.common.views.KView
import com.agoda.kakao.screen.Screen
import com.agoda.kakao.text.KButton

import com.kg.gettransfer.R

object Trips : Screen<Trips>() {

    val requests  = KView { withId(R.id.vpRequests) }
    val swRequests  = KSwipeView { withId(R.id.vpRequests) }
    val transferDetails = KSwipeView { withId(R.id.transfer_details_header) }
    val bookNowInfo = KSwipeView { withId(R.id.tv_bookNow_info) }
    val transfer_details_main  = KView { withId(R.id.transfer_details_main) }
    val bottomCommunicationButtons  = KView { withId(R.id.bottomCommunicationButtons) }
    val transferTime = KSwipeView { withId(R.id.tvTransferTime) }
    val transfer = KSwipeView { withId(R.id.transfer_details_main) }
    val flexboxTransportTypes  = KView { withId(R.id.flexboxTransportTypes) }
    val topCommunicationButtons  = KView { withId(R.id.topCommunicationButtons) }
    val cancel = KButton {
        withId(R.id.btnName)
        withText("Cancel\nrequest")
        isDisplayed()
    }
    val repeat = KButton {
        withId(R.id.btnName)
        withText("Repeat\ntransfer")
        isDisplayed()
    }
    val requestNumber = KView {
        withId(R.id.tvTransferRequestNumber)
        withText("Transfer request #8789")
        isDisplayed()
    }
    val chat = KView {
        withId(R.id.btnName)
        withText("Chat")
        isDisplayed()
    }
}
