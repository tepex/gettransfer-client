package com.kg.gettransfer.presentation.presenter

import com.kg.gettransfer.core.presentation.WorkerManager
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
import org.koin.core.parameter.parametersOf

open class BaseCardPaymentPresenter<BV : BaseView> : BasePresenter<BV>(), PaymentStatusEventListener {

    private val worker: WorkerManager by inject { parametersOf("BaseCardPaymentPresenter") }

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

    fun changePaymentStatus(isSuccess: Boolean, failureDescription: String? = null) = utils.launchSuspend {
        viewState.blockInterface(true)
        val model = PaymentStatusRequestModel(paymentId, isSuccess, failureDescription)
        val result = utils.asyncAwait { paymentInteractor.changeStatusPayment(mapper.fromView(model)) }
        result.error?.let {
            showFailedPayment(it)
        } ?: result.model.let { status ->
            when {
                status.isSuccess -> isPaymentWasSuccessful()
                status.isFailed  -> showFailedPayment()
                else             -> viewState.blockInterface(true, true)
            }
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
    }
}
