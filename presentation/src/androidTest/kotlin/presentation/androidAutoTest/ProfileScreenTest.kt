package com.kg.gettransfer.presentation

import androidx.test.rule.ActivityTestRule

import com.agoda.kakao.screen.Screen

import com.kaspersky.kaspresso.kaspresso.Kaspresso
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase

import com.kg.gettransfer.presentation.ui.SplashActivity

import org.junit.Rule
import org.junit.Test

import com.kg.gettransfer.presentation.data.Constants
import com.kg.gettransfer.presentation.screenelements.ChangingEmailScreen
import com.kg.gettransfer.presentation.screenelements.ChangingPasswordScreen
import com.kg.gettransfer.presentation.screenelements.NavBar
import com.kg.gettransfer.presentation.screenelements.Onboarding
import com.kg.gettransfer.presentation.screenelements.ProfileScreen
import com.kg.gettransfer.presentation.screenelements.SettingsScreen
import org.junit.Ignore

import presentation.androidAutoTest.NetworkRequests

@Suppress("LongMethod")
class ProfileScreenTest : TestCase(Kaspresso.Builder.default().apply {
     flakySafetyParams.apply {
        timeoutMs = Constants.big
    }
})  {

    @Rule
    @JvmField
    var activityTestRule = ActivityTestRule(SplashActivity::class.java)

    @Test
    fun testProfileScreen() {
        before {
        }.after {
            NetworkRequests().emptyInbox(Constants.TEXT_URL_EMPTY_INBOX, Constants.TEXT_API_CHANGE_EMAIL)
            NetworkRequests().emptyInbox(Constants.TEXT_URL_EMPTY_INBOX_SIM, Constants.TEXT_API_EMAIL_SIM)
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
                    tvProfileCell { flakySafely {  click() } }
                }
            }
            ProfileScreen {
                compose {
                    or(logoutBtn) { flakySafely { click() } }
                    or(email) { typeText(Constants.TEXT_CHANGE_EMAIL_SIM) }
                }
                compose {
                    or(tvPwd) { typeText(Constants.TEXT_CHANGE_PASSWORD_SIM) }
                    or(SettingsScreen.tvProfileCell) { isVisible() }
                }
                compose {
                    or(loginBtn) { click() }
                    or(SettingsScreen.tvProfileCell) { flakySafely { click() } }
                }
                compose {
                    or(NavBar.settingsItem) { click() }
                    or(email) { typeText(Constants.TEXT_CHANGE_EMAIL_SIM) }
                }
                compose {
                    or(NavBar.settingsItem) { click() }
                    or(tvPwd) { typeText(Constants.TEXT_CHANGE_PASSWORD_SIM) }
                }
                compose {
                    or(loginBtn) { click() }
                    or(NavBar.settingsItem) { click() }
                }
            }
            SettingsScreen {
                tvProfileCell { flakySafely { click() } }
            }
            ProfileScreen {
                nameField {
                    click()
                    clearText()
                    typeText(Constants.TEXT_NEW_NAME_SIM)
                }
                saveBtn { click() }
                passswordField {
                    click() }
            }
            ChangingPasswordScreen {
                newPwd { flakySafely { typeText(Constants.TEXT_CHANGE_PASSWORD_SIM) } }
                repeatPwd { flakySafely { typeText(Constants.TEXT_CHANGE_PASSWORD_SIM) } }
                doneBtn { flakySafely { click() } }
            }
            ProfileScreen {
                emailField { click() }
            }
            ChangingEmailScreen {
                emailField { typeText(Constants.TEXT_NEW_EMAIL_SIM) }
                changeBtn { click() }
                Screen.idle(Constants.big)
                Screen.idle(Constants.big)
                Screen.idle(Constants.big)
                val code: String? = parseEmailCode(NetworkRequests().receiveEmail(
                    urlChangeEmail = Constants.TEXT_URL_CHANGE_EMAIL,
                    apiKey = Constants.TEXT_API_CHANGE_EMAIL)
                )

                emailCode { code?.let { typeText(it) } }
                changeBtn { flakySafely { click() } }
            }
            ProfileScreen {
                emailField { flakySafely { click() } }
            }
            ChangingEmailScreen {
                emailField { flakySafely { typeText(Constants.TEXT_CHANGE_EMAIL_SIM) } }
                changeBtn { flakySafely { click() } }
                Screen.idle(Constants.big)
                Screen.idle(Constants.big)
                Screen.idle(Constants.big)
                val codeSim: String? = parseEmailCode(NetworkRequests().receiveEmail(
                    urlChangeEmail = Constants.TEXT_URL_EMAIL_SIM,
                    apiKey = Constants.TEXT_API_EMAIL_SIM))
                emailCode { flakySafely { codeSim?.let { typeText(it) } } }
                changeBtn { flakySafely { click() } }
            }
        }
    }

    fun parseEmailCode(map: CharSequence): String? {
        Screen.idle(Constants.big)
        val regexcode = Regex(Constants.REGEXCODE)
        val matchercode = regexcode.find(map)
        val codeSim = matchercode?.value
        return codeSim
    }
}
