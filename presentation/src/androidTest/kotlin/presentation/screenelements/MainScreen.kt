package presentation.screenelements

import com.agoda.kakao.common.views.KView
import com.agoda.kakao.screen.Screen
import com.agoda.kakao.text.KButton
import com.agoda.kakao.text.KTextView

import com.kg.gettransfer.R

object MainScreen : Screen<MainScreen>() {

    val switchHourly  = KView { withId(R.id.switch_mode_) }
    val next  = KButton { withId(R.id.btnNextFragment) }
    val sub_title = KTextView {
        withId(R.id.sub_title)
        withText("To: airport, train station, city, hotel, other place")
        isDisplayed()
    }
    val bestPriceLogo = KView { withId(R.id.bestPriceLogo) }
    val layoutBestPriceText = KView { withId(R.id.layoutBestPriceText) }
    val btnClose = KView { withId(R.id.btnClose) }
    val read_more_title = KView { withId(R.id.tv_read_more_title) }
}
