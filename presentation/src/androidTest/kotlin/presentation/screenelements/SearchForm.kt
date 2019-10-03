package com.kg.gettransfer.presentation.screenelements

import com.agoda.kakao.edit.KEditText
import com.agoda.kakao.screen.Screen
import com.agoda.kakao.text.KTextView

import com.kg.gettransfer.R

object SearchForm : Screen<SearchForm>() {
    val addressFrom = KEditText { withId(R.id.addressField)
        isDescendantOfA { withId(R.id.searchFrom) }
    }
    val mskAddressItem = KTextView {
        withId(R.id.addressItem)
        withText("Moscow, Russia")
        isDisplayed()
    }
    val addressTo = KEditText {
        withId(R.id.addressField)
        isDescendantOfA { withId(R.id.searchTo) }
    }
    val spbAddressItem = KTextView {
        withId(R.id.addressItem)
        withText("Saint Petersburg, Russia")
        isDisplayed() }
    }
