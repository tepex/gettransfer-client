package com.kg.gettransfer.presentation.screenelements

import com.agoda.kakao.common.views.KView
import com.agoda.kakao.screen.Screen

import com.kg.gettransfer.R

object NavBar : Screen<NavBar>() {
    val settingsItem = KView { withId(R.id.nav_settings) }
    val orderItem = KView { withId(R.id.nav_order) }
    val trips = KView { withId(R.id.nav_trips) }
    val help = KView { withId(R.id.nav_help) }
    val tripsItem = KView { withId(R.id.nav_trips) }
    val settings = KView { withId(R.id.nav_settings) }
    val order = KView { withId(R.id.nav_order) }
}
