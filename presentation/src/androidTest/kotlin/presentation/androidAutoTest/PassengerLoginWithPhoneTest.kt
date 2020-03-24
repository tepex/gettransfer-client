package com.kg.gettransfer.presentation

import androidx.test.rule.ActivityTestRule
import com.agoda.kakao.screen.Screen
import com.kaspersky.kaspresso.kaspresso.Kaspresso

import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import com.kg.gettransfer.presentation.androidAutoTest.BaseFun

import com.kg.gettransfer.presentation.data.Constants

import com.kg.gettransfer.presentation.ui.SplashActivity

import org.junit.Rule
import org.junit.Test

import com.kg.gettransfer.presentation.screenelements.NavBar
import com.kg.gettransfer.presentation.screenelements.ProfileScreen
import com.kg.gettransfer.presentation.screenelements.SettingsScreen

class PassengerLoginWithPhoneTest : TestCase(Kaspresso.Builder.default().apply {
    flakySafetyParams.apply {
        timeoutMs = Constants.big
    }
})  {
    @Rule
    @JvmField
    var activityTestRule = ActivityTestRule(SplashActivity::class.java)

    @Test
    fun testPassengerLoginWithPhone() {
        before {
        }.after {
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
                    email { typeText(Constants.TEXT_PHONE_PASSENGER) }
                    tvPwd { typeText(Constants.TEXT_PWD_PASSENGER) }
                    loginBtn { click() }
                }
                SettingsScreen {
                    tvProfileCell {
                        flakySafely { isVisible() }
                    }
                }
            }
        }
    }
}
