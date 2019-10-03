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
    val msgNoTransport = KTextView { withText("Please select transport type") }
    val msgNoTerms = KTextView { withText("You should accept terms of use") }
}
