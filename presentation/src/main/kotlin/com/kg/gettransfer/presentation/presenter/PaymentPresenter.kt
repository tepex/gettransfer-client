package com.kg.gettransfer.presentation.presenter

import com.arellomobile.mvp.InjectViewState
import com.kg.gettransfer.domain.eventListeners.PaymentStatusEventListener

import com.kg.gettransfer.domain.interactor.PaymentInteractor
import com.kg.gettransfer.domain.model.BookNowOffer

import com.kg.gettransfer.domain.model.Offer
import com.kg.gettransfer.domain.model.Transfer
import com.kg.gettransfer.extensions.newChainFromMain

import com.kg.gettransfer.presentation.mapper.PaymentStatusRequestMapper

import com.kg.gettransfer.presentation.model.PaymentStatusRequestModel

import com.kg.gettransfer.presentation.view.PaymentView
import com.kg.gettransfer.presentation.view.Screens

import com.kg.gettransfer.utilities.Analytics

import io.sentry.Sentry

import org.koin.core.inject

@InjectViewState
class PaymentPresenter : BasePresenter<PaymentView>(), PaymentStatusEventListener {

    private val paymentInteractor: PaymentInteractor by inject()
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
            analytics.logEvent(Analytics.EVENT_MAKE_PAYMENT, Analytics.STATUS, result.model.status.name)
        }
    }

    override fun onNewPaymentStatusEvent(isSuccess: Boolean) {
        if (isSuccess) utils.launchSuspend { isPaymentWasSuccessful() } else showFailedPayment()
    }

    private suspend fun isPaymentWasSuccessful() {
        if (isOfferPaid()) showSuccessfulPayment()
    }

    private suspend fun isOfferPaid(): Boolean {
        transfer?.let { tr ->
            fetchResult { transferInteractor.getTransfer(tr.id) }.isSuccess()?.let { transfer ->
                this.transfer = transfer
                return transfer.status == Transfer.Status.PERFORMED || transfer.paidPercentage > 0
            }
        }
        return false
    }

    private fun showFailedPayment() {
        viewState.blockInterface(false)
        router.exit()
        router.navigateTo(Screens.PaymentError(transfer!!.id))
    }

    private fun showSuccessfulPayment() {
        viewState.blockInterface(false)
        transfer?.let { router.newChainFromMain(Screens.PaymentSuccess(it.id, offer?.id)) }
        analytics.EcommercePurchase(paymentType).sendAnalytics()
    }
}
