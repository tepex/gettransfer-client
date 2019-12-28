package presentation.screenelements

import com.agoda.kakao.common.views.KView
import com.agoda.kakao.screen.Screen
import com.kg.gettransfer.R
import com.kg.gettransfer.presentation.data.Constants

object LanguageScreen : Screen<LanguageScreen>()  {
    val tvLanguage = KView {
    withText(Constants.TEXT_RUSSIANLANGUAGE)
    isDisplayed()
    withId(R.id.languageName)
    }
}
