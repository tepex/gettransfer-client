package com.kg.gettransfer.presentation

import androidx.test.rule.ActivityTestRule

import com.agoda.kakao.screen.Screen

import com.kaspersky.kaspresso.testcases.api.testcase.TestCase

import com.kg.gettransfer.presentation.ui.SplashActivity

import org.junit.Rule
import org.junit.Test

import com.kg.gettransfer.presentation.data.Constants

import com.kg.gettransfer.presentation.screenelements.Calendar
import com.kg.gettransfer.presentation.screenelements.DialogWindow
import com.kg.gettransfer.presentation.screenelements.NavBar
import com.kg.gettransfer.presentation.screenelements.OffersScreen
import com.kg.gettransfer.presentation.screenelements.Onboarding
import com.kg.gettransfer.presentation.screenelements.OrderScreen
import com.kg.gettransfer.presentation.screenelements.OrdersDetails
import com.kg.gettransfer.presentation.screenelements.ProfileScreen
import com.kg.gettransfer.presentation.screenelements.SearchForm
import com.kg.gettransfer.presentation.screenelements.SettingsScreen

class PassengerCreateTransferTest : TestCase() {
    @Rule
    @JvmField
    var activityTestRule = ActivityTestRule(SplashActivity::class.java)

    @Test
    @Suppress("LongMethod", "MagicNumber")
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
                tvProfileCell {
                    click()
                }
            }
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
                Thread.sleep(Constants.small)
                mskAddressItem {
                    click()
                }
                addressTo {
                    typeText("Saint Petersburg")
                }
                Thread.sleep(Constants.small)
                spbAddressItem {
                    click()
                }
            }
            Screen.idle()
            OrdersDetails {
                content {
                    swipeUp()
                }
                Thread.sleep(Constants.small)
                plusPassenger {
                    click()
                }
            }
            Screen.idle(Constants.medium)
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
