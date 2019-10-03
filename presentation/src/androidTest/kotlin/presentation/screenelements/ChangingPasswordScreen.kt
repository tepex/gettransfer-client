package com.kg.gettransfer.presentation.screenelements

import com.agoda.kakao.edit.KEditText
import com.agoda.kakao.screen.Screen
import com.agoda.kakao.text.KButton
import com.kg.gettransfer.R

object ChangingPasswordScreen : Screen<ChangingPasswordScreen>() {
    val newPwd = KEditText {
        withId(R.id.etPassword)
        isDescendantOfA { (withId(R.id.newPasswordLayout)) }
    }
    val repeatPwd = KEditText {
        withId(R.id.etPassword)
        isDescendantOfA { (withId(R.id.repeatNewPasswordLayout)) }
    }
    val doneBtn = KButton { withId(R.id.btnSave) }
}