package com.kg.gettransfer.presentation.screenelements

import com.agoda.kakao.check.KCheckBox
import com.agoda.kakao.common.views.KSwipeView
import com.agoda.kakao.common.views.KView
import com.agoda.kakao.screen.Screen
import com.agoda.kakao.text.KButton
import com.agoda.kakao.text.KTextView

import com.kg.gettransfer.R

object SettingsScreen : Screen<SettingsScreen>() {
    val tvProfileCell = KView {
        withId(R.id.settingsProfile)
        isDisplayed()
    }
    val tvCurrency = KView { withId(R.id.settingsCurrency) }
    val tvLanguage = KView { withId(R.id.settingsLanguage) }
    val tvDistanceUnit = KView { withId(R.id.settingsDistanceUnit) }
    val tvProfile = KView { withId(R.id.settingsProfile) }
    val tvAboutApp = KSwipeView { withId(R.id.scrollViewSettings) }
    val tvDistanceSwitcher = KCheckBox {
        withId(R.id.switch_button)
        isDescendantOfA { withId(R.id.settingsDistanceUnit) }
        isDisplayed()
    }
    var endpointDev = KTextView {
        withId(R.id.field_text)
        withText(R.string.endpoint_demo)
    }
    var endpointDemo = KTextView {
        withId(R.id.field_text)
        withText(R.string.endpoint_dev)
    }
    var checkoutDev = KTextView {
        withText(R.string.endpoint_dev)
    }
    var checkoutDemo = KTextView {
        withText(R.string.endpoint_demo)
    }
}
