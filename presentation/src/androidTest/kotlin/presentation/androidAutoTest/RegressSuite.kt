package com.kg.gettransfer.presentation.androidAutoTest

import androidx.test.rule.ActivityTestRule
import androidx.test.rule.GrantPermissionRule

import com.agoda.kakao.screen.Screen
import com.kaspersky.kaspresso.kaspresso.Kaspresso

import com.kaspersky.kaspresso.testcases.api.testcase.TestCase

import com.kg.gettransfer.presentation.ui.SplashActivity

import com.kg.gettransfer.presentation.data.Constants

import com.kg.gettransfer.presentation.screenelements.Chat
import com.kg.gettransfer.presentation.screenelements.BookNow
import com.kg.gettransfer.presentation.screenelements.Locations
import com.kg.gettransfer.presentation.screenelements.MainScreen
import com.kg.gettransfer.presentation.screenelements.NavBar
import com.kg.gettransfer.presentation.screenelements.OrdersDetails
import com.kg.gettransfer.presentation.screenelements.OrderScreen
import com.kg.gettransfer.presentation.screenelements.Payment
import com.kg.gettransfer.presentation.screenelements.ProfileScreen
import com.kg.gettransfer.presentation.screenelements.SearchForm
import com.kg.gettransfer.presentation.screenelements.SettingsScreen
import com.kg.gettransfer.presentation.screenelements.TripsScreen

import org.junit.Rule
import org.junit.Test

