package com.kg.gettransfer.presentation.presenter

import com.arellomobile.mvp.InjectViewState
import com.kg.gettransfer.domain.eventListeners.PaymentStatusEventListener

import com.kg.gettransfer.domain.interactor.PaymentInteractor
import com.kg.gettransfer.domain.interactor.OrderInteractor
import com.kg.gettransfer.domain.model.BookNowOffer

import com.kg.gettransfer.domain.model.Offer
import com.kg.gettransfer.domain.model.Transfer
import com.kg.gettransfer.extensions.newChainFromMain

import com.kg.gettransfer.presentation.mapper.PaymentStatusRequestMapper

import com.kg.gettransfer.presentation.model.OfferModel
import com.kg.gettransfer.presentation.model.PaymentStatusRequestModel

import com.kg.gettransfer.presentation.presenter.PaymentOfferPresenter.Companion.PRICE_30

import com.kg.gettransfer.presentation.view.PaymentView
import com.kg.gettransfer.presentation.view.Screens

import com.kg.gettransfer.utilities.Analytics
import io.sentry.Sentry

import org.koin.core.inject

import timber.log.Timber
import java.util.Currency

@InjectViewState
class PaymentPresenter : BasePresenter<PaymentView>(), PaymentStatusEventListener {
    private val paymentInteractor: PaymentInteractor by inject()
    private val mapper: PaymentStatusRequestMapper by inject()
    private val orderInteractor: OrderInteractor by inject()

    private var offer: Offer? = null
    private var bookNowOffer: BookNowOffer? = null
    private var transfer: Transfer? = null

    internal var percentage = 0
    internal var paymentType = ""

    override fun attachView(view: PaymentView) {
        super.attachView(view)
        with(paymentInteractor) {
            eventPaymentReceiver = this@PaymentPresenter
            if (selectedTransfer != null && selectedOffer != null) {
                transfer = selectedTransfer!!
                selectedOffer?.let {
                    when (it) {
                        is Offer -> offer = it
                        is BookNowOffer -> bookNowOffer = it
                    }
                }
            }
        }
    }

    override fun detachView(view: PaymentView?) {
        super.detachView(view)
        paymentInteractor.eventPaymentReceiver = null
    }

    fun changePaymentStatus(orderId: Long, success: Boolean) {
        utils.launchSuspend {
            viewState.blockInterface(true)
            val model = PaymentStatusRequestModel(null, orderId, true, success)
            val result = utils.asyncAwait { paymentInteractor.changeStatusPayment(mapper.fromView(model)) }
            if (result.error != null) {
                Timber.e(result.error!!)
                viewState.setError(result.error!!)
                router.exit()
            } else {
                if (result.model.isSuccess) {
                    isPaymentWasSuccessful()
                } else {
                    showFailedPayment()
                }
            }
        }
    }

    override fun onNewPaymentStatusEvent(isSuccess: Boolean) {
        if (isSuccess) {
            utils.launchSuspend {
               isPaymentWasSuccessful()
            }
        } else {
            showFailedPayment()
        }
    }

    private suspend fun isPaymentWasSuccessful() {
        if (isOfferPaid()) showSuccessfulPayment()
    }

    private suspend fun isOfferPaid(): Boolean {
        transfer?.let {
            fetchResult { transferInteractor.getTransfer(transfer!!.id) }
                .isSuccess()
                ?.let { transfer ->
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
        logEvent(Analytics.EVENT_MAKE_PAYMENT, Analytics.STATUS, Analytics.RESULT_FAIL)
    }

    private fun showSuccessfulPayment() {
        viewState.blockInterface(false)
        router.newChainFromMain(
            Screens.PaymentSuccess(
                transfer!!.id,
                offer?.id
            )
        )
        logEventEcommercePurchase()
    }

    private fun logEventEcommercePurchase() {
        val offerType = if (offer != null) Analytics.REGULAR else Analytics.NOW
        val requestType = when {
            transfer?.duration != null -> Analytics.TRIP_HOURLY
            transfer?.dateReturnLocal != null -> Analytics.TRIP_ROUND
            else -> Analytics.TRIP_DESTINATION
        }
        val currency = sessionInteractor.currency.code
        var price: Double = offer?.price?.amount ?: bookNowOffer?.amount ?: (-1.0).also {
            Sentry.capture(
                """when try to get offer for analytics of payment - server return invalid value:
                    |offer is null  - ${offer == null}
                    |offer.price is null  - ${offer?.price == null}
                    |offer.price.amount is null  - ${offer?.price?.amount == null}
                    |bookNowOffer is null  - ${bookNowOffer == null}
                    |bookNowOffer.amount is null  - ${bookNowOffer?.amount == null}
                 """.trimMargin()
            )
        }

        if (percentage == OfferModel.PRICE_30) price *= PRICE_30

        val purchase = analytics.EcommercePurchase(
            transfer?.id.toString(),
            transfer?.promoCode,
            orderInteractor.duration,
            paymentType,
            offerType,
            requestType,
            Currency.getInstance(sessionInteractor.currency.code),
            currency,
            price
        )
        purchase.sendAnalytics()
    }
}
