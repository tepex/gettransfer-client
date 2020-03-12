package com.kg.gettransfer.presentation.androidAutoTest

import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.espresso.web.sugar.Web.onWebView
import androidx.test.espresso.web.webdriver.DriverAtoms.*

import androidx.test.espresso.web.webdriver.Locator
import androidx.test.espresso.web.webdriver.Locator.*
import com.agoda.kakao.screen.Screen
import com.kg.gettransfer.R

import com.kg.gettransfer.presentation.data.Constants
import org.hamcrest.Matchers
import org.hamcrest.Matchers.allOf

object PayFun {

    fun goPayCardPlatron() {
        onWebView().withElement(findElement(NAME, WebPayId.cardNumPlatron)).perform(clearElement())
        onWebView().withElement(findElement(NAME, WebPayId.cardNumPlatron))
                .perform(webKeys("5285 0000 0000 0005"))
        onWebView().withElement(findElement(NAME, WebPayId.expMonPlatron)).perform(webClick())
        onWebView().withElement(findElement(NAME, WebPayId.expMonPlatron)).perform(webKeys("01"))
        onWebView().withElement(findElement(NAME, WebPayId.expYearPlatron)).perform(webClick())
        onWebView().withElement(findElement(NAME, WebPayId.expYearPlatron)).perform(webKeys("25"))
        onWebView().withElement(findElement(NAME, WebPayId.cardCvcPlatron)).perform(webClick())
        onWebView().withElement(findElement(NAME, WebPayId.cardCvcPlatron)).perform(webKeys("123"))
        onWebView().withElement(findElement(NAME, WebPayId.nameCardPlatron)).perform(webClick())
        onWebView().withElement(findElement(NAME, WebPayId.nameCardPlatron)).perform(webKeys("AUTO TEST"))
        onWebView().withElement(findElement(NAME, WebPayId.userPhonePlatron))
                .perform(webClick()).perform(clearElement())
    }

    fun goFailPayCardPlatron() {
        goPayCardPlatron()
        onWebView().withElement(findElement(NAME, WebPayId.userPhonePlatron))
                .perform(webKeys("79008888888"))
        goFinPayPlatron()
    }

    fun goGoodPayCardPlatron() {
        goPayCardPlatron()
        onWebView().withElement(findElement(NAME, WebPayId.userPhonePlatron))
                .perform(webKeys("79009999999"))
        goFinPayPlatron()
    }

    fun goFinPayPlatron() {
        onWebView().withElement(findElement(XPATH, WebPayId.XPATHPLATRON)).perform(webClick())
        Thread.sleep(Constants.big)
        onWebView().withElement(findElement(NAME, WebPayId.threeDonePlatron)).perform(webClick())
    }

    fun goPayCardCheckout() {
        onView(allOf(withId(R.id.cardNumber), isDescendantOfA(withId(R.id.cardInfo))))
                .perform(click())
        onView(allOf(withId(R.id.field_input), isDescendantOfA(withId(R.id.cardNumber))))
                .perform(typeText("4242424242424242"))
        onView(allOf(withId(R.id.field_input), isDescendantOfA(withId(R.id.cardDate))))
                .perform(typeText("0125"))
        onView(allOf(withId(R.id.field_input), isDescendantOfA(withId(R.id.cardCVC))))
                .perform(typeText("100"))
                .perform(closeSoftKeyboard())
        onView(allOf(withId(R.id.payButton), isDisplayed()))
                .perform(click())
    }

    fun goFinPayCheckout() {
        Screen.idle(Constants.big)
        onWebView()
                .inWindow(selectFrameByIdOrName(WebPayId.NAMEIFRAMECHECKOUT))
                .withElement(findElement(Locator.XPATH, WebPayId.BTNXPATHCHECKOUT))
                .perform(webKeys(WebPayId.CODECHECKOUT))
        onWebView()
                .inWindow(selectFrameByIdOrName(WebPayId.NAMEIFRAMECHECKOUT))
                .withElement(findElement(Locator.XPATH, WebPayId.TXTFLDXPATHCHECKOUT))
                .perform(webClick())
        Screen.idle(Constants.big)
    }
}

object WebPayId {
    const val cardNumPlatron = "card_num"
    const val expMonPlatron = "exp_month"
    const val expYearPlatron = "exp_year"
    const val cardCvcPlatron = "card_cvc"
    const val nameCardPlatron = "name_on_card"
    const val userPhonePlatron = "user_phone"
    const val threeDonePlatron = "threeDSIsDone"
    const val XPATHPLATRON = "/html/body/div[1]/div[2]/div/div[2]/form/div[7]/button/span"
    const val BTNXPATHCHECKOUT = "/html/body/div[2]/div/form/div/input"
    const val TXTFLDXPATHCHECKOUT = "/html/body/div[2]/div/form/input[2]"
    const val NAMEIFRAMECHECKOUT = "cko-3ds2-iframe"
    const val CODECHECKOUT = "Checkout1!"
}
