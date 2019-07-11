package com.kg.gettransfer.presentation.presenter

import android.support.annotation.CallSuper
import com.arellomobile.mvp.InjectViewState
import com.kg.gettransfer.domain.interactor.PaymentInteractor
import com.kg.gettransfer.domain.interactor.OrderInteractor
import com.kg.gettransfer.domain.model.BookNowOffer
import com.kg.gettransfer.domain.model.Offer
import com.kg.gettransfer.domain.model.Transfer
import com.kg.gettransfer.extensions.newChainFromMain
import com.kg.gettransfer.presentation.model.OfferModel
import com.kg.gettransfer.presentation.model.PaymentRequestModel
import com.kg.gettransfer.presentation.view.PaypalConnectionView
import com.kg.gettransfer.presentation.view.Screens
import com.kg.gettransfer.utilities.Analytics
import org.koin.core.inject
import timber.log.Timber
import java.util.Currency

@InjectViewState
class PaypalConnectionPresenter: BasePresenter<PaypalConnectionView>() {

    private val paymentInteractor: PaymentInteractor by inject()
    private val orderInteractor: OrderInteractor by inject()

    internal var paymentId = 0L
    internal var nonce = ""
    internal var transferId = 0L
    internal var transfer: Transfer? = null
    internal var offerId = 0L
    internal var percentage = PaymentRequestModel.FULL_PRICE
    internal var bookNowTransportId: String? = null

    private var offer: Offer? = null
    private var bookNowOffer: BookNowOffer? = null

    @CallSuper
    override fun attachView(view: PaypalConnectionView) {
        super.attachView(view)
        utils.launchSuspend {
            offer = offerInteractor.getOffer(offerId)
            transfer = fetchData { transferInteractor.getTransfer(transferId, true) }
            if (offer == null) {
                transfer?.let {
                    if (it.bookNowOffers.isNotEmpty()) {
                        bookNowOffer = it.bookNowOffers.find { bookNowOffer ->
                            bookNowOffer.transportType.id.name == bookNowTransportId
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
            if (result.model.isSuccess) showSuccessfulPayment() else showFailedPayment()
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
        router.navigateTo(Screens.PaymentError(transferId))
        analytics.logEvent(Analytics.EVENT_MAKE_PAYMENT, Analytics.STATUS, Analytics.RESULT_FAIL)
    }

    private fun showSuccessfulPayment() {
        router.newChainFromMain(Screens.PaymentSuccess(transferId, offerId))
        logEventEcommercePurchase()
    }

    private fun logEventEcommercePurchase() {
        val offerType = if (offer != null) Analytics.REGULAR else Analytics.NOW
        val requestType = when {
            transfer?.duration != null -> Analytics.TRIP_HOURLY
            transfer?.dateReturnLocal != null -> Analytics.TRIP_ROUND
            else -> Analytics.TRIP_DESTINATION
        }

        var price = 0.0
        if (offer != null) {
            price = offer?.price?.amount ?: 0.0
        } else if (bookNowOffer != null) {
            price = bookNowOffer?.amount ?: 0.0
        }

        if (percentage == OfferModel.PRICE_30) {
            price *= PaymentOfferPresenter.PRICE_30
        }

        val purchase = analytics.EcommercePurchase(
                transferId.toString(),
                transfer?.promoCode,
                orderInteractor.duration,
                PaymentRequestModel.PAYPAL,
                offerType,
                requestType,
                Currency.getInstance(sessionInteractor.currency.code),
                sessionInteractor.currency.code,
                price)
        purchase.sendAnalytics()
    }
}
