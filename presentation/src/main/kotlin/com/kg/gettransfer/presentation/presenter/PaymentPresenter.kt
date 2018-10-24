package com.kg.gettransfer.presentation.presenter

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

import com.kg.gettransfer.presentation.view.PaymentView

import ru.terrakok.cicerone.Router

import timber.log.Timber

@InjectViewState
class PaymentPresenter(cc: CoroutineContexts,
                       router: Router,
                       systemInteractor: SystemInteractor,
                       private val paymentInteractor: PaymentInteractor,
                       private val offerInteractor: OfferInteractor): BasePresenter<PaymentView>(cc, router, systemInteractor) {
    internal var price: Int = 0
    private lateinit var offer: Offer
    
    companion object {
        @JvmField val PARAM_TRANSACTION_ID = "transaction_id"
    }

    private val paymentRequest = PaymentRequestModel(offerInteractor.transferId!!, offerInteractor.selectedOfferId!!)
    
    fun changePaymentStatus(orderId: Long, success: Boolean) {
        utils.launchAsyncTryCatchFinally({
            viewState.blockInterface(true)
            val model = PaymentStatusRequestModel(null, orderId, true, success)
            val paymentStatus = paymentInteractor.changeStatusPayment(Mappers.getPaymentStatusRequest(model))
            if(paymentStatus.success) {
                router.navigateTo(Screens.PASSENGER_MODE)
                viewState.showSuccessfulMessage()
                offer = offerInteractor.getOffer(offerInteractor.selectedOfferId!!)!!
                logEventEcommercePurchase(orderId)
            } else {
                router.exit()
                viewState.showErrorMessage()
            }
        }, {
            e -> Timber.e(e)
            viewState.setError(e)
            router.exit()
        }, { viewState.blockInterface(false) })
    }
    
    private fun logEventEcommercePurchase(orderId: Long) {
        val params = HashMap<String, Any>()
        params[FirebaseAnalytics.Param.CURRENCY] = systemInteractor.currency.currencyCode
        when(paymentRequest.percentage) {
            OfferModel.FULL_PRICE -> params[FirebaseAnalytics.Param.VALUE] = offer.price.amount
            OfferModel.PRICE_30 -> params[FirebaseAnalytics.Param.VALUE] = offer.price.percentage30
        }
        params[PARAM_TRANSACTION_ID] = orderId
        mFBA.logEvent(FirebaseAnalytics.Event.ECOMMERCE_PURCHASE, createMultipleBundle(params))
    }
}
