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
        Web.onWebView().withElement(findElement(NAME, "card_num")).perform(clearElement())
        Web.onWebView().withElement(findElement(NAME, "card_num")).perform(webKeys("5285 0000 0000 0005"))
        Web.onWebView().withElement(findElement(NAME, "exp_month")).perform(webClick())
        Web.onWebView().withElement(findElement(NAME, "exp_month")).perform(webKeys("01"))
        Web.onWebView().withElement(findElement(NAME, "exp_year")).perform(webClick())
        Web.onWebView().withElement(findElement(NAME, "exp_year")).perform(webKeys("25"))
        Web.onWebView().withElement(findElement(NAME, "card_cvc")).perform(webClick())
        Web.onWebView().withElement(findElement(NAME, "card_cvc")).perform(webKeys("123"))
        Web.onWebView().withElement(findElement(NAME, "name_on_card")).perform(webClick())
        Web.onWebView().withElement(findElement(NAME, "name_on_card")).perform(webKeys("AUTO TEST"))
        Web.onWebView().withElement(findElement(NAME, "user_phone")).perform(webClick()).perform(clearElement())
        Web.onWebView().withElement(findElement(NAME, "user_phone")).perform(webKeys("79009999999"))
        Web.onWebView().withElement(findElement(XPATH, "/html/body/div[1]/div[2]/div/div[2]/form/div[6]/button"))
            .perform(webClick())

        @Suppress("MagicNumber")
        Thread.sleep(9_000)
        Web.onWebView().withElement(findElement(NAME, "threeDSIsDone")).perform(webClick())
    }
}
