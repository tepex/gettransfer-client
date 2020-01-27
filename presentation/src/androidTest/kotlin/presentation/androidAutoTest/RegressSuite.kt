package com.kg.gettransfer.presentation.androidAutoTest

import androidx.test.rule.ActivityTestRule
import androidx.test.rule.GrantPermissionRule

import com.agoda.kakao.screen.Screen

import com.kaspersky.kaspresso.testcases.api.testcase.TestCase

import com.kg.gettransfer.presentation.ui.SplashActivity

import org.junit.Rule
import org.junit.Test
import com.kg.gettransfer.presentation.data.Constants

import com.kg.gettransfer.presentation.screenelements.*
import org.junit.Ignore

@Suppress("LongMethod")
class RegressSuite : TestCase() {

    @Rule
    @JvmField
    var activityTestRule = ActivityTestRule(SplashActivity::class.java)

//    @get:Rule
//    val screenshotTestRule = ScreenshotTestRule()

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
                Onboarding {
                    compose {
                        or(btnClose) { click() }
                        or(gtrLogo) { click() }
                    }
                    val baseFun = BaseFun
                    baseFun.goTransferLater()
                }
            }
            step("TakeGeo") {
                MainScreen {
                    switchHourly { click() }
                    btnNext { click() }
                }
            }
            step("CreateTransfer") {
                TransferDetails {
                    typeCars { swipeUp() }
                    Screen.idle(DELAY_SMALL)
                    val baseFun = BaseFun
                    baseFun.chooseData()
                    getOffers { click() }
                    baseFun.goTransferType()
                    baseFun.goSwitchAgreement()
                }
            }
            step("OpenRequestInfo") {
                BookNow {
                    btnRequestInfo { click() }
                }
            }
            step("cancelTransfer") {
                Trips {
                    tvCancel { click() }
                    val baseFun = BaseFun
                    baseFun.okCancel()
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
                Onboarding {
                    compose {
                        or(btnClose) { click() }
                        or(gtrLogo) { click() }
                    }
                    val baseFun = BaseFun
                    baseFun.goTransferLater()
                }
            }
            step("GoSettings") {
                NavBar {
                    settings { click() }
                }
            }
            step("GoLogin") {
                val baseFun = BaseFun
                baseFun.goPassLogin()
            }
            step("GoTrips") {
                NavBar {
                    trips { click() }
                }
            }
            step("OpenPastTrips") {
                Trips {
                    Screen.idle(DELAY_BIG)
                    swRequests { flakySafely(timeoutMs = DELAY_MEDIUM) { swipeLeft() } }
                    Screen.idle(DELAY_BIG)
                    requests { flakySafely(timeoutMs = DELAY_MEDIUM) { click() } }
                }
            }
            step("RepeatTransfer") {
                Trips {
                    tvRepeat { click() }
                }
            }
            step("CreateTransfer") {
                TransferDetails {
                    Screen.idle()
                    typeCars { swipeUp() }
                    Screen.idle(DELAY_SMALL)
                    val baseFun = BaseFun
                    baseFun.chooseData()
                    getOffers { click() }
                    baseFun.goTransferType()
                    baseFun.goSwitchAgreement()
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
                Onboarding {
                    compose {
                        or(btnClose) { click() }
                        or(gtrLogo) { click() }
                    }
                    val baseFun = BaseFun
                    baseFun.goTransferLater()
                }
            }
            step("GoSettings") {
                NavBar {
                    settings { click() }
                }
            }
            step("GoLogin") {
                val baseFun = BaseFun
                baseFun.goPassLogin()
            }
            step("GoTrips") {
                NavBar {
                    trips { click() }
                }
            }
            step("OpenTransfer") {
                Trips {
                    Screen.idle(DELAY_BIG)
                    tvRequestNumber { click() }
                }
            }
            step("OpenChat") {
                Trips {
                    tvChat { click() }
                }
            }
            step("SendMessage") {
                Chat {
                    btnMessage { typeText(Constants.TEXT_TEST) }
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
                Onboarding {
                    compose {
                        or(btnClose) { click() }
                        or(gtrLogo) { click() }
                    }
                    val baseFun = BaseFun
                    baseFun.goTransferLater()
                }
            }
            step("UnLogin") {
                val baseFun = BaseFun
                baseFun.unLogin()
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
                Onboarding {
                    compose {
                        or(btnClose) { click() }
                        or(gtrLogo) { click() }
                    }
                    val baseFun = BaseFun
                    baseFun.goTransferLater()
                }
            }
            step("UnLogin") {
                val baseFun = BaseFun
                baseFun.unLogin()
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
                Onboarding {
                    compose {
                        or(btnClose) { click() }
                        or(gtrLogo) { click() }
                    }
                }
            }
            step("GoSettings") {
                NavBar {
                    settings { click() }
                }
            }
            step("GoLogin") {
                val baseFun = BaseFun
                baseFun.goPassLogin()
            }
            step("TakeGeo") {
                MainScreen {
                    subTitle { click() }
                }
            }
            step("Locations") {
                Locations {
                    tvSearchTo { typeText(Constants.TEXT_PETERSBURG) }
                    Screen.idle(DELAY_SMALL)
                    tvSpdAddress { flakySafely(timeoutMs = DELAY_MEDIUM) { click() } }
                }
            }
            step("CreateTransfer") {
                TransferDetails {
                    Screen.idle(DELAY_MEDIUM)
                    typeCars { swipeUp() }
                    Screen.idle(DELAY_SMALL)
                    val baseFun = BaseFun
                    baseFun.chooseData()
                    getOffers { click() }
                    baseFun.goTransferType()
                    baseFun.goSwitchAgreement()
                }
            }
            step("BookNow") {
                BookNow {
                    rvOffers { click() }
                    Screen.idle(DELAY_SMALL)
                    btnBook { click() }
                    tvBackground { swipeUp() }
                    tvCard { click() }
                    tvPayment { click() }
                }
            }
            step("PayCardFail") {
                val payFun = PayFun
                payFun.goFailPayCard()
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
                Onboarding {
                    compose {
                        or(btnClose) { click() }
                        or(gtrLogo) { click() }
                    }
                }
            }
            step("OpenMap") {
                MainScreen {
                    tvSelectFrom { click() }
                }
            }
            step("CheckMap") {
                Locations {
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
                Onboarding {
                    compose {
                        or(btnClose) { click() }
                        or(gtrLogo) { click() }
                    }
                }
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
