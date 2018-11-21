package com.kg.gettransfer.presentation.presenter

import android.os.Bundle

import android.support.annotation.CallSuper

import com.arellomobile.mvp.InjectViewState

import com.google.firebase.analytics.FirebaseAnalytics

import com.kg.gettransfer.domain.ApiException

import com.kg.gettransfer.domain.interactor.OfferInteractor
import com.kg.gettransfer.domain.interactor.PaymentInteractor

import com.kg.gettransfer.domain.model.Offer

import com.kg.gettransfer.presentation.model.Mappers
import com.kg.gettransfer.presentation.model.OfferModel
import com.kg.gettransfer.presentation.model.PaymentRequestModel

import com.kg.gettransfer.presentation.view.PaymentSettingsView
import com.kg.gettransfer.presentation.view.Screens
import com.kg.gettransfer.utilities.Analytics.Companion.CURRENCY
import com.kg.gettransfer.utilities.Analytics.Companion.EVENT_BEGIN_CHECKOUT
import com.kg.gettransfer.utilities.Analytics.Companion.SHARE
import com.kg.gettransfer.utilities.Analytics.Companion.VALUE

import org.koin.standalone.inject

import timber.log.Timber

@InjectViewState
class PaymentSettingsPresenter: BasePresenter<PaymentSettingsView>() {
    companion object {
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

        bundle.putString(CURRENCY, systemInteractor.currency.currencyCode)
        map[CURRENCY] = systemInteractor.currency.currencyCode

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
        bundle.putInt(SHARE, paymentRequest.percentage)
        map[SHARE] = paymentRequest.percentage

        analytics.logEventBeginCheckout(EVENT_BEGIN_CHECKOUT, bundle, map, price)
    }

    fun changePrice(price: Int)        { paymentRequest.percentage = price }
    fun changePayment(payment: String) { paymentRequest.gatewayId  = payment }
}
