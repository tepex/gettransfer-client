package presentation.screenelements

import com.agoda.kakao.common.views.KSwipeView
import com.agoda.kakao.common.views.KView
import com.agoda.kakao.screen.Screen
import com.agoda.kakao.text.KButton
import com.kg.gettransfer.R

object BookNow : Screen<BookNow>() {

    val driversCount  = KView { withId(R.id.tv_drivers_count) }
    val clock  = KView { withId(R.id.ivClock) }
    val tvWait  = KView { withId(R.id.tvWait) }
    val rvOffers = KView { withId(R.id.rvOffers) }
    val btn_book = KView { withId(R.id.btn_book) }
    val background = KSwipeView { withId(R.id.background) }
    val Balance = KView { withId(R.id.layoutBalance) }
    val Card = KView { withId(R.id.layoutCard) }
    val payment = KView { withId(R.id.btnGetPayment) }
    val request_info = KButton { withId(R.id.btn_request_info) }
}
