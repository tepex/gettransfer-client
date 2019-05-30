package com.kg.gettransfer.presentation.presenter

import com.arellomobile.mvp.InjectViewState

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

import org.koin.standalone.inject

import timber.log.Timber
import java.util.Currency

@InjectViewState
class PaymentPresenter : BasePresenter<PaymentView>() {
    private val paymentInteractor: PaymentInteractor by inject()
    private val mapper: PaymentStatusRequestMapper by inject()
    private val orderInteractor: OrderInteractor by inject()

    private var offer: Offer? = null
    private var bookNowOffer: BookNowOffer? = null
    private var transfer: Transfer? = null

    internal var transferId = 0L
    internal var offerId = 0L
    internal var percentage = 0
    internal var bookNowTransportId = ""
    internal var paymentType = ""

    override fun attachView(view: PaymentView) {
        super.attachView(view)
        offer = offerInteractor.getOffer(offerId)
        utils.launchSuspend {
            val result = utils.asyncAwait { transferInteractor.getTransfer(transferId) }
            if (result.error == null || (result.error != null && result.fromCache)) {
                transfer = result.model
                if (offer == null) {
                    transfer?.let {
                        if (it.bookNowOffers.isNotEmpty() && bookNowTransportId.isNotEmpty()) {
                            bookNowOffer = it.bookNowOffers.find { bookNowOffer ->
                                bookNowOffer.transportType.id.name == bookNowTransportId
                            }
                        }
                    }
                }
            } else {
                Sentry.capture(result.error)
            }
        }
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
                if (result.model.success) {
                    showSuccessfulPayment()
                } else {
                    showFailedPayment()
                }
            }
            viewState.blockInterface(false)
        }
    }

    private fun showFailedPayment() {
        router.exit()
        router.navigateTo(Screens.PaymentError(transferId))
        logEvent(Analytics.RESULT_FAIL)
    }

    private fun showSuccessfulPayment() {
        router.newChainFromMain(
                Screens.PaymentSuccess(
                        transferId,
                        offerId
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
            transferId.toString(),
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

    private fun logEvent(value: String) {
        val map = mutableMapOf<String, Any>()
        map[Analytics.STATUS] = value
        analytics.logEvent(Analytics.EVENT_MAKE_PAYMENT, createStringBundle(Analytics.STATUS, value), map)
    }
}
