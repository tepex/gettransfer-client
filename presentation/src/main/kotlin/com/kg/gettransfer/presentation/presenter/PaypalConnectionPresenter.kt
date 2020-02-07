package com.kg.gettransfer.presentation.presenter

import moxy.InjectViewState

import com.kg.gettransfer.domain.model.PaymentRequest

import com.kg.gettransfer.extensions.newChainFromMain

import com.kg.gettransfer.presentation.view.PaypalConnectionView
import com.kg.gettransfer.presentation.view.Screens

import com.kg.gettransfer.utilities.Analytics

@InjectViewState
class PaypalConnectionPresenter : BasePresenter<PaypalConnectionView>() {

    internal var paymentId = 0L
    internal var nonce = ""
    internal var transferId = 0L
    internal var offerId = 0L

    override fun attachView(view: PaypalConnectionView) {
        super.attachView(view)
        confirmPayment()
    }

    private fun confirmPayment() = utils.launchSuspend {
        val result = utils.asyncAwait { paymentInteractor.confirmPaypal(paymentId, nonce) }
        result.error?.let { err ->
            log.error("confirm payment error", err)
            viewState.setError(err)
            viewState.blockInterface(false)
            showFailedPayment()
        } ?: if (result.model.isSuccess) showSuccessfulPayment() else showFailedPayment()
        viewState.blockInterface(false)
        viewState.stopAnimation()
    }

    private suspend fun showFailedPayment() {
        analytics.PaymentStatus(PaymentRequest.Gateway.BRAINTREE).sendAnalytics(Analytics.EVENT_PAYMENT_FAILED)
        paymentInteractor.isFailedPayment = true
        router.exit()
    }

    private suspend fun showSuccessfulPayment() {
        analytics.PaymentStatus(PaymentRequest.Gateway.BRAINTREE).sendAnalytics(Analytics.EVENT_PAYMENT_DONE)
        router.newChainFromMain(Screens.PaymentSuccess(transferId, offerId))
        paymentInteractor.selectedTransfer?.let {
            val offerPaid = utils.asyncAwait { transferInteractor.isOfferPaid(it.id) }
            if (offerPaid.model.first) {
                paymentInteractor.selectedTransfer = offerPaid.model.second
                analytics.EcommercePurchase().sendAnalytics()
            }
        }
    }
}
