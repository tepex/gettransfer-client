package presentation.androidAutoTest

import androidx.test.rule.ActivityTestRule

import com.agoda.kakao.screen.Screen

import com.kaspersky.kaspresso.kaspresso.Kaspresso
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase

import com.kg.gettransfer.presentation.ui.SplashActivity

import okhttp3.OkHttpClient
import okhttp3.Request

import org.junit.Rule
import org.junit.Test
import com.kg.gettransfer.presentation.data.Constants
import com.kg.gettransfer.presentation.screenelements.NavBar
import com.kg.gettransfer.presentation.screenelements.Onboarding
import com.kg.gettransfer.presentation.screenelements.ProfileScreen
import com.kg.gettransfer.presentation.screenelements.SettingsScreen
import com.kg.gettransfer.presentation.screenelements.SmsScreen

class PassengerLoginWithEmailCodeTest  : TestCase(Kaspresso.Builder.default().apply {
    flakySafetyParams = flakySafetyParams.apply {
        timeoutMs = Constants.big
    }
})  {
    @Rule
    @JvmField
    var activityTestRule = ActivityTestRule(SplashActivity::class.java)

    @Test
    @Suppress("LongMethod")
    fun testPassengerLoginWithEmailCode() {
        before {
        }.after {
        }.run {
            step("Open order screen") {
                Onboarding {
                    compose {
                        or(btnNext) { click() }
                        or(NavBar.orderItem) { click() }
                    }
                    compose {
                        or(btnNext) { click() }
                        or(NavBar.orderItem) { click() }
                    }
                }
            }
            step("Go to settings ") {
                NavBar {
                    settingsItem { click() }
                }
            }
            step("Go to login screen ") {
                SettingsScreen {
                    tvProfileCell {
                        click()
                    }
                }
            }
            ProfileScreen {
                compose {
                    or(logoutBtn) { click() }
                    or(email) { isVisible() }
                }
                compose {
                    or(SettingsScreen.tvProfileCell) { click() }
                    or(email) { isVisible() }
                }

                compose {
                    or(email) { typeText(Constants.TEXT_EMAIL_MAILSLURP) }
                }
            }
            SettingsScreen {
                tvBtnRequestCode { click() }
            }
            SmsScreen {
                pinText { click() }
                val s: String? = parse()
                pinText { s?.let { typeText(it) } }
                btnDone { click() }
            }
        }
    }

    fun parse(): String? {
        Screen.idle(Constants.medium)
        val currEmail = receiveEmail()
        val regexcode = Regex("""(\d){4}(?=\\r\\n\\t\</p>)""")
        val matchercode = regexcode.find(currEmail)
        val code = matchercode?.value
        return code
    }

    @Suppress("UnsafeCallOnNullableType")
    fun receiveEmail(): CharSequence {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url(Constants.HTTP_REQUEST)
            .get()
            .addHeader("accept", "application/json")
            .addHeader("x-api-key", Constants.API_KEY)
            .build()
        val response = client.newCall(request).execute()
        val converted = response.body()!!.string()
        return converted
    }
}
