package com.kg.gettransfer.presentation.presenter

import android.support.annotation.CallSuper
import com.arellomobile.mvp.InjectViewState
import com.kg.gettransfer.domain.interactor.PaymentInteractor
import com.kg.gettransfer.domain.interactor.RouteInteractor
import com.kg.gettransfer.domain.model.BookNowOffer
import com.kg.gettransfer.domain.model.Offer
import com.kg.gettransfer.domain.model.Transfer
import com.kg.gettransfer.presentation.model.OfferModel
import com.kg.gettransfer.presentation.model.PaymentRequestModel
import com.kg.gettransfer.presentation.view.PaypalConnectionView
import com.kg.gettransfer.presentation.view.Screens
import com.kg.gettransfer.utilities.Analytics
import org.koin.standalone.inject
import timber.log.Timber

@InjectViewState
class PaypalConnectionPresenter: BasePresenter<PaypalConnectionView>() {

    private val paymentInteractor: PaymentInteractor by inject()
    private val routeInteractor: RouteInteractor by inject()

    internal var paymentId = 0L
    internal var nonce = ""
    internal var offerId = 0L
    internal var percentage = PaymentRequestModel.FULL_PRICE
    internal var bookNowTransportId: String? = null
    internal var transfer: Transfer? = null

    private var offer: Offer? = null
    private var bookNowOffer: BookNowOffer? = null

    @CallSuper
    override fun attachView(view: PaypalConnectionView) {
        super.attachView(view)
        utils.launchSuspend {
            offer = offerInteractor.getOffer(offerId)
            if (offer == null) {
                transfer?.let {
                    if (it.bookNowOffers.isNotEmpty()) {
                        val filteredBookNow = it.bookNowOffers.filterKeys { it.toString() == bookNowTransportId }
                        if (filteredBookNow.isNotEmpty()) {
                            bookNowOffer = filteredBookNow.values.first()
                        }
                    }
                }
            }
            confirmPayment()
        }
    }

    private suspend fun confirmPayment() {
        val result = utils.asyncAwait { paymentInteractor.confirmPaypal(paymentId, nonce) }
        if (result.error == null) {
            if (result.model.success) showSuccessfulPayment()
            else showFailedPayment()
            viewState.blockInterface(false)
        } else {
            Timber.e(result.error!!)
            viewState.setError(result.error!!)
            viewState.blockInterface(false)
            showFailedPayment()
        }
        viewState.stopAnimation()
    }

    private fun showFailedPayment() {
        router.exit()
        transfer?.let {
            router.navigateTo(Screens.PaymentError(it.id))
        }
        logEvent(Analytics.RESULT_FAIL)
    }

    private fun showSuccessfulPayment() {
        router.replaceScreen(Screens.ChangeMode(Screens.PASSENGER_MODE))
        transfer?.let {
            router.navigateTo(Screens.PaymentSuccess(it.id, offerId))
        }
        logEventEcommercePurchase()
    }

    private fun logEventEcommercePurchase() {
        val offerType = if (offer != null) Analytics.REGULAR else Analytics.NOW
        val requestType = when {
            transfer?.duration != null -> Analytics.TRIP_HOURLY
            transfer?.dateReturnLocal != null -> Analytics.TRIP_ROUND
            else -> Analytics.TRIP_DESTINATION
        }
        var price: Double = if (offer != null) offer!!.price.amount else bookNowOffer!!.amount
        if (percentage == OfferModel.PRICE_30) price *= PaymentOfferPresenter.PRICE_30

        val purchase = analytics.EcommercePurchase(
                transfer?.id.toString(),
                transfer?.promoCode,
                routeInteractor.duration,
                PaymentRequestModel.PAYPAL,
                offerType,
                requestType,
                systemInteractor.currency,
                systemInteractor.currency.currencyCode,
                price)
        purchase.sendAnalytics()
    }

    private fun logEvent(value: String) {
        val map = mutableMapOf<String, Any>()
        map[Analytics.STATUS] = value
        analytics.logEvent(Analytics.EVENT_MAKE_PAYMENT, createStringBundle(Analytics.STATUS, value), map)
    }
}