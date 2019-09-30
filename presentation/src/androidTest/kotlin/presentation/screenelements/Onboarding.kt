package presentation.screenelements

import com.agoda.kakao.common.views.KView
import com.agoda.kakao.screen.Screen

import com.agoda.kakao.text.KButton

import com.kg.gettransfer.R

object Onboarding : Screen<Onboarding>() {
    val btnNext = KButton { withId(R.id.btnNext) }
    val btnClose = KButton { withId(R.id.btnClose) }
    val ivClose = KView { withId(R.id.ivClose) }
    val gtrLogo = KView { withId(R.id.gtrLogo) }
}
