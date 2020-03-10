package com.kg.gettransfer.presentation.androidAutoTest

import androidx.test.rule.ActivityTestRule
import androidx.test.rule.GrantPermissionRule

import com.agoda.kakao.screen.Screen
import com.kaspersky.kaspresso.kaspresso.Kaspresso

import com.kaspersky.kaspresso.testcases.api.testcase.TestCase

import com.kg.gettransfer.presentation.ui.SplashActivity

import org.junit.Rule
import org.junit.Test

import com.kg.gettransfer.presentation.data.Constants
import com.kg.gettransfer.presentation.screenelements.*

class TransferSmokeSuite : TestCase(Kaspresso.Builder.default().apply {
    flakySafetyParams.apply {
        timeoutMs = Constants.big
    }
}) {

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
    fun createHourlyTransfer() {
        before {
        }.after {
        }.run {
            step("GoStart") {
                Screen.idle(RegressSuite.DELAY_BIG)
                BaseFun.goStart()
            }
            step("TakeGeo") {
                MainScreen {
                    switchHourly { click() }
                }
                OrderScreen {
                    duration { isVisible() }
                    searchFrom { click() }
                }
                SearchForm {
                    addressFrom {
                        clearText()
                        typeText(Constants.TEXT_MOSCOW)
                    }
                    Thread.sleep(Constants.small)
                    mskAddressItem {
                        click()
                    }
                }
            }
            step("CreateTransfer") {
                TransferDetails {
                    Screen.idle(DELAY_VERY_BIG)
                    typeCars { swipeUp() }
                    Screen.idle(DELAY_VERY_BIG)
                    BaseFun.chooseDate()
                    getOffers { click() }
                    BaseFun.goTransferType()
                    BaseFun.goSwitchAgreement()
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

    @Test
    fun payBalance() {
        before {
        }.after {
        }.run {
            step("GoStart") {
                Screen.idle(RegressSuite.DELAY_BIG)
                BaseFun.goStart()
            }
            step("GoLogin") {
                BaseFun.goProfilePartner()
            }
            step("TakeGeo") {
                MainScreen {
                    subTitle { click() }
                }
            }
            step("Locations") {
                BaseFun.inputAddress()
            }
            step("CreateTransfer") {
                TransferDetails {
                    Screen.idle(DELAY_MEDIUM)
                    typeCars { swipeUp() }
                    Screen.idle(DELAY_SMALL)
                    val baseFun = BaseFun
                    baseFun.chooseDate()
                    getOffers { click() }
                    baseFun.goTransferType()
                    baseFun.goSwitchAgreement()
                }
            }
            step("BookNow") {
                BookNow {
                    tvModelCar { click() }
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

    @Test
    fun payCard() {
        before {
        }.after {
        }.run {
            step("GoStart") {
                Screen.idle(RegressSuite.DELAY_BIG)
                BaseFun.goStart()
            }
            step(" Checkout DEMO ") {
                BaseFun.checkoutDemo()
            }
            step("GoLogin") {
                BaseFun.goProfilePartner()
            }
            step("TakeGeo") {
                MainScreen {
                    subTitle { click() }
                }
            }
            step("Locations") {
                BaseFun.inputAddress()
            }
            step("CreateTransfer") {
                TransferDetails {
                    Screen.idle(Constants.medium)
                    typeCars { swipeUp() }
                    Screen.idle(Constants.medium)
                    BaseFun.chooseDate()
                    getOffers { click() }
                    BaseFun.goTransferType()
                    BaseFun.goSwitchAgreement()
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
            step("PayCard") {
                PayFun.goGoodPayCard()
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
                BaseFun.goStart()
            }
            step("GoSettings") {
                NavBar {
                    settings { click() }
                }
            }
            step("GoLogin") {
                BaseFun.goProfilePartner()
            }
            step("GoTrips") {
                NavBar {
                    trips { click() }
                }
            }
            step("BookNow") {
                TripsScreen {
                    recycler {
                        isVisible()
                        firstChild<TripsScreen.Item> {
                            flakySafely { isVisible() }
                            click()
                            compose {
                                or { content { isVisible() } }
                                or { TripsScreen.btnInfo { click() } }
                            }
                            compose {
                                or { content { swipeUp() } }
                                or { TripsScreen.btnInfo { click() } }
                            }
                            content { swipeUp() }
                            content { swipeUp() }
                        }
                    }
                }
            }
            step("CheckTrips") {
                TripsScreen {
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
                BaseFun.goStart()
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
                BaseFun.goStart()
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
                BaseFun.goStart()
            }
            step("GoLogin") {
                BaseFun.goProfilePartner()
            }
            step("OpenPastTrips") {
                BaseFun.openPastTrips()
            }
            step("CheckPastTrips") {
                TripsScreen {
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
                BaseFun.goStart()
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
        }
    }

    @Test
    fun checkReadMore() {
        before {
        }.after {
        }.run {
            step("GoStart") {
                    BaseFun.goStart()
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
                BaseFun.goStart()
            }
            step("TakeGeo") {
                MainScreen {
                    subTitle { click() }
                }
            }
            step("CheckTips") {
                Screen.idle(DELAY_MEDIUM)
                BaseFun.checkTips()
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
