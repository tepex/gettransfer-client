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
                    next { click() }
                }
            }
            step("CreateTransfer") {
                TransferDetails {
                    typeCar { click() }
                    typeCars { swipeUp() }
                    Screen.idle(DELAY_SMALL)
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
                    sub_title { click() }
                }
            }
            step("Locations") {
                Locations {
                    searchTo { typeText("Saint-Petersburg") }
                    Screen.idle(DELAY_SMALL)
                    spdAddress { flakySafely(timeoutMs = DELAY_MEDIUM) { click() } }
                }
            }
            step("CreateTransfer") {
                TransferDetails {
                    Screen.idle(DELAY_MEDIUM)
                    typeCar { click() }
                    typeCars { swipeUp() }
                    Screen.idle(DELAY_SMALL)
                    val baseFun = BaseFun
                    baseFun.chooseData()
                    getOffers { click() }
                    baseFun.goSwitchAgreement()
                }
            }
            step("BookNow") {
                BookNow {
                    rvOffers { click() }
                    Screen.idle(DELAY_SMALL)
                    btn_book { click() }
                    background { swipeUp() }
                    Balance { click() }
                    payment { click() }
                }
            }
            step("PayCheck") {
                Payment {
                    paidSuccessfully { isVisible() }
                    bookingNumber { isVisible() }
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
                    sub_title { click() }
                }
            }
            step("Locations") {
                Locations {
                    searchTo { typeText("Saint-Petersburg") }
                    Screen.idle(DELAY_SMALL)
                    spdAddress { flakySafely(timeoutMs = DELAY_MEDIUM) { click() } }
                }
            }
            step("CreateTransfer") {
                TransferDetails {
                    Screen.idle()
                    typeCar { click() }
                    typeCars { swipeUp() }
                    Screen.idle(DELAY_SMALL)
                    val baseFun = BaseFun
                    baseFun.chooseData()
                    getOffers { click() }
                    baseFun.goSwitchAgreement()
                }
            }
            step("BookNow") {
                BookNow {
                    rvOffers { click() }
                    Screen.idle(DELAY_SMALL)
                    btn_book { click() }
                    background { swipeUp() }
                    Card { click() }
                    payment { click() }
                }
            }
            step("PayCard") {
                val payFun = PayFun
                payFun.goPayCard()
            }
            step("PayCheck") {
                Screen.idle(DELAY_BIG)
                Payment {
                    bookingNumber { isVisible() }
                    mapRoute { isVisible() }
                    paidSuccessfully { isVisible() }
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
                    weSpeak { isVisible() }
                    aboutUs { isVisible() }
                    becomeCarrier { isVisible() }
                    messageUs { isVisible() }
                    languages { isVisible() }
                    socialNetwork { isVisible() }
                    ourLanguages { swipeUp() }
                    Screen.idle(DELAY_SMALL)
                    writeUs { isVisible() }
                    include { isVisible() }
                    network { swipeUp() }
                    Screen.idle(DELAY_SMALL)
                    email { isVisible() }
                    callUs { isVisible() }
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
                    currency { isVisible() }
                    language { isVisible() }
                    distanceUnit { isVisible() }
                    profile { isVisible() }
                    aboutApp { isVisible() }
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
                    requests { flakySafely(timeoutMs = DELAY_MEDIUM) { click() } }
                    transferTime { flakySafely(timeoutMs = DELAY_MEDIUM) { swipeUp() } }
                    transfer { flakySafely(timeoutMs = DELAY_MEDIUM) { swipeUp() } }
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
                    aboutUs { click() }
                    viewpager { isVisible() }
                    next { click() }
                    viewpager { isVisible() }
                    next { click() }
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
                    sub_title { click() }
                }
            }
            step("CheckTips") {
                Locations {
                    pointOnMap { isVisible() }
                    airport { click() }
                    station { click() }
                    map { swipeLeft() }
                    map { swipeLeft() }
                    hotel { click() }
                }
            }
        }
    }

    companion object {
        const val DELAY_SMALL = 500L
        const val DELAY_MEDIUM = 800L
        const val DELAY_BIG = 1000L
    }
}
