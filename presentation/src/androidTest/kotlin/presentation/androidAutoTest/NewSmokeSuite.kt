package com.kg.gettransfer.androidAutoTest

import androidx.test.rule.ActivityTestRule
import androidx.test.rule.GrantPermissionRule

import com.agoda.kakao.screen.Screen

import com.kaspersky.kaspresso.testcases.api.testcase.TestCase

import com.kg.gettransfer.presentation.ui.SplashActivity

import org.junit.Rule
import org.junit.Test
import presentation.screenelements.*
import presentation.screenelements.MainScreen

@Suppress("MagicNumber")
class NewSmokeSuite : TestCase() {

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
                Screen.idle(1_000L)
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
                    next { click() }
                }
            }
            step("CreateTransfer") {
                TransferDetails {
                    typeCar { click() }
                    typeCars { swipeUp() }
                    Screen.idle(500L)
                    val baseFun = BaseFun
                    baseFun.chooseData()
                    getOffers { click() }
                    baseFun.goSwitchAgreement()
                }
            }
            step("CheckElements") {
                BookNow {
                    driversCount { isVisible() }
                    clock { isVisible() }
                    tvWait { isVisible() }
                }
            }
        }
    }

    @Test
    fun payBalance() {
        before {
        }.after {
        }.run {
            step("GoStart") {
                Screen.idle(1_000L)
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
                    sub_title { click() }
                }
            }
            step("Locations") {
                Locations {
                    searchTo { typeText("Saint-Petersburg") }
                    Screen.idle(500L)
                    spdAddress { flakySafely(timeoutMs = 800) { click() } }
                }
            }
            step("CreateTransfer") {
                TransferDetails {
                    Screen.idle(800L)
                    typeCar { click() }
                    typeCars { swipeUp() }
                    Screen.idle(500L)
                    val baseFun = BaseFun
                    baseFun.chooseData()
                    getOffers { click() }
                    baseFun.goSwitchAgreement()
                }
            }
            step("BookNow") {
                BookNow {
                    rvOffers { click() }
                    Screen.idle(500L)
                    btn_book { click() }
                    background { swipeUp() }
                    Balance { click() }
                    Payment { click() }
                }
            }
            step("PayCheck") {
                Payment {
                    PaidSuccessfully { isVisible() }
                    BookingNumber { isVisible() }
                    mapRoute { isVisible() }
                    mapViewRoute { swipeUp() }
                    tvRemainTime { isVisible() }
                }
            }
        }
    }

    @Test
    fun payCard() {
        before {
        }.after {
        }.run {
            step("GoStart") {
                Screen.idle(1_000L)
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
                    sub_title { click() }
                }
            }
            step("Locations") {
                Locations {
                    searchTo { typeText("Saint-Petersburg") }
                    Screen.idle(500L)
                    spdAddress { flakySafely(timeoutMs = 800) { click() } }
                }
            }
            step("CreateTransfer") {
                TransferDetails {
                    Screen.idle(800L)
                    typeCar { click() }
                    typeCars { swipeUp() }
                    Screen.idle(500L)
                    val baseFun = BaseFun
                    baseFun.chooseData()
                    getOffers { click() }
                    baseFun.goSwitchAgreement()
                }
            }
            step("BookNow") {
                BookNow {
                    rvOffers { click() }
                    Screen.idle(500L)
                    btn_book { click() }
                    background { swipeUp() }
                    Card { click() }
                    Payment { click() }
                }
            }
            step("PayCard") {
                val payFun = PayFun
                payFun.goPayCard()
            }
            step("PayCheck") {
                Screen.idle(1_000L)
                Payment {
                    BookingNumber { isVisible() }
                    mapRoute { isVisible() }
                    PaidSuccessfully { isVisible() }
                    mapViewRoute { swipeUp() }
                    tvRemainTime { isVisible() }
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
                Screen.idle(1_000L)
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
                    Screen.idle(1_000L)
                    Requests { flakySafely(timeoutMs = 800) { click() } }
                    transferDetails { swipeUp() }
                    Screen.idle(500L)
                    bookNowInfo { swipeUp() }
                    Screen.idle(500L)
                }
            }
            step("CheckTrips") {
                Trips {
                    transfer_details_main { isVisible() }
                    bottomCommunicationButtons { isVisible() }
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
                Screen.idle(1_000L)
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
                    WeSpeak { isVisible() }
                    aboutUs { isVisible() }
                    becomeCarrier { isVisible() }
                    MessageUs { isVisible() }
                    Languages { isVisible() }
                    socialNetwork { isVisible() }
                    ourLanguages { swipeUp() }
                    Screen.idle(500L)
                    WriteUs { isVisible() }
                    include { isVisible() }
                    Network { swipeUp() }
                    Screen.idle(500L)
                    Email { isVisible() }
                    CallUs { isVisible() }
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
                Screen.idle(1_000L)
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
                    Currency { isVisible() }
                    Language { isVisible() }
                    DistanceUnit { isVisible() }
                    Profile { isVisible() }
                    AboutApp { isVisible() }
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
                Screen.idle(1_000L)
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
                    Screen.idle(1_000L)
                    swRequests { flakySafely(timeoutMs = 800) { swipeLeft() } }
                    Requests { flakySafely(timeoutMs = 800) { click() } }
                    TransferTime { flakySafely(timeoutMs = 800) { swipeUp() } }
                    transfer { flakySafely(timeoutMs = 800) { swipeUp() } }
                    flexboxTransportTypes { isVisible() }
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
                Screen.idle(1_000L)
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
                    aboutUs { click() }
                    viewpager { isVisible() }
                    Next { click() }
                    viewpager { isVisible() }
                    Next { click() }
                }
            }
            step("CheckBecome") {
                Help {
                    becomeCarrier { click() }
                    tv_title { isVisible() }
                    btn_continue { isVisible() }
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
                Screen.idle(1_000L)
                Onboarding {
                    compose {
                        or(btnClose) { click() }
                        or(gtrLogo) { click() }
                    }
                }
            }
            step("CheckReadMore") {
                MainScreen {
                    bestPriceLogo { isVisible() }
                    layoutBestPriceText { click() }
                    read_more_title { isVisible() }
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
                Screen.idle(1_000L)
                Onboarding {
                    compose {
                        or(btnClose) { click() }
                        or(gtrLogo) { click() }
                    }
                }
            }
            step("TakeGeo") {
                MainScreen {
                    sub_title { click() }
                }
            }
            step("CheckTips") {
                Locations {
                    PointOnMap { isVisible() }
                    Airport { click() }
                    Station { click() }
                    Map { swipeLeft() }
                    Map { swipeLeft() }
                    Hotel { click() }
                }
            }
        }
    }
}
