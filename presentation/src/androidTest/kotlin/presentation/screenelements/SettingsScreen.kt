package presentation.screenelements

import com.agoda.kakao.check.KCheckBox
import com.agoda.kakao.common.views.KView
import com.agoda.kakao.screen.Screen
import com.agoda.kakao.text.KButton

import com.kg.gettransfer.R

object SettingsScreen : Screen<SettingsScreen>() {
    val tvProfileCell = KView { withId(R.id.settingsProfile) }
    val tvCurrency = KView { withId(R.id.settingsCurrency) }
    val tvLanguage = KView { withId(R.id.settingsLanguage) }
    val tvDistanceUnit = KView { withId(R.id.settingsDistanceUnit) }
    val tvProfile = KView { withId(R.id.settingsProfile) }
    val tvAboutApp = KView { withId(R.id.layoutAboutApp) }
    val tvDistanceSwitcher = KCheckBox {
        withId(R.id.switch_button)
        isDescendantOfA { withId(R.id.settingsDistanceUnit) }
        isDisplayed()
    }
    val tvBtnRequestCode = KButton {withId(R.id.btnRequestCode)}
}
