package com.kg.gettransfer.presentation.presenter

import com.checkout.android_sdk.Utils.CardUtils
import com.checkout.android_sdk.Utils.Environment
import com.kg.gettransfer.core.presentation.WorkerManager
import com.kg.gettransfer.domain.model.PaymentProcessRequest
import com.kg.gettransfer.domain.model.Token
import com.kg.gettransfer.domain.model.PaymentProcess
import com.kg.gettransfer.domain.model.Result
import com.kg.gettransfer.presentation.model.PaymentRequestModel
import com.kg.gettransfer.presentation.view.CheckoutcomPaymentView
import com.kg.gettransfer.utilities.CardDateFormatter
import kotlinx.coroutines.launch
import moxy.InjectViewState
import org.koin.core.inject
import org.koin.core.parameter.parametersOf
import sys.domain.CheckoutcomCredentials
import java.util.Calendar

@InjectViewState
class CheckoutcomPaymentPresenter : BaseCardPaymentPresenter<CheckoutcomPaymentView>() {

    private val worker: WorkerManager by inject { parametersOf("CheckoutcomPaymentPresenter") }

    var cardNumber = ""
        set(value) {
            field = value
            val cardType = CardUtils.getType(value)
            viewState.setCardNumberLength(cardType.maxCardLength)
            cardLength = cardType.cardLength
            maxCVCLength = cardType.maxCvvLength
            viewState.setCVCLength(cardType.maxCvvLength)
            viewState.setCardTypeIcon(cardType.resourceId)
        }

    var cardMonth = ""
    var cardYear = ""
    var cardCVC = ""

    var cardLength = CardUtils.Cards.DEFAULT.cardLength
    var maxCVCLength = CardUtils.Cards.DEFAULT.maxCvvLength

    override fun attachView(view: CheckoutcomPaymentView) {
        super.attachView(view)
        gatewayId = PaymentRequestModel.CHECKOUTCOM

        viewState.setCVCLength(maxCVCLength)

        worker.main.launch {
            val checkoutcomCredentials = configsManager.getConfigs().checkoutcomCredentials
            val environment = when (checkoutcomCredentials.environment) {
                CheckoutcomCredentials.ENVIRONMENT_LIVE -> Environment.LIVE
                else                                    -> Environment.SANDBOX
            }
            viewState.initPaymentForm(environment, checkoutcomCredentials.publicKey)
        }
    }

    fun onPayButtonPressed() {
        if (!cardInfoDataIsValid()) return
        viewState.generateToken(
            cardNumber,
            cardMonth,
            cardYear,
            cardCVC
        )
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
