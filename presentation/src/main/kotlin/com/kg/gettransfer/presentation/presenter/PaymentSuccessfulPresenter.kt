package com.kg.gettransfer.presentation.presenter

import com.arellomobile.mvp.InjectViewState
import com.kg.gettransfer.domain.interactor.OfferInteractor
import com.kg.gettransfer.presentation.view.PaymentSuccessfulView
import org.koin.standalone.inject

@InjectViewState
class PaymentSuccessfulPresenter : BasePresenter<PaymentSuccessfulView>() {

    private val offerInteractor: OfferInteractor by inject()

    internal var offerId = 0L

    fun onCallClick() {
        viewState.call(offerInteractor.getOffer(offerId)?.carrier?.profile?.phone)
    }
}