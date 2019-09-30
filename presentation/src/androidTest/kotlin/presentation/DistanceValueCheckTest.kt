package com.kg.gettransfer.presentation

import androidx.test.rule.ActivityTestRule

import com.agoda.kakao.screen.Screen

import com.kaspersky.kaspresso.kaspresso.Kaspresso
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase

import com.kg.gettransfer.presentation.ui.SplashActivity

import org.junit.Rule
import org.junit.Test

import presentation.data.Delay

import presentation.screenelements.NavBar
import presentation.screenelements.Onboarding
import presentation.screenelements.OrderScreen
import presentation.screenelements.OrdersDetails
import presentation.screenelements.ProfileScreen
import presentation.screenelements.SearchForm
import presentation.screenelements.SettingsScreen
import presentation.screenelements.TripsScreen

class DistanceValueCheckTest : TestCase(
    Kaspresso.Builder.default().apply {
    flakySafetyParams = flakySafetyParams.apply {
        timeoutMs = Delay.big
    }
}) {
    @Rule
    @JvmField
    var activityTestRule = ActivityTestRule(SplashActivity::class.java)

    @Test
    @Suppress("LongMethod")
    fun testDistanceValueCheck() {
        before {
        }.after {
        }.run {
            step("Open order screen") {
                Onboarding {
                    compose {
                        or(btnNext) { click() }
                        or(NavBar.orderItem) { click() }
                    }

                    compose {
                        or(btnNext) { click() }
                        or(NavBar.orderItem) { click() }
                    }
                }
            }
            step("Go to settings ") {
                NavBar {
                    settingsItem { click() }
                }
            }
            SettingsScreen {
                tvProfileCell { click() }
            }
            step("Signed in account ") {
                ProfileScreen {
                    compose {
                        or(logoutBtn) { isVisible() }
                        or(email) { typeText("mygtracc1@gmail.com") }
                    }
                    compose {
                        or(tvPwd) { typeText("PassRR11") }
                        or(logoutBtn) { isVisible() }
                    }
                    compose {
                        or(loginBtn) { click() }
                        or(logoutBtn) { isVisible() }
                    }
                    compose {
                        or(NavBar.settingsItem) { click() }
                        or(btnBack) { click() }
                    }
                }
            }
            Screen.idle(Delay.small)
            step("Choose KM") {
                SettingsScreen {
                    tvDistanceSwitcher {
                        setChecked(checked = true)
                        click()
                    }
                }
            }
            step("Go to trips Screen") {
                NavBar {
                    tripsItem { click() }
                }
            }
            TripsScreen {
                recycler {
                    isVisible()
                    firstChild<TripsScreen.Item> {
                        isVisible()
                        distance {
                            isVisible()
                            hasText("Distance: 712 KM")
                        }
                        click()
                        content { swipeUp() }
                        distanceOnTransferInfo {
                            isVisible()
                            hasText("712 KM")
                        }
                        btnBack { click() }
                    }
                }
            }
            NavBar {
                orderItem { click() }
            }
            OrderScreen {
                searchFrom { click() }
            }
            SearchForm {
                addressFrom { typeText("Moscow") }
                Screen.idle(Delay.small)
                mskAddressItem {
                    isVisible()
                    click()
                }
                addressTo { typeText("Saint Petersburg") }
                Screen.idle(Delay.small)
                spbAddressItem {
                    isVisible()
                    click()
                }
            }
            Screen.idle(Delay.small)
            OrdersDetails {
                btnBack { click() }
            }
            step("Go to settings ") {
                NavBar {
                    settingsItem { click() }
                }
            }
            step("Choose MILES") {
                SettingsScreen {
                    tvDistanceSwitcher {
                        setChecked(checked = false)
                        click()
                    }
                }
            }
            NavBar {
                tripsItem { click() }
            }
            TripsScreen {
                recycler {
                    isVisible()
                    firstChild<TripsScreen.Item> {
                        isVisible()
                        distance {
                            isVisible()
                            hasText("Distance: 442 MI")
                        }
                        click()
                        content { swipeUp() }
                        distanceOnTransferInfo {
                            isVisible()
                            hasText("442 MI")
                        }
                        btnBack { click() }
                    }
                }
            }

            NavBar {
                orderItem { click() }
            }
            OrderScreen {
                searchFrom { click() }
            }
            SearchForm {
                addressFrom {
                    clearText()
                    typeText("Moscow") }
                Screen.idle(Delay.small)
                mskAddressItem {
                    isVisible()
                    click()
                }
                closeSoftKeyboard()
                Screen.idle(Delay.small)
                addressTo {
                    clearText()
                    typeText("Saint Petersburg")
                }
                Screen.idle(Delay.small)
                spbAddressItem {
                    isVisible()
                    click()
                }
            }

            OrdersDetails {
                btnBack { click() }
            }
        }
    }
}
