package presentation.screenelements

import com.agoda.kakao.edit.KEditText
import com.agoda.kakao.screen.Screen
import com.agoda.kakao.text.KButton
import com.kg.gettransfer.R

object SmsScreen : Screen<SmsScreen>()  {
    val pinText = KEditText { withId(R.id.pinView) }
    val btnDone = KButton {
        withId(R.id.btnDone)
        isDisplayed()
    }
}