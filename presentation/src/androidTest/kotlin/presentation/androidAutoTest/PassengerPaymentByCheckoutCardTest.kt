package presentation.androidAutoTest

import androidx.test.rule.ActivityTestRule

import com.agoda.kakao.screen.Screen

import com.kaspersky.kaspresso.kaspresso.Kaspresso
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase

import com.kg.gettransfer.presentation.androidAutoTest.BaseFun
import com.kg.gettransfer.presentation.androidAutoTest.PayFun
import com.kg.gettransfer.presentation.androidAutoTest.RegressSuite

import com.kg.gettransfer.presentation.data.Constants

import com.kg.gettransfer.presentation.screenelements.SettingsScreen
import com.kg.gettransfer.presentation.screenelements.BookNow
import com.kg.gettransfer.presentation.screenelements.Payment
import com.kg.gettransfer.presentation.screenelements.OrdersDetails
import com.kg.gettransfer.presentation.screenelements.OrderScreen
import com.kg.gettransfer.presentation.screenelements.SearchForm

import com.kg.gettransfer.presentation.ui.SplashActivity

import org.junit.Rule
import org.junit.Test

class PassengerPaymentByCheckoutCardTest : TestCase(Kaspresso.Builder.default().apply {
    flakySafetyParams.apply {
        timeoutMs = Constants.big
    }
}) {
    @Rule
    @JvmField
    var activityTestRule = ActivityTestRule(SplashActivity::class.java)

    @Test
    fun testPassengerPaymentByCheckoutCard() {
        before {
        }.after {
        }.run {
            step("Go to order screen") {
                BaseFun.goStart()
            }
            step("Checkout dev") {
                SettingsScreen.checkoutDev()
            }
            step("Authtorization") {
                BaseFun.goProfilePassenger()
                SettingsScreen.changeLanguage()
            }
            step("Create order") {
                OrderScreen.goToSearchScreen()
                SearchForm.inputAddress()
                OrdersDetails {
                    content { flakySafely { swipeUp() } }
                    Thread.sleep(Constants.medium)
                    BaseFun.chooseDate()
                    btnGetOffers { click() }
                    OrdersDetails.goTransferType()
                    OrdersDetails.goSwitchAgreement()
                }
            }
            step("BookNow") {
                BookNow {
                    tvModelCar { click() }
                    Screen.idle(RegressSuite.DELAY_SMALL)
                    btnBook { click() }
                    tvBackground { swipeUp() }
                    tvCard { click() }
                    tvPayment { click() }
                }
            }
            step("Pay successfully") {
                PayFun.goPayCardCheckout()
                PayFun.goFinPayCheckout()
            }
            step("Check successfuly pay") {
                Payment {
                    btnSupport { isVisible() }
                    tvBookingNumber { isVisible() }
                    mapViewRoute { swipeUp() }
                    mapRoute { isVisible() }
                    tvPaidSuccessfully { isVisible() }
                    tvRemainTime  { isVisible() }
                }
            }
        }
    }
}
