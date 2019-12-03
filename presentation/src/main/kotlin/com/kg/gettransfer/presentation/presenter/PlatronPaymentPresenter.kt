package com.kg.gettransfer.presentation.presenter

import moxy.InjectViewState
import com.kg.gettransfer.domain.eventListeners.PaymentStatusEventListener

import com.kg.gettransfer.domain.model.Offer
import com.kg.gettransfer.extensions.newChainFromMain

import com.kg.gettransfer.presentation.mapper.PaymentStatusRequestMapper
import com.kg.gettransfer.presentation.model.PaymentRequestModel

import com.kg.gettransfer.presentation.model.PaymentStatusRequestModel

import com.kg.gettransfer.presentation.view.PlatronPaymentView
import com.kg.gettransfer.presentation.view.Screens

import com.kg.gettransfer.utilities.Analytics

import org.koin.core.inject

@InjectViewState
class PlatronPaymentPresenter : BasePresenter<PlatronPaymentView>(), PaymentStatusEventListener {

    private val mapper: PaymentStatusRequestMapper by inject()

    private var showSuccessPayment = false
    private var showFailedPayment = false

    override fun attachView(view: PlatronPaymentView) {
        super.attachView(view)
        paymentInteractor.eventPaymentReceiver = this
    }

    override fun detachView(view: PlatronPaymentView?) {
        super.detachView(view)
        paymentInteractor.eventPaymentReceiver = null
    }

    fun changePaymentStatus(orderId: Long, success: Boolean) = utils.launchSuspend {
        viewState.blockInterface(true)
        val model = PaymentStatusRequestModel(null, orderId, true, success)
        val result = utils.asyncAwait { paymentInteractor.changeStatusPayment(mapper.fromView(model)) }
        result.error?.let {
            log.error("change payment status error", it)
            viewState.setError(it)
            router.exit()
        } ?: result.model.isSuccess.let {
            if (it) isPaymentWasSuccessful() else showFailedPayment()
        }
    }

    override fun onNewPaymentStatusEvent(isSuccess: Boolean) {
        utils.launchSuspend {
            if (isSuccess) isPaymentWasSuccessful() else showFailedPayment()
        }
    }

    private suspend fun isPaymentWasSuccessful() {
        paymentInteractor.selectedTransfer?.let {
            val offerPaid = utils.asyncAwait { transferInteractor.isOfferPaid(it.id) }
            if (offerPaid.model.first) {
                paymentInteractor.selectedTransfer = offerPaid.model.second
                showSuccessfulPayment(it.id)
            }
        }
    }

    private suspend fun showFailedPayment() {
        if (!showFailedPayment) {
            showFailedPayment = true
            viewState.blockInterface(false)
            router.exit()
            paymentInteractor.isFailedPayment = true
            analytics.PaymentStatus(PaymentRequestModel.PLATRON).sendAnalytics(Analytics.EVENT_PAYMENT_FAILED)
        }
    }

    private suspend fun showSuccessfulPayment(transferId: Long) {
        if (!showSuccessPayment) {
            showSuccessPayment = true
            viewState.blockInterface(false)
            analytics.PaymentStatus(PaymentRequestModel.PLATRON).sendAnalytics(Analytics.EVENT_PAYMENT_DONE)
            val offerId = (paymentInteractor.selectedOffer as? Offer)?.id
            router.newChainFromMain(Screens.PaymentSuccess(transferId, offerId))
            analytics.EcommercePurchase().sendAnalytics()
        }
    }
}
