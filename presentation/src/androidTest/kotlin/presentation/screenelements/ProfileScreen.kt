package presentation.screenelements

import com.agoda.kakao.common.views.KSwipeView
import com.agoda.kakao.common.views.KView
import com.agoda.kakao.edit.KEditText
import com.agoda.kakao.screen.Screen
import com.agoda.kakao.text.KButton

import com.kg.gettransfer.R

object ProfileScreen : Screen<ProfileScreen>() {
    val logoutBtn = KButton { withId(R.id.btnLogout) }
    val loginBtn = KButton { withId(R.id.btnLogin) }
    val profile = KView { withId(R.id.settingsProfile) }
    val dialogImage = KView { withId(R.id.bottomSheetDialogImage) }
    val dialogTitle = KView { withId(R.id.bottomSheetDialogTitle) }
    val dialogDetail = KView { withId(R.id.bottomSheetDialogDetail) }
    val email = KEditText {
        withId(R.id.fieldText)
        isDescendantOfA { withId(R.id.fieldLayout) }
        isDisplayed()
    }
    val tvPwd = KEditText { withId(R.id.etPassword) }
    val btnBack = KButton { withContentDescription("Navigate up") }
    val btnDialogOkButton = KButton { withId(R.id.bottomSheetDialogOkButton) }
    val btnSwitch = KButton { withId(R.id.switchAgreementTb) }
    val btnSignUp = KButton { withId(R.id.btnSignUp) }
    val loginPager = KSwipeView { withId(R.id.loginPager) }
    }
    val tvName = KEditText {
        withId(R.id.fieldText)
        isDescendantOfA { withId(R.id.nameLayout) }
        isDisplayed() }
    val tvPhone = KEditText {
        withId(R.id.fieldText)
        isDescendantOfA { withId(R.id.phoneLayout) }
        isDisplayed() }
    val tvEmailTo = KEditText {
        withId(R.id.fieldText)
        isDescendantOfA { withId(R.id.emailLayout) }
        isDisplayed()
}
