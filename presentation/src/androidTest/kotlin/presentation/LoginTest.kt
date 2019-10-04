package com.kg.gettransfer.presentation

import androidx.test.rule.ActivityTestRule

import com.kaspersky.kaspresso.testcases.api.testcase.TestCase

import com.kg.gettransfer.presentation.ui.SplashActivity

import org.junit.Rule
import org.junit.Test

import presentation.data.Delay

import presentation.screenelements.NavBar
import presentation.screenelements.Onboarding
import presentation.screenelements.ProfileScreen
import presentation.screenelements.SettingsScreen

class LoginTest : TestCase() {

    @Rule
    @JvmField
    var activityTestRule = ActivityTestRule(SplashActivity::class.java)

    @Test
    fun testAuth() {
        before {
        }.after {
        }.run {
            step("Pass onboarding") {
                Onboarding {
                    btnNext { flakySafely(timeoutMs = Delay.big) { click() } }
                    btnNext { flakySafely(timeoutMs = Delay.big) { click() } }
                }
            }
            step("Click settings menu") {
                NavBar {
                    settingsItem { click() }
                }
            }
            SettingsScreen {
                tvProfileCell { click() }
            }
            ProfileScreen {
                email { typeText("mygtracc1@gmail.com") }
                tvPwd { typeText("PassRR11") }
                loginBtn { click() }
            }
        }
    }
}
