package com.kg.gettransfer.presentation.presenter

import android.os.Bundle

import android.support.annotation.CallSuper

import com.arellomobile.mvp.InjectViewState

import com.google.firebase.analytics.FirebaseAnalytics

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
        @JvmField val BUNDLE_KEY_URL   = "url"
    }

    init {
        router.setResultListener(LoginPresenter.RESULT_CODE, { _ -> onFirstViewAttach() })
    }

    private val paymentRequest = PaymentRequestModel(offerInteractor.transferId!!, offerInteractor.selectedOfferId!!)

    private var offer: Offer? = null
    
    override fun onFirstViewAttach() {
        offerInteractor.selectedOfferId?.let { offer = offerInteractor.getOffer(it) }
    }

    @CallSuper
    override fun attachView(view: PaymentSettingsView?) {
        super.attachView(view)
        offer?.let { viewState.setOffer(Mappers.getOfferModel(it, systemInteractor.locale)) }
    }

    @CallSuper
    override fun onDestroy() {
        router.removeResultListener(LoginPresenter.RESULT_CODE)
        super.onDestroy()
    }

    fun getPayment() {
        utils.launchAsyncTryCatchFinally({
            viewState.blockInterface(true)
            val payment = paymentInteractor.getPayment(Mappers.getPaymentRequest(paymentRequest))
            logEventBeginCheckout()
            navigateToPayment(payment)
        }, {
            e -> Timber.e(e)
            viewState.setError(e)
        }, { viewState.blockInterface(false) })
    }
    
    private fun navigateToPayment(payment: Payment) {
        val bundle = Bundle()
        bundle.putString(BUNDLE_KEY_URL, payment.url)
        router.navigateTo(Screens.PAYMENT, bundle)
    }

    private fun logEventBeginCheckout() {
        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.CURRENCY, "USD")
        if(offer != null) {
            when(paymentRequest.percentage) {
//                OfferModel.FULL_PRICE -> params[FirebaseAnalytics.Param.VALUE] = offer!!.price.amount
                OfferModel.FULL_PRICE -> bundle.putDouble(FirebaseAnalytics.Param.VALUE, 300.0)
//                OfferModel.PRICE_30 -> params[FirebaseAnalytics.Param.VALUE] = offer!!.price.percentage30
            }
        }
        bundle.putInt(PARAM_SHARE, paymentRequest.percentage)
        bundle.putString(PaymentPresenter.PARAM_TRANSACTION_ID, offerInteractor.transferId!!.toString())
        mFBA.logEvent(FirebaseAnalytics.Event.BEGIN_CHECKOUT, bundle)
    }

    fun changePrice(price: Int)        { paymentRequest.percentage = price }
    fun changePayment(payment: String) { paymentRequest.gatewayId  = payment }
}
