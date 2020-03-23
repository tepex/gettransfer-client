package com.kg.gettransfer.presentation

import androidx.test.rule.ActivityTestRule

import com.agoda.kakao.screen.Screen

import com.kaspersky.kaspresso.kaspresso.Kaspresso
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import com.kg.gettransfer.presentation.androidAutoTest.BaseFun

import com.kg.gettransfer.presentation.ui.SplashActivity

import org.junit.Rule
import org.junit.Test

import com.kg.gettransfer.presentation.data.Constants
import com.kg.gettransfer.presentation.screenelements.ChangingPasswordScreen
import com.kg.gettransfer.presentation.screenelements.ProfileScreen
import com.kg.gettransfer.presentation.screenelements.SettingsScreen

import presentation.androidAutoTest.NetworkRequests

class ProfileScreenTest : TestCase(
    Kaspresso.Builder.default().apply {
        flakySafetyParams.apply {
            timeoutMs = Constants.big
        }
    }) {

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
            step("Open Main Screen") {
                BaseFun.goStart()
            }
            step(" Checkout DEV ") {
                SettingsScreen.checkoutDev()
            }
            Screen.idle(Constants.small)
            BaseFun.unLogin()
            step("Log in ") {
                ProfileScreen {
                    email { typeText(Constants.TEXT_CHANGE_EMAIL_SIM) }
                    tvPwd { typeText(Constants.TEXT_CHANGE_PASSWORD_SIM) }
                    loginBtn { click() }
                }
            }
            SettingsScreen {
                tvProfileCell { flakySafely { click() } }
            }
            Screen.idle(Constants.medium)
            ProfileScreen {
                nameField {
                    click()
                    clearText()
                    typeText(Constants.TEXT_NEW_NAME_SIM)
                }
                saveBtn { click() }
                passswordField { click() }
            }
            ChangingPasswordScreen {
                newPwd { flakySafely { typeText(Constants.TEXT_CHANGE_PASSWORD_SIM) } }
                repeatPwd { flakySafely { typeText(Constants.TEXT_CHANGE_PASSWORD_SIM) } }
                doneBtn { flakySafely { click() } }
            }
            BaseFun.changeEmail(
                Constants.TEXT_NEW_EMAIL_SIM,
                Constants.TEXT_URL_CHANGE_EMAIL,
                Constants.TEXT_API_CHANGE_EMAIL)
            Screen.idle(Constants.medium)
            BaseFun.changeEmail(
                Constants.TEXT_CHANGE_EMAIL_SIM,
                Constants.TEXT_URL_EMAIL_SIM,
                Constants.TEXT_API_EMAIL_SIM)
            Screen.idle(Constants.medium)
            ProfileScreen {
                emailField {   isVisible()  }
            }
        }
    }
}
