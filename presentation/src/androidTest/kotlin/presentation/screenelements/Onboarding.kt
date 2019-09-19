package presentation.screenelements

import com.agoda.kakao.screen.Screen
import com.agoda.kakao.text.KButton

import com.kg.gettransfer.R

object Onboarding  : Screen<Onboarding>() {
    val btnNext = KButton { withId(R.id.btnNext) }
}
