package com.kg.gettransfer.presentation.presenter

import android.os.Bundle

import android.support.annotation.CallSuper

import com.arellomobile.mvp.InjectViewState

import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.FirebaseAnalytics.Event.BEGIN_CHECKOUT
import com.google.firebase.analytics.FirebaseAnalytics.Param.CURRENCY
import com.google.firebase.analytics.FirebaseAnalytics.Param.VALUE

import com.kg.gettransfer.domain.ApiException
import com.kg.gettransfer.domain.CoroutineContexts

import com.kg.gettransfer.domain.interactor.OfferInteractor
import com.kg.gettransfer.domain.interactor.PaymentInteractor
import com.kg.gettransfer.domain.interactor.SystemInteractor

import com.kg.gettransfer.domain.model.Offer
import com.kg.gettransfer.domain.model.Payment

import com.kg.gettransfer.presentation.Screens

import com.kg.gettransfer.presentation.model.Mappers
import com.kg.gettransfer.presentation.model.OfferModel
import com.kg.gettransfer.presentation.model.PaymentRequestModel

import com.kg.gettransfer.presentation.view.PaymentSettingsView
import com.yandex.metrica.YandexMetrica

import java.util.Date

import kotlinx.serialization.Serializable

import ru.terrakok.cicerone.Router

import timber.log.Timber

@InjectViewState
class PaymentSettingsPresenter(cc: CoroutineContexts,
                               router: Router,
                               systemInteractor: SystemInteractor,
                               private val offerInteractor: OfferInteractor,
                               private val paymentInteractor: PaymentInteractor): BasePresenter<PaymentSettingsView>(cc, router, systemInteractor) {
    companion object {
        @JvmField val PARAM_SHARE      = "share"
        @JvmField val PRICE_30         = 0.3
        
        @JvmField val PARAMS = "params"
    }
    
    @Serializable
    data class Params(val dateRefund: Date?, val transferId: Long, val offerId: Long)

    init {
        router.setResultListener(LoginPresenter.RESULT_CODE, { _ -> onFirstViewAttach() })
    }

    private lateinit var paymentRequest: PaymentRequestModel
    private var offer: Offer? = null
    
    internal lateinit var params: Params
    
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

    @CallSuper
    override fun onDestroy() {
        router.removeResultListener(LoginPresenter.RESULT_CODE)
        super.onDestroy()
    }

    fun getPayment() {
        utils.launchSuspend {
            viewState.blockInterface(true)
            
            val result = utils.asyncAwait { paymentInteractor.getPayment(Mappers.getPaymentRequest(paymentRequest)) }
            if(result.error != null) {
                Timber.e(result.error!!)
                viewState.setError(result.error!!)
            } else {
                logEventBeginCheckout()
                navigateToPayment(result.model)
            }
            viewState.blockInterface(false)
        }
    }
    
    private fun navigateToPayment(payment: Payment) {
        router.navigateTo(Screens.PAYMENT, PaymentPresenter.Params(offer!!.id, payment.url!!))
    }

    private fun logEventBeginCheckout() {
        val bundle = Bundle()
        val map = HashMap<String, Any>()

        bundle.putString(CURRENCY, systemInteractor.currency.currencyCode)
        map[CURRENCY] = systemInteractor.currency.currencyCode

        val price = offer!!.price.amount
        when (paymentRequest.percentage) {
            OfferModel.FULL_PRICE -> {
                bundle.putDouble(VALUE, price)
                map[FirebaseAnalytics.Param.VALUE] = price
            }
            OfferModel.PRICE_30 -> {
                bundle.putDouble(VALUE, price * PRICE_30)
                map[VALUE] = price * PRICE_30
            }
        }
        bundle.putInt(PARAM_SHARE, paymentRequest.percentage)
        map[PARAM_SHARE] = paymentRequest.percentage

        mFBA.logEvent(BEGIN_CHECKOUT, bundle)
        YandexMetrica.reportEvent(BEGIN_CHECKOUT, map)
    }

    fun changePrice(price: Int)        { paymentRequest.percentage = price }
    fun changePayment(payment: String) { paymentRequest.gatewayId  = payment }
}
