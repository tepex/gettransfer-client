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
import com.kg.gettransfer.presentation.screenelements.OrderScreen

import com.kg.gettransfer.presentation.screenelements.OrdersDetails
import com.kg.gettransfer.presentation.screenelements.SearchForm
import com.kg.gettransfer.presentation.screenelements.SettingsScreen

class DistanceValueCheckTest : TestCase(
    Kaspresso.Builder.default().apply {
        flakySafetyParams.apply {
            timeoutMs = Constants.big
        }
    }) {

    @Rule
    @JvmField
    var activityTestRule = ActivityTestRule(SplashActivity::class.java)

    @Test
    fun testDistanceValueCheck() {
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
            step("Signed in account") {
                    BaseFun.goProfilePassenger()
            }
            SettingsScreen.changeLanguage()
            Screen.idle(Constants.small)
            SettingsScreen.switchDistanceOff()
            BaseFun.checkSwitcherChanging(arrayOf("km"))
            OrderScreen.goToSearchScreen()
            SearchForm.inputAddress()
            Screen.idle(Constants.small)
            OrdersDetails {
                btnBack { flakySafely {  click() } }
            }
            SettingsScreen.switchDistanceOn()
            BaseFun.checkSwitcherChanging(arrayOf("miles"))
            OrderScreen. goToSearchScreen()
            SearchForm.inputAddress()
            Screen.idle(Constants.big)
            OrdersDetails {
                btnBack { click() }
            }
        }
    }
}
