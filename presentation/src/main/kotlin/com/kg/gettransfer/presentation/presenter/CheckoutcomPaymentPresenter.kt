package com.kg.gettransfer.presentation.presenter

import com.checkout.android_sdk.Utils.Environment
import com.kg.gettransfer.core.presentation.WorkerManager
import com.kg.gettransfer.domain.ApiException
import com.kg.gettransfer.domain.model.PaymentProcess
import com.kg.gettransfer.domain.model.Result
import com.kg.gettransfer.domain.model.Offer
import com.kg.gettransfer.extensions.newChainFromMain
import com.kg.gettransfer.presentation.mapper.PaymentProcessRequestMapper
import com.kg.gettransfer.presentation.model.PaymentProcessRequestModel
import com.kg.gettransfer.presentation.model.PaymentRequestModel
import com.kg.gettransfer.presentation.view.CheckoutcomPaymentView
import com.kg.gettransfer.presentation.view.Screens
import com.kg.gettransfer.utilities.Analytics
import kotlinx.coroutines.launch
import moxy.InjectViewState
import org.koin.core.inject
import org.koin.core.parameter.parametersOf

@InjectViewState
class CheckoutcomPaymentPresenter: BasePresenter<CheckoutcomPaymentView>() {

    private val worker: WorkerManager by inject { parametersOf("CheckoutcomPaymentPresenter") }

    private val paymentProcessRequestMapper: PaymentProcessRequestMapper by inject()

    internal var paymentId = 0L

    override fun attachView(view: CheckoutcomPaymentView) {
        super.attachView(view)
        worker.main.launch {
            val checkoutcomCredentials = configsManager.getConfigs().checkoutcomCredentials
            val environment = when(checkoutcomCredentials.environment) {
                "live" -> Environment.LIVE
                else   -> Environment.SANDBOX
            }
            viewState.initPaymentForm(environment, checkoutcomCredentials.publicKey)
        }
    }

    fun onTokenGenerated(token: String) {
        utils.launchSuspend {
            viewState.blockInterface(true, true)
            val paymentProcess = PaymentProcessRequestModel(paymentId, token, true)
            val processResult = getProcessPaymentResult(paymentProcess)
            checkPaymentResult(processResult)
            viewState.blockInterface(false)
        }
    }

    private suspend fun getProcessPaymentResult(paymentProcessModel: PaymentProcessRequestModel): Result<PaymentProcess> =
        utils.asyncAwait { paymentInteractor.processPayment(paymentProcessRequestMapper.fromView(paymentProcessModel)) }

    private suspend fun checkPaymentResult(result: Result<PaymentProcess>) {
        viewState.clearForm()
        result.error?.let {
            paymentError(it)
        } ?: result.model.redirect?.let { redirectUrl ->
            viewState.redirectTo3ds(
                redirectUrl,
                PAYMENT_RESULT_SUCCESSFUL,
                PAYMENT_RESULT_FAILED
            )
        } ?: paymentSuccess()
    }

    fun handle3DS(isSuccess: Boolean) {
        utils.launchSuspend {
            if (isSuccess) paymentSuccess()
            else paymentError()
        }
    }

    private suspend fun paymentError(err: ApiException? = null) {
        val gatewayId = PaymentRequestModel.CHECKOUTCOM
        log.error("get by $gatewayId payment error", err)
        analytics.PaymentStatus(gatewayId).sendAnalytics(Analytics.EVENT_PAYMENT_FAILED)
        paymentInteractor.selectedTransfer?.id?.let {
            router.exit()
            router.navigateTo(Screens.PaymentError(it))
        }
    }

    private suspend fun paymentSuccess() {
        analytics.PaymentStatus(PaymentRequestModel.CHECKOUTCOM).sendAnalytics(Analytics.EVENT_PAYMENT_DONE)
        paymentInteractor.selectedTransfer?.let {
            val offerId = (paymentInteractor.selectedOffer as? Offer)?.id
            router.newChainFromMain(Screens.PaymentSuccess(it.id, offerId))
            val offerPaid = utils.asyncAwait { transferInteractor.isOfferPaid(it.id) }
            if (offerPaid.model.first) {
                paymentInteractor.selectedTransfer = offerPaid.model.second
                analytics.EcommercePurchase().sendAnalytics()
            }
        }
    }

    override fun onBackCommandClick() {
        viewState.clearForm()
        super.onBackCommandClick()
    }

    companion object {
        private const val PAYMENT_RESULT_SUCCESSFUL = "/api/payments/successful"
        private const val PAYMENT_RESULT_FAILED     = "/api/payments/failed"
    }

}