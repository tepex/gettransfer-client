package com.kg.gettransfer.androidAutoTest

import androidx.test.espresso.web.sugar.Web

import androidx.test.espresso.web.webdriver.DriverAtoms.findElement
import androidx.test.espresso.web.webdriver.DriverAtoms.clearElement
import androidx.test.espresso.web.webdriver.DriverAtoms.webClick
import androidx.test.espresso.web.webdriver.DriverAtoms.webKeys

import androidx.test.espresso.web.webdriver.Locator.NAME
import androidx.test.espresso.web.webdriver.Locator.XPATH

object PayFun {

    fun goPayCard() {
        Web.onWebView().withElement(findElement(NAME, WebPayId.cardNum)).perform(clearElement())
        Web.onWebView().withElement(findElement(NAME, WebPayId.cardNum)).perform(webKeys("5285 0000 0000 0005"))
        Web.onWebView().withElement(findElement(NAME, WebPayId.expMon)).perform(webClick())
        Web.onWebView().withElement(findElement(NAME, WebPayId.expMon)).perform(webKeys("01"))
        Web.onWebView().withElement(findElement(NAME, WebPayId.expYear)).perform(webClick())
        Web.onWebView().withElement(findElement(NAME, WebPayId.expYear)).perform(webKeys("25"))
        Web.onWebView().withElement(findElement(NAME, WebPayId.cardCvc)).perform(webClick())
        Web.onWebView().withElement(findElement(NAME, WebPayId.cardCvc)).perform(webKeys("123"))
        Web.onWebView().withElement(findElement(NAME, WebPayId.nameCard)).perform(webClick())
        Web.onWebView().withElement(findElement(NAME, WebPayId.nameCard)).perform(webKeys("AUTO TEST"))
        Web.onWebView().withElement(findElement(NAME, WebPayId.userPhone)).perform(webClick()).perform(clearElement())
    }

    fun goFailPayCard() {
        goPayCard()
        Web.onWebView().withElement(findElement(NAME, WebPayId.userPhone)).perform(webKeys("79008888888"))
        goFinPay()
    }

    fun goGoodPayCard() {
        goPayCard()
        Web.onWebView().withElement(findElement(NAME, WebPayId.userPhone)).perform(webKeys("79009999999"))
        goFinPay()
    }

    fun goFinPay() {
        Web.onWebView().withElement(findElement(XPATH, WebPayId.XPAT))
            .perform(webClick())

        @Suppress("MagicNumber")
        Thread.sleep(9_000)
        Web.onWebView().withElement(findElement(NAME, WebPayId.threeDone)).perform(webClick())
    }
}

object WebPayId {
    const val cardNum = "card_num"
    const val expMon = "exp_month"
    const val expYear = "exp_year"
    const val cardCvc = "card_cvc"
    const val nameCard = "name_on_card"
    const val userPhone = "user_phone"
    const val threeDone = "threeDSIsDone"
    const val XPAT = "/html/body/div[1]/div[2]/div/div[2]/form/div[6]/button"
}
