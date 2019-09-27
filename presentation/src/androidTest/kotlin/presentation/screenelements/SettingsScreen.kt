package presentation.screenelements

import com.agoda.kakao.common.views.KView
import com.agoda.kakao.screen.Screen

import com.kg.gettransfer.R

object SettingsScreen : Screen<SettingsScreen>() {
    val profileCell = KView { withId(R.id.settingsProfile) }
    val Currency = KView { withId(R.id.settingsCurrency) }
    val Language = KView { withId(R.id.settingsLanguage) }
    val DistanceUnit = KView { withId(R.id.settingsDistanceUnit) }
    val Profile = KView { withId(R.id.settingsProfile) }
    val AboutApp = KView { withId(R.id.layoutAboutApp) }
}
