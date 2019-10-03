package presentation.screenelements

import com.agoda.kakao.check.KCheckBox
import com.agoda.kakao.common.views.KView
import com.agoda.kakao.screen.Screen

import com.kg.gettransfer.R

object SettingsScreen : Screen<SettingsScreen>() {
    val profileCell = KView { withId(R.id.settingsProfile) }
    val currency = KView { withId(R.id.settingsCurrency) }
    val language = KView { withId(R.id.settingsLanguage) }
    val distanceUnit = KView { withId(R.id.settingsDistanceUnit) }
    val profile = KView { withId(R.id.settingsProfile) }
    val aboutApp = KView { withId(R.id.layoutAboutApp) }
    val distanceSwitcher = KCheckBox {
        withId(R.id.switch_button)
        isDescendantOfA { withId(R.id.settingsDistanceUnit) }
        isDisplayed()
    }
}
