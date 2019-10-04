package presentation.screenelements

import com.agoda.kakao.common.views.KSwipeView
import com.agoda.kakao.common.views.KView
import com.agoda.kakao.screen.Screen

import com.agoda.kakao.text.KButton

import com.kg.gettransfer.R

object TransferDetails : Screen<TransferDetails>() {

    val typeCar = KView { withId(R.id.rvTransferType) }
    val getOffers = KButton { withId(R.id.btnGetOffers) }
    val typeCars = KSwipeView { withId(R.id.rvTransferType) }
}
