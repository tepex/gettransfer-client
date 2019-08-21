package com.kg.gettransfer.presentation.presenter

import com.arellomobile.mvp.InjectViewState

import com.kg.gettransfer.domain.model.BookNowOffer
import com.kg.gettransfer.domain.model.Offer
import com.kg.gettransfer.domain.model.Transfer

import com.kg.gettransfer.extensions.newChainFromMain

import com.kg.gettransfer.presentation.model.PaymentRequestModel
import com.kg.gettransfer.presentation.view.PaypalConnectionView
import com.kg.gettransfer.presentation.view.Screens

import com.kg.gettransfer.utilities.Analytics


@InjectViewState
class PaypalConnectionPresenter : BasePresenter<PaypalConnectionView>() {

    internal var paymentId = 0L
    internal var nonce = ""
    internal var transferId = 0L
    internal var transfer: Transfer? = null
    internal var offerId = 0L
    internal var percentage = PaymentRequestModel.FULL_PRICE
    internal var bookNowTransportId: String? = null

    private var offer: Offer? = null
    private var bookNowOffer: BookNowOffer? = null

    override fun attachView(view: PaypalConnectionView) {
        super.attachView(view)
        paymentInteractor.selectedTransfer?.let { st ->
            paymentInteractor.selectedOffer?.let { so ->
                transfer = st
                when (so) {
                    is Offer        -> offer = so
                    is BookNowOffer -> bookNowOffer = so
                }
            }
        }
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

    private fun showFailedPayment() {
        analytics.PaymentStatus(PaymentRequestModel.PAYPAL).sendAnalytics(Analytics.EVENT_PAYMENT_FAILED)
        router.exit()
        router.navigateTo(Screens.PaymentError(transferId))
    }

    private fun showSuccessfulPayment() {
        router.newChainFromMain(Screens.PaymentSuccess(transferId, offerId))
        utils.launchSuspend {
            transfer?.let {
                val offerPaid = transferInteractor.isOfferPaid(it.id)
                if (offerPaid.first) {
                    transfer = offerPaid.second
                    paymentInteractor.selectedTransfer = transfer
                    analytics.PaymentStatus(PaymentRequestModel.PAYPAL).sendAnalytics(Analytics.EVENT_PAYMENT_DONE)
                    analytics.EcommercePurchase().sendAnalytics()
                }
            }
        }
    }
}
