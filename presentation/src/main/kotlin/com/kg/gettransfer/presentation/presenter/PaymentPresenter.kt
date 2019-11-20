package com.kg.gettransfer.presentation.presenter

import moxy.InjectViewState
import com.kg.gettransfer.domain.eventListeners.PaymentStatusEventListener

import com.kg.gettransfer.domain.model.BookNowOffer

import com.kg.gettransfer.domain.model.Offer
import com.kg.gettransfer.domain.model.Transfer
import com.kg.gettransfer.extensions.newChainFromMain

import com.kg.gettransfer.presentation.mapper.PaymentStatusRequestMapper

import com.kg.gettransfer.presentation.model.PaymentStatusRequestModel

import com.kg.gettransfer.presentation.view.PaymentView
import com.kg.gettransfer.presentation.view.Screens

import com.kg.gettransfer.utilities.Analytics

import org.koin.core.inject

@InjectViewState
class PaymentPresenter : BasePresenter<PaymentView>(), PaymentStatusEventListener {

    private val mapper: PaymentStatusRequestMapper by inject()

    private var offer: Offer? = null
    private var bookNowOffer: BookNowOffer? = null
    private var transfer: Transfer? = null

    internal var percentage = 0
    internal var paymentType = ""

    private var showSuccessPayment = false
    private var showFailedPayment = false

    override fun attachView(view: PaymentView) {
        super.attachView(view)
        paymentInteractor.eventPaymentReceiver = this
        paymentInteractor.selectedTransfer?.let { st ->
            paymentInteractor.selectedOffer?.let { so ->
                transfer = st
                when (so) {
                    is Offer -> offer = so
                    is BookNowOffer -> bookNowOffer = so
                }
            }
        }
    }

    override fun detachView(view: PaymentView?) {
        super.detachView(view)
        paymentInteractor.eventPaymentReceiver = null
    }

    fun changePaymentStatus(orderId: Long, success: Boolean) = utils.launchSuspend {
        viewState.blockInterface(true)
        val model = PaymentStatusRequestModel(null, orderId, true, success)
        val result = utils.asyncAwait { paymentInteractor.changeStatusPayment(mapper.fromView(model)) }
        val err = result.error
        if (err != null) {
            log.error("change payment status error", err)
            viewState.setError(err)
            router.exit()
        } else {
            if (result.model.isSuccess) isPaymentWasSuccessful() else showFailedPayment()
        }
    }

    override fun onNewPaymentStatusEvent(isSuccess: Boolean) {
        utils.launchSuspend { if (isSuccess) isPaymentWasSuccessful() else showFailedPayment() }
    }

    private suspend fun isPaymentWasSuccessful() {
        transfer?.let {
            val offerPaid = utils.asyncAwait { transferInteractor.isOfferPaid(it.id) }
            if (offerPaid.model.first) {
                transfer = offerPaid.model.second
                paymentInteractor.selectedTransfer = transfer
                showSuccessfulPayment()
            }
        }
    }

    private suspend fun showFailedPayment() {
        if (!showFailedPayment) {
            showFailedPayment = true
            viewState.blockInterface(false)
            router.exit()
            transfer?.let { router.navigateTo(Screens.PaymentError(it.id)) }
            analytics.PaymentStatus(paymentType).sendAnalytics(Analytics.EVENT_PAYMENT_FAILED)
        }
    }

    private suspend fun showSuccessfulPayment() {
        if (!showSuccessPayment) {
            showSuccessPayment = true
            viewState.blockInterface(false)
            transfer?.let { router.newChainFromMain(Screens.PaymentSuccess(it.id, offer?.id)) }
            analytics.PaymentStatus(paymentType).sendAnalytics(Analytics.EVENT_PAYMENT_DONE)
            analytics.EcommercePurchase().sendAnalytics()
        }
    }
}
