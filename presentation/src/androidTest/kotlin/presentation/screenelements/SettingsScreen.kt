package presentation.screenelements

import com.agoda.kakao.common.views.KView
import com.agoda.kakao.screen.Screen

import com.kg.gettransfer.R

object SettingsScreen : Screen<SettingsScreen>() {
    val profileCell = KView { withId(R.id.settingsProfile) }
}
