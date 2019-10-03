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

class TransferSmokeSuite : TestCase() {

    @Rule
    @JvmField
    var activityTestRule = ActivityTestRule(SplashActivity::class.java)

    @get:Rule
    val screenshotTestRule = ScreenshotTestRule()

    @Rule
    @JvmField
    var grantPermissionRule = GrantPermissionRule.grant(
        "android.permission.ACCESS_FINE_LOCATION",
        "android.permission.ACCESS_COARSE_LOCATION"
    )

    @Test
    fun createHourlyTransfer() {
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
            step("CheckElements") {
                BookNow {
                    tvDriversCount { isVisible() }
                    tvClock { isVisible() }
                    tvWait { isVisible() }
                }
            }
        }
    }

    @Suppress("LongMethod")
    @Test
    fun payBalance() {
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
                    tvBalance { click() }
                    tvPayment { click() }
                }
            }
            step("PayCheck") {
                Payment {
                    tvPaidSuccessfully { isVisible() }
                    tvBookingNumber { isVisible() }
                    mapRoute { isVisible() }
                    mapViewRoute { swipeUp() }
                    tvRemainTime { isVisible() }
                }
            }
        }
    }

    @Suppress("LongMethod")
    @Test
    fun payCard() {
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
            step("PayCard") {
                val payFun = PayFun
                payFun.goGoodPayCard()
            }
            step("PayCheck") {
                Screen.idle(DELAY_VERY_BIG)
                Payment {
                    btnSupport { flakySafely(timeoutMs = DELAY_BIG) { isVisible() } }
                    tvBookingNumber { flakySafely(timeoutMs = DELAY_BIG) { isVisible() } }
                    mapViewRoute { flakySafely(timeoutMs = DELAY_MEDIUM) { swipeUp() } }
                    mapRoute { flakySafely(timeoutMs = DELAY_MEDIUM) { isVisible() } }
                    tvPaidSuccessfully { flakySafely(timeoutMs = DELAY_MEDIUM) { isVisible() } }
                    tvRemainTime { flakySafely(timeoutMs = DELAY_MEDIUM) { isVisible() } }
                }
            }
        }
    }

    @Test
    fun checkTrips() {
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
            step("GoTrips") {
                NavBar {
                    trips { click() }
                }
            }
            step("OpenTrips") {
                Trips {
                    Screen.idle(DELAY_BIG)
                    requests { flakySafely(timeoutMs = DELAY_MEDIUM) { click() } }
                    transferDetails { swipeUp() }
                    Screen.idle(DELAY_SMALL)
                    bookNowInfo { swipeUp() }
                    Screen.idle(DELAY_SMALL)
                }
            }
            step("CheckTrips") {
                Trips {
                    tvDetailsMain { isVisible() }
                    btnCommunication { isVisible() }
                }
            }
        }
    }

    @Test
    fun checkHelp() {
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
            step("GoHelp") {
                NavBar {
                    help { click() }
                }
            }
            step("CheckHelp") {
                Help {
                    tvWeSpeak { isVisible() }
                    tvAboutUs { isVisible() }
                    tvBecomeCarrier { isVisible() }
                    tvMessageUs { isVisible() }
                    tvLanguages { isVisible() }
                    tvSocialNetwork { isVisible() }
                    tvOurLanguages { swipeUp() }
                    Screen.idle(DELAY_SMALL)
                    tvWriteUs { isVisible() }
                    include { isVisible() }
                    tvNetwork { swipeUp() }
                    Screen.idle(DELAY_SMALL)
                    tvEmail { isVisible() }
                    tvCallUs { isVisible() }
                }
            }
        }
    }

    @Test
    fun checkSettings() {
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
            step("SettingsCheck") {
                SettingsScreen {
                    tvCurrency { isVisible() }
                    tvLanguage { isVisible() }
                    tvDistanceUnit { isVisible() }
                    tvProfile { isVisible() }
                    tvAboutApp { isVisible() }
                }
            }
        }
    }

    @Test
    fun checkPastTrips() {
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
                    transferTime { flakySafely(timeoutMs = DELAY_MEDIUM) { swipeUp() } }
                    tvTransfer { flakySafely(timeoutMs = DELAY_MEDIUM) { swipeUp() } }
                    tvTransportTypes { isVisible() }
                    topCommunicationButtons { isVisible() }
                }
            }
        }
    }

    @Test
    fun checkHelpWindows() {
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
            step("GoHelp") {
                NavBar {
                    help { click() }
                }
            }
            step("CheckAbout") {
                Help {
                    tvAboutUs { click() }
                    tvViewPager { isVisible() }
                    btnNext { click() }
                    tvViewPager { isVisible() }
                    btnNext { click() }
                }
            }
            step("CheckBecome") {
                Help {
                    tvBecomeCarrier { click() }
                    tvTitle { isVisible() }
                    btnContinue { isVisible() }
                    ivBack { click() }
                }
            }
        }
    }

    @Test
    fun checkReadMore() {
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
            step("CheckReadMore") {
                MainScreen {
                    tvBestPriceLogo { isVisible() }
                    tvLayoutBestPriceText { click() }
                    tvReadMoreTitle { isVisible() }
                    btnClose { click() }
                }
            }
        }
    }

    @Test
    fun checkTipsSearch() {
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
            step("TakeGeo") {
                MainScreen {
                    subTitle { click() }
                }
            }
            step("CheckTips") {
                Screen.idle(DELAY_MEDIUM)
                val baseFun = BaseFun
                baseFun.checkTips()
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
