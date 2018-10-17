package com.kg.gettransfer.presentation.presenter

import android.support.annotation.CallSuper

import com.arellomobile.mvp.InjectViewState

import com.kg.gettransfer.domain.CoroutineContexts

import com.kg.gettransfer.domain.interactor.OfferInteractor
import com.kg.gettransfer.domain.interactor.PaymentInteractor
import com.kg.gettransfer.domain.interactor.SystemInteractor

import com.kg.gettransfer.domain.model.Offer

import com.kg.gettransfer.presentation.Screens

import com.kg.gettransfer.presentation.model.Mappers
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
        if(offer != null) viewState.setOffer(Mappers.getOfferModel(offer!!))
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
            router.navigateTo(Screens.PAYMENT, payment.url)
        }, {
            e -> Timber.e(e)
            viewState.setError(e)
        }, { viewState.blockInterface(false) })
    }

    fun changePrice(price: Int)        { paymentRequest.percentage = price }
    fun changePayment(payment: String) { paymentRequest.gatewayId  = payment }
}
