package com.kg.gettransfer.presentation.presenter

import android.os.Bundle
import com.arellomobile.mvp.InjectViewState

import com.google.firebase.analytics.FirebaseAnalytics

import com.kg.gettransfer.domain.CoroutineContexts

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

import kotlinx.serialization.Serializable

import ru.terrakok.cicerone.Router

import timber.log.Timber

@InjectViewState
class PaymentPresenter(cc: CoroutineContexts,
                       router: Router,
                       systemInteractor: SystemInteractor,
                       private val paymentInteractor: PaymentInteractor,
                       private val offerInteractor: OfferInteractor): BasePresenter<PaymentView>(cc, router, systemInteractor) {

    private lateinit var offer: Offer
    internal lateinit var params: Params
    
    companion object {
        @JvmField val PARAM_OFFER_ID = "offer_id"
        @JvmField val PARAMS = "params"
    }
    
    @Serializable
    data class Params(val offerId: Long, val paymentUrl: String)

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
        bundle.putString(FirebaseAnalytics.Param.CURRENCY, systemInteractor.currency.currencyCode)
        val price = offer.price.amount
        bundle.putString(PARAM_OFFER_ID, params.offerId.toString())
        mFBA.logEvent(FirebaseAnalytics.Event.ECOMMERCE_PURCHASE, bundle)
    }
}
