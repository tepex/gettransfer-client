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
import com.kg.gettransfer.presentation.screenelements.OffersScreen
import com.kg.gettransfer.presentation.screenelements.SearchForm
import com.kg.gettransfer.presentation.screenelements.SettingsScreen
import com.kg.gettransfer.presentation.screenelements.OrdersDetails
import com.kg.gettransfer.presentation.screenelements.OrderScreen

class PassengerCreateTransferTest : TestCase(
    Kaspresso.Builder.default().apply {
        flakySafetyParams.apply {
            timeoutMs = Constants.big
        }
    }
) {
    @Rule
    @JvmField
    var activityTestRule = ActivityTestRule(SplashActivity::class.java)

    @Test
    fun testPassengerCreateTransfer() {
        before {
        }.after {
        }.run {
            step("Open Screen") {
                BaseFun.goStart()            }
            step(" Checkout DEV ") {
                SettingsScreen.checkoutDev()
            }
            Screen.idle(Constants.small)
            step("Signed in account") {
                BaseFun.goProfilePassenger()
            }
            SettingsScreen.changeLanguage()
            OrderScreen.goToSearchScreen()
            SearchForm.inputAddress()
            step("Create order") {
                OrdersDetails {
                    content { swipeUp() }
                    Thread.sleep(Constants.medium)
                    BaseFun.chooseDate()
                    plusPassenger { click() }
                    btnGetOffers { click() }
                    OrdersDetails.goTransferType()
                    OrdersDetails.goSwitchAgreement()
                }
            }
            Screen.idle(Constants.medium)
            OffersScreen {
                recycler {
                    isVisible()
                    firstChild<OffersScreen.Item> {
                        isVisible()
                        click()
                        btnBook { click() }
                    }
                }
            }
        }
    }
}
