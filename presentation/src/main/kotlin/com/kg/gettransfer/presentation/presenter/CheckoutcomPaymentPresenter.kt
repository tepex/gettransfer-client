package com.kg.gettransfer.presentation.presenter

import com.checkout.android_sdk.Utils.CardUtils
import com.checkout.android_sdk.Utils.Environment
import com.kg.gettransfer.R

import com.kg.gettransfer.domain.model.CheckoutcomTokenRequest
import com.kg.gettransfer.domain.model.PaymentProcess
import com.kg.gettransfer.domain.model.PaymentProcessRequest
import com.kg.gettransfer.domain.model.PaymentRequest
import com.kg.gettransfer.domain.model.Result
import com.kg.gettransfer.domain.model.Token

import com.kg.gettransfer.presentation.view.CheckoutcomPaymentView
import com.kg.gettransfer.utilities.CardDateFormatter

import java.util.Calendar

import moxy.InjectViewState

import sys.domain.CheckoutcomCredentials

@InjectViewState
class CheckoutcomPaymentPresenter : BaseCardPaymentPresenter<CheckoutcomPaymentView>() {

    var cardNumber = ""
        set(value) {
            field = value
            val cardType = CardUtils.getType(value)
            viewState.setCardNumberLength(cardType.maxCardLength)
            cardLength = cardType.cardLength
            maxCVCLength = cardType.maxCvvLength
            viewState.setCVCLength(cardType.maxCvvLength)
            setCardType(cardNumber)
        }

    var cardMonth = ""
    var cardYear = ""
    var cardCVC = ""

    var cardLength = CardUtils.Cards.DEFAULT.cardLength
    var maxCVCLength = CardUtils.Cards.DEFAULT.maxCvvLength

    override fun attachView(view: CheckoutcomPaymentView) {
        super.attachView(view)
        gateway = PaymentRequest.Gateway.CHECKOUTCOM

        viewState.setCVCLength(maxCVCLength)
    }

    private fun setCardType(cardNUmber: String) {
        when (CardUtils.getType(cardNUmber)) {
            CardUtils.Cards.AMEX -> viewState.setCardTypeIcon(R.drawable.ic_amex)
            CardUtils.Cards.DISCOVER -> viewState.setCardTypeIcon(R.drawable.ic_discover)
            CardUtils.Cards.MAESTRO -> viewState.setCardTypeIcon(R.drawable.ic_maestro)
            CardUtils.Cards.MASTERCARD -> viewState.setCardTypeIcon(R.drawable.ic_master_card)
            CardUtils.Cards.VISA -> viewState.setCardTypeIcon(R.drawable.ic_visa_2)
            else -> viewState.setCardTypeIcon(0)
        }
    }


    fun onPayButtonPressed() {
        if (!cardInfoDataIsValid()) return
        utils.launchSuspend {
            val checkoutcomCredentials = configsManager.getConfigs().checkoutcomCredentials
            val url = when (checkoutcomCredentials.environment) {
                CheckoutcomCredentials.ENVIRONMENT_LIVE -> Environment.LIVE
                else                                    -> Environment.SANDBOX
            }.token
            val key = checkoutcomCredentials.publicKey
            val tokenRequest =
                CheckoutcomTokenRequest(CheckoutcomTokenRequest.CARD, cardNumber, cardMonth, cardYear, cardCVC)
            fetchResultOnly { paymentInteractor.getCheckoutcomToken(tokenRequest, url, key) }.let { result ->
                result.isSuccess()?.token?.let { onTokenGenerated(it) }
                result.error?.let { viewState.setError(it) }
            }
        }
    }

    private fun cardInfoDataIsValid(): Boolean {
        val errorFields = arrayListOf<Int>().apply {
            if (!cardNumberIsValid()) add(CARD_NUMBER_ERROR)
            if (!cardDateIsValid())   add(CARD_DATE_ERROR)
            if (!cardCVCIsValid())    add(CARD_CVC_ERROR)
        }
        if (errorFields.isEmpty()) return true
        viewState.highLightErrorField(errorFields)
        return false
    }

    private fun cardNumberIsValid() = CardUtils.isValidCard(cardNumber) && cardNumber.length in cardLength

    private fun cardDateIsValid(): Boolean {
        if (cardMonth.length != MONTH_YEAR_LENGTH || cardYear.length != MONTH_YEAR_LENGTH) return false
        val currentDate = Calendar.getInstance()
        val currentYear = currentDate.get(Calendar.YEAR) % CardDateFormatter.YEARS_IN_CENTURY
        val currentMonth = currentDate.get(Calendar.MONTH)
        return when {
            cardYear.toInt() < currentYear                                      -> false
            cardYear.toInt() == currentYear && cardMonth.toInt() < currentMonth -> false
            else                                                                -> true
        }
    }

    private fun cardCVCIsValid() = cardCVC.length == maxCVCLength

    fun onTokenGenerated(token: String) {
        utils.launchSuspend {
            viewState.blockInterface(true, true)
            val paymentProcess = PaymentProcessRequest(paymentId, Token.StringToken(token))
            val processResult = getProcessPaymentResult(paymentProcess)
            checkPaymentResult(processResult)
            viewState.blockInterface(false)
        }
    }

    private suspend fun getProcessPaymentResult(paymentProcess: PaymentProcessRequest): Result<PaymentProcess> =
        utils.asyncAwait { paymentInteractor.processPayment(paymentProcess) }

    private suspend fun checkPaymentResult(result: Result<PaymentProcess>) {
        result.error?.let {
            showFailedPayment(it)
        } ?: result.model.redirect?.let { redirectUrl ->
            viewState.redirectTo3ds(redirectUrl)
        } ?: result.model.payment.isSuccess.let {
            if (it) isPaymentWasSuccessful() else showFailedPayment()
        }
    }

    companion object {
        const val CARD_DATE_LENGTH = 5
        const val MONTH_YEAR_LENGTH = 2

        const val CARD_NUMBER_ERROR = 0
        const val CARD_DATE_ERROR   = 1
        const val CARD_CVC_ERROR    = 2
    }
}
