package presentation.screenelements

import com.agoda.kakao.common.views.KView
import com.agoda.kakao.screen.Screen
import com.kg.gettransfer.R

object NavBar : Screen<NavBar>() {

    val settings = KView { withId(R.id.nav_settings) }
    val order = KView { withId(R.id.nav_order) }
    val tripsItem = KView { withId(R.id.nav_order) }
}
