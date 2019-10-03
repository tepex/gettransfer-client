package com.kg.gettransfer.presentation

import androidx.test.rule.ActivityTestRule

import com.agoda.kakao.screen.Screen

import com.kaspersky.kaspresso.testcases.api.testcase.TestCase

import com.kg.gettransfer.presentation.ui.SplashActivity

import org.junit.Rule
import org.junit.Test

import presentation.data.Delay

import presentation.screenelements.Calendar
import presentation.screenelements.DialogWindow
import presentation.screenelements.NavBar
import presentation.screenelements.OffersScreen
import presentation.screenelements.Onboarding
import presentation.screenelements.OrderScreen
import presentation.screenelements.OrdersDetails
import presentation.screenelements.ProfileScreen
import presentation.screenelements.SearchForm
import presentation.screenelements.SettingsScreen

class PassengerCreateTransferTest : TestCase() {
    @Rule
    @JvmField
    var activityTestRule = ActivityTestRule(SplashActivity::class.java)

    @Test
    @Suppress("LongMethod")
    fun testPassengerCreateTransfer() {
        before {
        }.after {
        }.run {
            step("Open Screen") {
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
            step("Click settings menu") {
                NavBar {
                    settingsItem { click() }
                }
            }
            SettingsScreen {
                profileCell {
                    click()
                }
            }
            ProfileScreen {
                compose {
                    or(logoutBtn) { isVisible() }
                    or(email) { typeText("mygtracc1@gmail.com") }
                }
                compose {
                    or(pwd) { typeText("PassRR11") }
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
            NavBar {
                orderItem { click() }
            }
            OrderScreen {
                searchFrom { click() }
            }
            SearchForm {
                addressFrom {
                    typeText("Moscow")
                }
                Thread.sleep(Delay.small)
                mskAddressItem {
                    click()
                }
                addressTo {
                    typeText("Saint Petersburg")
                }
                Thread.sleep(Delay.small)
                spbAddressItem {
                    click()
                }
            }
            Screen.idle()
            OrdersDetails {
                content {
                    swipeUp()
                }
                Thread.sleep(Delay.small)
                plusPassenger {
                    click()
                }
            }
            Screen.idle(Delay.medium)
            Calendar {
                transferDate {
                    click()
                }
                datePickerDialog {
                    datePicker {
                        setDate(2019, 11, 12)
                        hasDate(2019, 11, 12)
                    }
                    okButton {
                        click()
                    }
                }
                timePickerDialog {
                    timePicker {
                        setTime(22, 4)
                        hasTime(22, 4)
                    }
                    okButton {
                        click()
                    }
                }
            }
            OrdersDetails {
                btnGetOffers {
                    click()
                }
            }
            DialogWindow {
                msgNoTransport {
                    isVisible()
                }
                btnOk {
                    click()
                }
            }
            OrdersDetails {
                transportType {
                    click()
                }
                btnGetOffers {
                    click()
                }
            }
            OffersScreen {
                recycler {
                    isVisible()

                    firstChild<OffersScreen.Item> {
                        isVisible()
                        click()
                        btnBook {
                            click() }
                    }
                }
            }
        }
    }
}
