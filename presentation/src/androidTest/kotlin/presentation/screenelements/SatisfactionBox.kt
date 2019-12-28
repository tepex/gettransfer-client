package com.kg.gettransfer.presentation.screenelements

import com.agoda.kakao.common.views.KView
import com.agoda.kakao.screen.Screen
import com.agoda.kakao.text.KButton

import com.kg.gettransfer.R

object SatisfactionBox : Screen<SatisfactionBox>() {
    val satisfaction = KView { withId(R.id.design_bottom_sheet) }
    val btnClose = KButton { withId(R.id.ivClose) }
}