class RegressSuite : TestCase(
    Kaspresso.Builder.default().apply {
        flakySafetyParams.apply {
            timeoutMs = Constants.big
        }
    }
) {

    @Rule
    @JvmField
    var activityTestRule = ActivityTestRule(SplashActivity::class.java)

    @Rule
    @JvmField
    var grantPermissionRule = GrantPermissionRule.grant(
        "android.permission.ACCESS_FINE_LOCATION",
        "android.permission.ACCESS_COARSE_LOCATION"
    )

    @Test
    fun cancellations() {
        before {
        }.after {
        }.run {
            step("GoStart") {
                Screen.idle(DELAY_BIG)
                BaseFun.goStart()
            }
            step("TakeGeo") {
                MainScreen {
                    switchHourly { click() }
                    btnNext { click() }
                }
            }
            step("CreateTransfer") {
                OrdersDetails {
                    transportType { swipeUp() }
                    Screen.idle(DELAY_VERY_BIG)
                    BaseFun.chooseDate()
                    btnGetOffers { click() }
                    OrdersDetails.goTransferType()
                    OrdersDetails.goSwitchAgreement()
                }
            }
            step("OpenRequestInfo") {
                BookNow {
                    btnRequestInfo { click() }
                }
            }
            step("cancelTransfer") {
                TripsScreen {
                    tvCancel { click() }
                    BaseFun.okCancel()
                    step("OpenPastTrips") {
                        TripsScreen.openPastTrips()
                    }
                    step("RepeatTransfer") {
                        TripsScreen {
                            transferTime { swipeUp() }
                        }
                    }
                }
            }
        }
    }

    @Test
    fun transferRepeat() {
        before {
        }.after {
        }.run {
            step("GoStart") {
                Screen.idle(DELAY_BIG)
                BaseFun.goStart()
            }
            step(" Checkout DEV ") {
                SettingsScreen.checkoutDev()
            }
            step("GoLogin") {
                BaseFun.goProfilePassenger()
            }
            step("GoTrips") {
                NavBar {
                    trips { click() }
                }
            }
            step("OpenPastTrips") {
                TripsScreen {
                    recycler {
                        flakySafely { isVisible() }
                        swipeLeft()
                        firstChild<TripsScreen.Item> {
                            flakySafely { isVisible() }
                            click()
                        }
                    }
                }
            }
            step("RepeatTransfer") {
                TripsScreen {
                    tvRepeat { click() }
                }
            }
            step("CreateTransfer") {
                OrdersDetails {
                    Screen.idle()
                    transportType { swipeUp() }
                    Screen.idle(DELAY_SMALL)
                    BaseFun.chooseDate()
                    btnGetOffers { click() }
                    OrdersDetails.goTransferType()
                    OrdersDetails.goSwitchAgreement()
                }
            }
        }
    }

    @Test
    fun testChat() {
        before {
        }.after {
        }.run {
            step("GoStart") {
                Screen.idle(DELAY_BIG)
                BaseFun.goStart()
            }
            step(" Checkout DEV ") {
                SettingsScreen.checkoutDev()
            }
            step("GoLogin") {
                BaseFun.goProfilePartner()
            }
            Screen.idle(Constants.big)
            step("GoTrips") {
                NavBar {
                    trips { click() }
                }
            }
            step("OpenTransfer") {
                TripsScreen {
                    recycler {
                        flakySafely { isVisible() }
                        firstChild<TripsScreen.Item> {
                            flakySafely { isVisible() }
                            click()
                        }
                    }
                }
            }
            step("OpenChat") {
                TripsScreen {
                    tvChat { click() }
                }
            }
            step("SendMessage") {
                Chat {
                    fieldMessage { typeText(Constants.TEXT_TEST) }
                    btnSend { click() }
                }
            }
        }
    }

    @Test
    fun loginVerification() {
        before {
        }.after {
        }.run {
            step("GoStart") {
                Screen.idle(DELAY_BIG)
                BaseFun.goStart()
            }
            step(" Checkout DEV ") {
                SettingsScreen.checkoutDev()
            }
            Screen.idle(Constants.small)
            step("UnLogin") {
                BaseFun.unLogin()
            }
            step("Verification") {
                ProfileScreen {
                    email { typeText(Constants.TEXT_PHONE_IVAN) }
                    tvPwd { typeText(Constants.TEXT_PWD_IVAN) }
                    loginBtn { click() }
                }
            }
            step("CheckInvalid") {
                ProfileScreen {
                    dialogImage { isVisible() }
                    dialogTitle { isVisible() }
                    dialogDetail { isVisible() }
                    btnDialogOkButton { isVisible() }
                }
            }
        }
    }

    @Test
    fun registrationVerification() {
        before {
        }.after {
        }.run {
            step("GoStart") {
                Screen.idle(DELAY_BIG)
                BaseFun.goStart()
            }
            step(" Checkout DEV ") {
                SettingsScreen.checkoutDev()
            }
            step("UnLogin") {
                BaseFun.unLogin()
            }
            step("Verification") {
                ProfileScreen {
                    loginPager { swipeLeft() }
                    tvName { typeText(Constants.TEXT_RECARDO) }
                    tvPhone { typeText(Constants.TEXT_NUMBER_IVAN) }
                    tvEmailTo { typeText(Constants.TEXT_EMAIL_IVAN) }
                    closeSoftKeyboard()
                    btnSwitch { click() }
                    btnSignUp {
                        scrollTo()
                        click()
                    }
                }
            }
            step("CheckInvalid") {
                ProfileScreen {
                    flakySafely(DELAY_BIG) {
                        btnDialogOkButton { click() }
                    }
                }
            }
        }
    }

    @Test
    fun failPay() {
        before {
        }.after {
        }.run {
            step("GoStart") {
                Screen.idle(DELAY_BIG)
                BaseFun.goStart()
            }
            step(" Checkout DEMO ") {
                SettingsScreen.checkoutDemo()
            }
            step("GoLogin") {
                BaseFun.goProfilePartner()
            }
            Screen.idle(Constants.big)
            OrderScreen.goToSearchScreen()
            SearchForm.inputAddress()
            step("CreateTransfer") {
                OrdersDetails {
                    Screen.idle(DELAY_VERY_BIG)
                    transportType { swipeUp() }
                    Screen.idle(DELAY_SMALL)
                    BaseFun.chooseDate()
                    btnGetOffers { click() }
                    OrdersDetails.goTransferType()
                    OrdersDetails.goSwitchAgreement()
                }
            }
            step("BookNow") {
                BookNow {
                    tvModelCar { click() }
                    Screen.idle(DELAY_SMALL)
                    btnBook { click() }
                    tvBackground { swipeUp() }
                    tvCard { click() }
                    tvPayment { click() }
                }
            }
            step("PayCardFail") {
                PayFun.goFailPayCard()
            }
            step("FailCheck") {
                Screen.idle(DELAY_VERY_BIG)
                Payment {
                    btnTryAgain { flakySafely(timeoutMs = DELAY_BIG) { isVisible() } }
                    tvImageFail { flakySafely(timeoutMs = DELAY_MEDIUM) { isVisible() } }
                    tvTransferNotPaid { flakySafely(timeoutMs = DELAY_MEDIUM) { isVisible() } }
                }
            }
        }
    }

    @Test
    fun checkMap() {
        before {
        }.after {
        }.run {
            step("GoStart") {
                Screen.idle(DELAY_BIG)
                BaseFun.goStart()
            }
            step("OpenMap") {
                MainScreen {
                    tvSelectFrom { click() }
                }
            }
            step("CheckMap") {
                Locations {
                    compose {
                        or(btnNoChangeLng) {
                            click()
                        }
                        or(tvSearchPanel) {
                            isVisible()
                        }
                    }
                    tvSearchPanel { isVisible() }
                    btnNext { click() }
                }
            }
        }
    }

    @Test
    fun checkHourly() {
        before {
        }.after {
        }.run {
            step("GoStart") {
                Screen.idle(DELAY_BIG)
                BaseFun.goStart()
            }
            step("OpenWindowHourly") {
                MainScreen {
                    switchHourly { click() }
                    tvHourly { click() }
                    tvHourlyWindow { swipeUp() }
                    okBtn { click() }
                }
            }
        }
    }

    companion object {
        const val DELAY_SMALL = 500L
        const val DELAY_MEDIUM = 800L
        const val DELAY_BIG = 1000L
        const val DELAY_VERY_BIG = 2000L
    }
}
