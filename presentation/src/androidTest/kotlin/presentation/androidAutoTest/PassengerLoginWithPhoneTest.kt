package com.kg.gettransfer.presentation

import androidx.test.rule.ActivityTestRule

import com.kaspersky.kaspresso.testcases.api.testcase.TestCase

import com.kg.gettransfer.presentation.ui.SplashActivity

import org.junit.Rule
import org.junit.Test

import presentation.screenelements.NavBar
import presentation.screenelements.Onboarding
import presentation.screenelements.ProfileScreen
import presentation.screenelements.SettingsScreen

class PassengerLoginWithPhoneTest : TestCase() {
    @Rule
    @JvmField
    var activityTestRule = ActivityTestRule(SplashActivity::class.java)
    @Test
    fun testPassengerLoginWithPhone() {
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
            step("Go to login screen ") {
                SettingsScreen {
                    tvProfileCell {
                        click()
                    }
                }
            }
            ProfileScreen {
                compose {
                    or(logoutBtn) { click() }
                    or(email) { typeText("+79992223838") }
                }
                compose {
                    or(tvPwd) { typeText("PassRR11") }
                    or(SettingsScreen.tvProfileCell) { isVisible() }
                }
                compose {
                    or(loginBtn) { click() }
                    or(SettingsScreen.tvProfileCell) { click() }
                }
                compose {
                    or(NavBar.settingsItem) { click() }
                    or(email) { typeText("+79992223838") }
                }
                compose {
                    or(NavBar.settingsItem) { click() }
                    or(tvPwd) { typeText("PassRR11") }
                }
                compose {
                    or(loginBtn) { click() }
                    or(NavBar.settingsItem) { click() }
                }
            }
        }
    }
}
