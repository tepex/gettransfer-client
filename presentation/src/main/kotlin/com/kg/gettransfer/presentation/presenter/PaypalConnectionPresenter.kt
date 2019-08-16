package com.kg.gettransfer.presentation.presenter

import com.arellomobile.mvp.InjectViewState

import com.kg.gettransfer.domain.interactor.PaymentInteractor
import com.kg.gettransfer.domain.model.BookNowOffer
import com.kg.gettransfer.domain.model.Offer
import com.kg.gettransfer.domain.model.Transfer

import com.kg.gettransfer.extensions.newChainFromMain

import com.kg.gettransfer.presentation.model.PaymentRequestModel
import com.kg.gettransfer.presentation.view.PaypalConnectionView
import com.kg.gettransfer.presentation.view.Screens

import com.kg.gettransfer.utilities.Analytics

import org.koin.core.inject

@InjectViewState
class PaypalConnectionPresenter : BasePresenter<PaypalConnectionView>() {

    private val paymentInteractor: PaymentInteractor by inject()

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
        analytics.logEvent(Analytics.EVENT_MAKE_PAYMENT, Analytics.STATUS, result.model.status.name)
        viewState.stopAnimation()
    }

    private fun showFailedPayment() {
        router.exit()
        router.navigateTo(Screens.PaymentError(transferId))
    }

    private fun showSuccessfulPayment() {
        router.newChainFromMain(Screens.PaymentSuccess(transferId, offerId))
        utils.launchSuspend {
            if (isOfferPaid()) {
                analytics.EcommercePurchase().sendAnalytics()
            }
        }
    }

    private suspend fun isOfferPaid(): Boolean {
        transfer?.let { tr ->
            fetchResult { transferInteractor.getTransfer(tr.id) }.isSuccess()?.let { transfer ->
                this.transfer = transfer
                paymentInteractor.selectedTransfer = transfer
                return transfer.status == Transfer.Status.PERFORMED || transfer.paidPercentage > 0
            }
        }
        return false
    }
}
