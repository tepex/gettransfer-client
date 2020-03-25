package presentation.androidAutoTest

import androidx.test.rule.ActivityTestRule
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase

import com.kg.gettransfer.presentation.androidAutoTest.BaseFun
import com.kg.gettransfer.presentation.data.Constants

import com.kg.gettransfer.presentation.screenelements.SettingsScreen
import com.kg.gettransfer.presentation.screenelements.ProfileScreen

import com.kg.gettransfer.presentation.ui.SplashActivity

import org.junit.Rule
import org.junit.Test

class PassengerRegistrationSuccessfulyTest : TestCase() {
    @Rule
    @JvmField
    var activityTestRule = ActivityTestRule(SplashActivity::class.java)
    @Test
    fun testPassengerRegistrationSuccessfuly() {
        before {
        }.after {
        }.run {
            step("Open order screen") {
                BaseFun.goStart()
            }
            step("Checkout on dev") {
                SettingsScreen.checkoutDev()
            }
            step("Go to login screen ") {
                BaseFun.unLogin()
            }
            step("Verification") {
                ProfileScreen {
                    loginPager { swipeLeft() }
                    tvName { typeText(Constants.TEXT_RECARDO) }
                    tvPhone {
                        click()
                        clearText()
                        typeText(getRandomPhone(Constants.SEVEN_SYMBOL))
                    }
                    tvEmailTo { typeText(getRandomEmail(Constants.SEVEN_SYMBOL)) }
                    closeSoftKeyboard()
                    btnSwitch { click() }
                    btnSignUp {
                        scrollTo()
                        click()
                    }
                }
            }
            step("Check succesful registration") {
                ProfileScreen {
                    dialogImage { isVisible() }
                    dialogTitle { isVisible() }
                    dialogDetail { isVisible() }
                    btnDialogOkButton { isVisible() }
                }
            }
        }
    }

    fun getRandomEmail(length: Int): String {
        val allowedChars = "ABCDEFGHIJKLMNOPQRSTUVWXTZabcdefghiklmnopqrstuvwxyz"
        return (1..length)
            .map { allowedChars.random() }
            .joinToString("") + "@gmail.com"
    }

    fun getRandomPhone(length: Int): String {
        val allowedChars = "1234567890"
        return "7999" + (1..length)
            .map { allowedChars.random() }
            .joinToString("")
    }
}
