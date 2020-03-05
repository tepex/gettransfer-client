package com.kg.gettransfer.presentation.screenelements

import android.R

import com.agoda.kakao.screen.Screen
import com.agoda.kakao.text.KButton
import com.agoda.kakao.text.KTextView

object DialogWindow : Screen<DialogWindow>() {
    val btnOk = KButton {
        withId(R.id.button1)
        withText("OK")
    }
    val msgNoTransport = KTextView { withText(com.kg.gettransfer.R.string.LNG_RIDE_CHOOSE_TRANSPORT) }
    val msgNoTerms = KTextView { withText(com.kg.gettransfer.R.string.LNG_RIDE_OFFERT_ERROR) }
}
