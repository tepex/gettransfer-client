package presentation.screenelements

import com.agoda.kakao.edit.KEditText
import com.agoda.kakao.screen.Screen
import com.agoda.kakao.text.KButton

import com.kg.gettransfer.R


object Profile : Screen<Profile>() {
    val logoutBtn = KButton { withId(R.id.btnLogout) }
    val loginBtn = KButton { withId(R.id.btnLogin) }
    val email = KEditText {
        withId(R.id.fieldText)
        isDescendantOfA { withId(R.id.fieldLayout) }
        isDisplayed()
    }
    val pwd = KEditText { withId(R.id.etPassword) }
    val btnBack = KButton { withContentDescription("Navigate up") }
}
