package presentation.androidAutoTest

import androidx.test.rule.ActivityTestRule

import com.agoda.kakao.screen.Screen

import com.kaspersky.kaspresso.kaspresso.Kaspresso
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import com.kg.gettransfer.presentation.androidAutoTest.BaseFun

import com.kg.gettransfer.presentation.ui.SplashActivity

import org.junit.Rule
import org.junit.Test
import com.kg.gettransfer.presentation.data.Constants
import com.kg.gettransfer.presentation.screenelements.NavBar
import com.kg.gettransfer.presentation.screenelements.ProfileScreen
import com.kg.gettransfer.presentation.screenelements.SettingsScreen
import com.kg.gettransfer.presentation.screenelements.SmsScreen

class PassengerLoginWithEmailCodeTest  : TestCase(Kaspresso.Builder.default().apply {
    flakySafetyParams.apply {
        timeoutMs = Constants.big
    }
})  {
    @Rule
    @JvmField
    var activityTestRule = ActivityTestRule(SplashActivity::class.java)

    @Test
    fun testPassengerLoginWithEmailCode() {
        before {
        }.after {
        }.run {
            step("Open Main Screen") {
                BaseFun.goStart()            }
            step(" Checkout DEV ") {
                SettingsScreen.checkoutDev()
            }
            Screen.idle(Constants.small)
            NavBar {
                settingsItem { click() }
            }
            Screen.idle(Constants.small)
            BaseFun.unLogin()
            ProfileScreen {
                email { typeText(Constants.TEXT_EMAIL_MAILSLURP) }
                tvBtnRequestCode { click() }
            }
            Screen.idle(Constants.big)
            SmsScreen {
                pinText { click() }
                val s: String? = RegEx().parse(NetworkRequests().receiveEmail(
                    Constants.HTTP_REQUEST, Constants.API_KEY), Constants.REGEX4LOGIN, Constants.medium)
                pinText { s?.let { typeText(it) } }
                btnDone { click() }
            }
            SettingsScreen {
                tvProfileCell {
                    flakySafely { isVisible() }
                }
            }
        }
    }
}
