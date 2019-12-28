package com.kg.gettransfer.presentation.presenter

import android.os.Handler
import com.kg.gettransfer.domain.ApiException
import com.kg.gettransfer.domain.eventListeners.PaymentStatusEventListener
import com.kg.gettransfer.domain.model.Offer
import com.kg.gettransfer.extensions.newChainFromMain
import com.kg.gettransfer.presentation.mapper.PaymentStatusRequestMapper
import com.kg.gettransfer.presentation.model.PaymentStatusRequestModel
import com.kg.gettransfer.presentation.view.BaseView
import com.kg.gettransfer.presentation.view.Screens
import com.kg.gettransfer.utilities.Analytics
import org.koin.core.inject

open class BaseCardPaymentPresenter<BV : BaseView> : BasePresenter<BV>(), PaymentStatusEventListener {

    private val mapper: PaymentStatusRequestMapper by inject()

    internal var paymentId = 0L
    lateinit var gatewayId: String

    private var showSuccessPayment = false
    private var showFailedPayment = false

    override fun attachView(view: BV) {
        super.attachView(view)
        paymentInteractor.eventPaymentReceiver = this
    }

    override fun detachView(view: BV) {
        super.detachView(view)
        paymentInteractor.eventPaymentReceiver = null
    }

    fun changePaymentStatus(isSuccess: Boolean, failureDescription: String? = null) {
        viewState.blockInterface(true, true)
        if (isSuccess) {
            Handler().postDelayed({ checkPaymentStatus(isSuccess, failureDescription) }, PAYMENT_STATUS_REQUEST_DELAY)
        } else {
            checkPaymentStatus(isSuccess, failureDescription)
        }
    }

    private fun checkPaymentStatus(isSuccess: Boolean, failureDescription: String? = null) = utils.launchSuspend {
        val model = PaymentStatusRequestModel(paymentId, isSuccess, failureDescription)
        val result = utils.asyncAwait { paymentInteractor.changeStatusPayment(mapper.fromView(model)) }
        result.error?.let {
            showFailedPayment(it)
        } ?: result.model.let { status ->
            when {
                status.isSuccess -> isPaymentWasSuccessful()
                status.isFailed  -> showFailedPayment()
                else             -> waitingPaymentStatus()
            }
        }
    }

    private fun waitingPaymentStatus() {
        paymentInteractor.selectedTransfer?.id?.let { transferId ->
            Handler().postDelayed({ checkTransferStatus(transferId) }, TRANSFER_REQUEST_DELAY)
        }
    }

    private fun checkTransferStatus(transferId: Long) = utils.launchSuspend {
        val result = utils.asyncAwait { transferInteractor.getTransfer(transferId) }
        result.isSuccess()?.let { transfer ->
            if (transfer.price != null) isPaymentWasSuccessful()
            else waitingPaymentStatus()
        }
    }

    override fun onNewPaymentStatusEvent(isSuccess: Boolean) {
        utils.launchSuspend {
            if (isSuccess) isPaymentWasSuccessful() else showFailedPayment()
        }
    }

    suspend fun isPaymentWasSuccessful() {
        paymentInteractor.selectedTransfer?.id?.let { transferId ->
            val offerPaid = utils.asyncAwait { transferInteractor.isOfferPaid(transferId) }
            if (offerPaid.model.first) {
                paymentInteractor.selectedTransfer = offerPaid.model.second
                showSuccessfulPayment(transferId)
            }
        }
    }

    private suspend fun showSuccessfulPayment(transferId: Long) {
        if (!showSuccessPayment) {
            showSuccessPayment = true
            viewState.blockInterface(false)
            analytics.PaymentStatus(gatewayId).sendAnalytics(Analytics.EVENT_PAYMENT_DONE)
            val offerId = (paymentInteractor.selectedOffer as? Offer)?.id
            router.newChainFromMain(Screens.PaymentSuccess(transferId, offerId))
            analytics.EcommercePurchase().sendAnalytics()
        }
    }

    suspend fun showFailedPayment(err: ApiException? = null) {
        if (!showFailedPayment) {
            showFailedPayment = true
            viewState.blockInterface(false)
            err?.let { e ->
                log.error("get by $gatewayId payment error", e)
                viewState.setError(e)
            }
            analytics.PaymentStatus(gatewayId).sendAnalytics(Analytics.EVENT_PAYMENT_FAILED)
            paymentInteractor.isFailedPayment = true
            router.exit()
        }
    }

    companion object {
        const val PAYMENT_RESULT_SUCCESSFUL = "/api/payments/successful"
        const val PAYMENT_RESULT_FAILED     = "/api/payments/failed"

        const val PAYMENT_STATUS_REQUEST_DELAY = 2_000L
        const val TRANSFER_REQUEST_DELAY       = 10_000L
    }
}
