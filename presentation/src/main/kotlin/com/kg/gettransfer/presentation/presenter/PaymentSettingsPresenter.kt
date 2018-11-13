package com.kg.gettransfer.presentation.presenter

import android.os.Bundle

import android.support.annotation.CallSuper

import com.arellomobile.mvp.InjectViewState

import com.facebook.appevents.AppEventsConstants.EVENT_NAME_INITIATED_CHECKOUT
import com.facebook.appevents.AppEventsConstants.EVENT_PARAM_CURRENCY

import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.FirebaseAnalytics.Event.BEGIN_CHECKOUT
import com.google.firebase.analytics.FirebaseAnalytics.Param.CURRENCY
import com.google.firebase.analytics.FirebaseAnalytics.Param.VALUE

import com.kg.gettransfer.domain.ApiException

import com.kg.gettransfer.domain.interactor.OfferInteractor
import com.kg.gettransfer.domain.interactor.PaymentInteractor

import com.kg.gettransfer.domain.model.Offer
import com.kg.gettransfer.domain.model.Payment

import com.kg.gettransfer.presentation.model.Mappers
import com.kg.gettransfer.presentation.model.OfferModel
import com.kg.gettransfer.presentation.model.PaymentRequestModel

import com.kg.gettransfer.presentation.view.PaymentSettingsView
import com.kg.gettransfer.presentation.view.Screens

import com.yandex.metrica.YandexMetrica

import org.koin.standalone.inject

import timber.log.Timber

@InjectViewState
class PaymentSettingsPresenter: BasePresenter<PaymentSettingsView>() {
    companion object {
        @JvmField val PARAM_SHARE      = "share"
        @JvmField val PRICE_30         = 0.3
    }
    
    private val offerInteractor: OfferInteractor by inject()
    private val paymentInteractor: PaymentInteractor by inject()
    
    private var offer: Offer? = null
    internal lateinit var params: PaymentSettingsView.Params
    
    private lateinit var paymentRequest: PaymentRequestModel
    
    @CallSuper
    override fun attachView(view: PaymentSettingsView?) {
        super.attachView(view)
        offer = offerInteractor.getOffer(params.offerId)
        offer?.let {
            paymentRequest = PaymentRequestModel(params.transferId, params.offerId)
            viewState.setOffer(Mappers.getOfferModel(it, systemInteractor.locale))
            return
        }
        viewState.setError(ApiException(ApiException.NOT_FOUND, "Offer [${params.offerId}] not found!"))
    }

    /*
    @CallSuper
    override fun onDestroy() {
        router.removeResultListener(LoginPresenter.RESULT_CODE)
        super.onDestroy()
    }
    */

    fun getPayment() = utils.launchSuspend {
        viewState.blockInterface(true)
            
        val result = utils.asyncAwait { paymentInteractor.getPayment(Mappers.getPaymentRequest(paymentRequest)) }
        if(result.error != null) {
            Timber.e(result.error!!)
            viewState.setError(result.error!!)
        } else {
            logEventBeginCheckout()
            router.navigateTo(Screens.Payment(params.transferId, offer!!.id, result.model.url!!, paymentRequest.percentage))
        }
        viewState.blockInterface(false)
    }
    
    private fun logEventBeginCheckout() {
        val bundle = Bundle()
        val map = HashMap<String, Any>()
        val fbBundle = Bundle()

        bundle.putString(CURRENCY, systemInteractor.currency.currencyCode)
        map[CURRENCY] = systemInteractor.currency.currencyCode
        fbBundle.putString(EVENT_PARAM_CURRENCY, systemInteractor.currency.currencyCode)

        var price = offer!!.price.amount
        when (paymentRequest.percentage) {
            OfferModel.FULL_PRICE -> {
                bundle.putDouble(VALUE, price)
                map[FirebaseAnalytics.Param.VALUE] = price
            }
            OfferModel.PRICE_30 -> {
                price *= PRICE_30
                bundle.putDouble(VALUE, price)
                map[VALUE] = price
            }
        }
        bundle.putInt(PARAM_SHARE, paymentRequest.percentage)
        map[PARAM_SHARE] = paymentRequest.percentage
        fbBundle.putInt(PARAM_SHARE, paymentRequest.percentage)

        mFBA.logEvent(BEGIN_CHECKOUT, bundle)
        YandexMetrica.reportEvent(BEGIN_CHECKOUT, map)
        eventsLogger.logEvent(EVENT_NAME_INITIATED_CHECKOUT, price, fbBundle)
    }

    fun changePrice(price: Int)        { paymentRequest.percentage = price }
    fun changePayment(payment: String) { paymentRequest.gatewayId  = payment }
}
