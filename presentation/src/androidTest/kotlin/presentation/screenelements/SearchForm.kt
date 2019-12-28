package com.kg.gettransfer.presentation.screenelements

import com.agoda.kakao.edit.KEditText
import com.agoda.kakao.screen.Screen
import com.agoda.kakao.text.KTextView

import com.kg.gettransfer.R
import com.kg.gettransfer.presentation.data.Constants

object SearchForm : Screen<SearchForm>() {
    val addressFrom = KEditText { withId(R.id.addressField)
        isDescendantOfA { withId(R.id.searchFrom) }
    }
    val mskAddressItem = KTextView {
        withId(R.id.addressItem)
        withText(Constants.TEXT_MOSCOW_SELECT)
        isDisplayed()
    }
    val addressTo = KEditText {
        withId(R.id.addressField)
        isDescendantOfA { withId(R.id.searchTo) }
    }
    val spbAddressItem = KTextView {
        withId(R.id.addressItem)
        withText(Constants.TEXT_PETERSBURG_SELECT)
        isDisplayed() }
    }
