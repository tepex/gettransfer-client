package com.kg.gettransfer.presentation.presenter

import android.os.Bundle

import com.arellomobile.mvp.InjectViewState

import com.google.firebase.analytics.FirebaseAnalytics.Event.ECOMMERCE_PURCHASE
import com.google.firebase.analytics.FirebaseAnalytics.Param.TRANSACTION_ID
import com.google.firebase.analytics.FirebaseAnalytics.Param.VALUE
import com.google.firebase.analytics.FirebaseAnalytics.Param.CURRENCY

import com.kg.gettransfer.domain.interactor.OfferInteractor
import com.kg.gettransfer.domain.interactor.PaymentInteractor
import com.kg.gettransfer.domain.interactor.SystemInteractor

import com.kg.gettransfer.domain.model.Offer

import com.kg.gettransfer.presentation.Screens

import com.kg.gettransfer.presentation.model.Mappers
import com.kg.gettransfer.presentation.model.OfferModel
import com.kg.gettransfer.presentation.model.PaymentRequestModel
import com.kg.gettransfer.presentation.model.PaymentStatusRequestModel
import com.kg.gettransfer.presentation.presenter.PaymentSettingsPresenter.Companion.PRICE_30

import com.kg.gettransfer.presentation.view.PaymentView

import com.yandex.metrica.YandexMetrica

import kotlinx.serialization.Serializable

import ru.terrakok.cicerone.Router

import timber.log.Timber

@InjectViewState
class PaymentPresenter(router: Router,
                       systemInteractor: SystemInteractor,
                       private val paymentInteractor: PaymentInteractor,
                       private val offerInteractor: OfferInteractor): BasePresenter<PaymentView>(router, systemInteractor) {

    private lateinit var offer: Offer
    internal lateinit var params: Params

    companion object {
        @JvmField val PARAM_OFFER_ID = "offer_id"
        @JvmField val PARAMS = "params"
    }

    @Serializable
    data class Params(val transferId: Long, val offerId: Long, val paymentUrl: String, val percentage: Int)

    fun changePaymentStatus(orderId: Long, success: Boolean) {
        utils.launchSuspend {
            viewState.blockInterface(true)
            val model = PaymentStatusRequestModel(null, orderId, true, success)
            val result = utils.asyncAwait { paymentInteractor.changeStatusPayment(Mappers.getPaymentStatusRequest(model)) }
            if(result.error != null) {
                Timber.e(result.error!!)
                viewState.setError(result.error!!)
                router.exit()
            } else {
                if(result.model.success) {
                    router.navigateTo(Screens.PASSENGER_MODE)
                    viewState.showSuccessfulMessage()
                    offer = offerInteractor.getOffer(params.offerId)!!
                    logEventEcommercePurchase()
                } else {
                    router.exit()
                    viewState.showErrorMessage()
                }
            }
            viewState.blockInterface(false)
        }
    }

    private fun logEventEcommercePurchase() {
        val bundle = Bundle()
        val map = HashMap<String, Any>()

        map[CURRENCY] = systemInteractor.currency.currencyCode

        var price = offer.price.amount

        when(params.percentage) {
            OfferModel.FULL_PRICE -> {
                bundle.putDouble(VALUE, price)
                map[VALUE] = price
            }
            OfferModel.PRICE_30 -> {
                price *= PRICE_30
                bundle.putDouble(VALUE, price)
                map[VALUE] = price
            }
        }

        bundle.putString(TRANSACTION_ID, params.transferId.toString())
        map[TRANSACTION_ID] = params.transferId

        eventsLogger.logPurchase(price.toBigDecimal(), systemInteractor.currency, bundle)

        bundle.putString(CURRENCY, systemInteractor.currency.currencyCode)
        mFBA.logEvent(ECOMMERCE_PURCHASE, bundle)
        YandexMetrica.reportEvent(ECOMMERCE_PURCHASE, map)
    }
}
