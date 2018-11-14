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

import ru.terrakok.cicerone.Router

import timber.log.Timber

@InjectViewState
class PaymentPresenter(cc: CoroutineContexts,
                       router: Router,
                       systemInteractor: SystemInteractor,
                       private val paymentInteractor: PaymentInteractor,
                       private val offerInteractor: OfferInteractor): BasePresenter<PaymentView>(cc, router, systemInteractor) {
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
                logEventEcommercePurchase()
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
    
    private fun logEventEcommercePurchase() {
        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.CURRENCY, systemInteractor.currency.currencyCode)
        val price = offer.price.amount
        when (paymentRequest.percentage) {
            OfferModel.FULL_PRICE -> bundle.putDouble(FirebaseAnalytics.Param.VALUE, price)
            OfferModel.PRICE_30 -> bundle.putDouble(FirebaseAnalytics.Param.VALUE, price * PRICE_30)
        }
        bundle.putString(PARAM_TRANSACTION_ID, offerInteractor.transferId!!.toString())
        mFBA.logEvent(FirebaseAnalytics.Event.ECOMMERCE_PURCHASE, bundle)
    }
}
