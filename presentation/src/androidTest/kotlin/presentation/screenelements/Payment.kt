package presentation.screenelements

import com.agoda.kakao.common.views.KSwipeView
import com.agoda.kakao.common.views.KView
import com.agoda.kakao.screen.Screen
import com.kg.gettransfer.R

object Payment : Screen<Payment>() {
    val PaidSuccessfully = KView { withId(R.id.tvPaidSuccessfully) }
    val BookingNumber = KView { withId(R.id.tvBookingNumber) }
    val mapRoute = KView { withId(R.id.mapViewRoute) }
    val mapViewRoute = KSwipeView { withId(R.id.mapViewRoute) }
    val tvRemainTime = KView { withId(R.id.tvRemainTime) }
}
