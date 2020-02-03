package com.kg.gettransfer.presentation.screenelements

import com.agoda.kakao.edit.KEditText
import com.agoda.kakao.screen.Screen
import com.agoda.kakao.text.KButton
import com.kg.gettransfer.R

object ChangingEmailScreen : Screen<ChangingEmailScreen>() {
    val emailField = KEditText { withId(R.id.fieldText) }
    val changeBtn = KButton { withId(R.id.btnChangeEmail) }
    val doneBtn = KButton { withId(R.id.btnDone) }
    val emailCode = KEditText { withId(R.id.codeView) }
}
