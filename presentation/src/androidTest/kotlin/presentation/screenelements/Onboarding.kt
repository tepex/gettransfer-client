package com.kg.gettransfer.presentation.screenelements

import com.agoda.kakao.common.views.KView
import com.agoda.kakao.image.KImageView
import com.agoda.kakao.screen.Screen

import com.agoda.kakao.text.KButton

import com.kg.gettransfer.R

object Onboarding : Screen<Onboarding>() {
    val btnNext = KButton {
        withId(R.id.btnNext)
    }
    val btnClose = KButton {
        withId(R.id.btnClose)
    }
    val btnOK = KButton {
        withId(R.id.btnNext)
        withText(R.string.LNG_OK)
    }
    val gtrLogo = KImageView {
        withId(R.id.gtrLogo)
    }
}
