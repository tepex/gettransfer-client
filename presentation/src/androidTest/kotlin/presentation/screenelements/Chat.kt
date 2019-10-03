package presentation.screenelements

import com.agoda.kakao.edit.KEditText
import com.agoda.kakao.screen.Screen
import com.agoda.kakao.text.KButton

import com.kg.gettransfer.R

object Chat : Screen<Chat>() {

    val message = KEditText { withId(R.id.messageText) }
    val send = KButton { withId(R.id.btnSend) }
}
