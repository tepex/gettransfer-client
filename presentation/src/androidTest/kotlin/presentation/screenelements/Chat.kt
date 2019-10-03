package com.kg.gettransfer.presentation.screenelements

import com.agoda.kakao.edit.KEditText
import com.agoda.kakao.screen.Screen
import com.agoda.kakao.text.KButton

import com.kg.gettransfer.R

object Chat : Screen<Chat>() {

    val btnMessage = KEditText { withId(R.id.messageText) }
    val btnSend = KButton { withId(R.id.btnSend) }
}
