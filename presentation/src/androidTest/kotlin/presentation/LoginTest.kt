package com.kg.gettransfer.presentation

import androidx.test.rule.ActivityTestRule

import com.agoda.kakao.screen.Screen

import com.kaspersky.kaspresso.testcases.api.testcase.TestCase

import com.kg.gettransfer.presentation.ui.SplashActivity

import presentation.screenelements.NavBar
import presentation.screenelements.Onboarding
import presentation.screenelements.Profile
import presentation.screenelements.SettingsScreen

import org.junit.Rule
import org.junit.Test

@Suppress("MagicNumber")
class LoginTest : TestCase() {

    @Rule
    @JvmField
    var activityTestRule = ActivityTestRule(SplashActivity::class.java)

    @Test
    fun testAuth() {
        before {
        }.after {
        }.run {

            step("Pass Onboarding") {
            Screen.idle(5_500L)
        Onboarding {
            btnNext {
                flakySafely {  click()
            }
            }
            btnNext {
                flakySafely {  click()
            }
        }
        }
        }
            step("Click settings menu ") {
        NavBar {
            settings {
                click()
            }
            }
            }
            SettingsScreen {
                profileCell {
                    click()
                }
            }
            Profile {
                email {
                    typeText("mygtracc1@gmail.com")
                }
                pwd {
                    typeText("PassRR11")
                }
                loginBtn {
                    click()
                }
            }
        }
    }
    }
