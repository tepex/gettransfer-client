package presentation.screenelements

import com.agoda.kakao.common.views.KSwipeView
import com.agoda.kakao.edit.KEditText
import com.agoda.kakao.screen.Screen
import com.agoda.kakao.text.KTextView
import com.kg.gettransfer.R

object Locations : Screen<Locations>()  {
    val searchTo  = KEditText { withId(R.id.addressField)
        isDescendantOfA { withId(R.id.searchTo) } }
    val spdAddress = KTextView {
        withId(R.id.addressItem)
        withText("Saint Petersburg, Россия")
        isDisplayed()
    }
    val PointOnMap = KTextView {
        withId(R.id.popular_title)
        withText("Point on map")
        isDisplayed()
    }
    val Airport = KTextView {
        withId(R.id.popular_title)
        withText("Airport")
        isDisplayed()
    }
    val Station = KTextView {
        withId(R.id.popular_title)
        withText("Station")
        isDisplayed()
    }
    val Map = KSwipeView {
        withId(R.id.popular_title)
        withText("Point on map")
        isDisplayed()
    }
    val Hotel = KTextView {
        withId(R.id.popular_title)
        withText("Hotel")
        isDisplayed()
    }
}
