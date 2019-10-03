package com.kg.gettransfer.androidAutoTest

import androidx.test.rule.ActivityTestRule
import androidx.test.rule.GrantPermissionRule

import com.agoda.kakao.screen.Screen

import com.kaspersky.kaspresso.testcases.api.testcase.TestCase

import com.kg.gettransfer.presentation.ui.SplashActivity

import org.junit.Rule
import org.junit.Test

import presentation.screenelements.*

class NewRegressSuite : TestCase() {

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
            step("OpenRequestInfo") {
                BookNow {
                    request_info { click() }
                }
            }
            step("cancelTransfer") {
                Trips {
                    cancel { click() }
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
                    requests { flakySafely(timeoutMs = DELAY_MEDIUM) { click() } }
                }
            }
            step("RepeatTransfer") {
                Trips {
                    repeat { click() }
                }
            }
            step("CreateTransfer") {
                TransferDetails {
                    Screen.idle()
                    typeCar { click() }                       // TODO Выпилить после фикса
                    typeCars { swipeUp() }
                    Screen.idle(DELAY_SMALL)
                    val baseFun = BaseFun
                    baseFun.chooseData()
                    getOffers { click() }
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
                    requestNumber { click() }
                }
            }
            step("OpenChat") {
                Trips {
                    chat { click() }
                }
            }
            step("SendMessage") {
                Chat {
                    message { typeText("Test-Test") }
                    send { click() }
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
