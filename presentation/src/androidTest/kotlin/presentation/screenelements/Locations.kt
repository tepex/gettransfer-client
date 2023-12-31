package com.kg.gettransfer.presentation.screenelements

import com.agoda.kakao.common.views.KView

import com.agoda.kakao.edit.KEditText
import com.agoda.kakao.screen.Screen

import com.agoda.kakao.text.KButton
import com.agoda.kakao.text.KTextView

import com.kg.gettransfer.R
import com.kg.gettransfer.presentation.data.Constants

object Locations : Screen<Locations>()  {
    val tvSearchTo = KEditText { withId(R.id.addressField)
        isDescendantOfA { withId(R.id.searchTo) } }
    val tvSpdAddress = KTextView {
        withId(R.id.addressItem)
        withText(Constants.TEXT_PETERSBURG_SELECT)
        isDisplayed()
    }
    val tvSearchPanel = KView { withId(R.id.search_panel) }
    val btnNext = KButton { withId(R.id.btnNext) }
    val btnNoChangeLng = KButton {
        withId(android.R.id.button2)
        withText("NO")
    }
}
