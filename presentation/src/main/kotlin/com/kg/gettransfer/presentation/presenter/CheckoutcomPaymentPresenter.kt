package com.kg.gettransfer.presentation.presenter

import com.checkout.android_sdk.Utils.Environment
import com.kg.gettransfer.core.presentation.WorkerManager
import com.kg.gettransfer.domain.model.PaymentProcess
import com.kg.gettransfer.domain.model.PaymentProcessRequest
import com.kg.gettransfer.domain.model.Token
import com.kg.gettransfer.domain.model.Result
import com.kg.gettransfer.presentation.model.PaymentRequestModel
import com.kg.gettransfer.presentation.view.CheckoutcomPaymentView
import kotlinx.coroutines.launch
import moxy.InjectViewState
import org.koin.core.inject
import org.koin.core.parameter.parametersOf
import sys.domain.CheckoutcomCredentials

@InjectViewState
class CheckoutcomPaymentPresenter : BaseCardPaymentPresenter<CheckoutcomPaymentView>() {

    private val worker: WorkerManager by inject { parametersOf("CheckoutcomPaymentPresenter") }

    override fun attachView(view: CheckoutcomPaymentView) {
        super.attachView(view)
        gatewayId = PaymentRequestModel.CHECKOUTCOM
        worker.main.launch {
            val checkoutcomCredentials = configsManager.getConfigs().checkoutcomCredentials
            val environment = when (checkoutcomCredentials.environment) {
                CheckoutcomCredentials.ENVIRONMENT_LIVE -> Environment.LIVE
                else                                    -> Environment.SANDBOX
            }
            viewState.initPaymentForm(environment, checkoutcomCredentials.publicKey)
        }
    }

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
        viewState.clearForm()
        result.error?.let {
            showFailedPayment(it)
        } ?: result.model.redirect?.let { redirectUrl ->
            viewState.redirectTo3ds(redirectUrl)
        } ?: result.model.payment.isSuccess.let {
            if (it) isPaymentWasSuccessful() else showFailedPayment()
        }
    }

    override fun onBackCommandClick() {
        viewState.clearForm()
        super.onBackCommandClick()
    }
}
