package com.kg.gettransfer.presentation.presenter

import moxy.InjectViewState

import com.kg.gettransfer.domain.model.PaymentRequest

import com.kg.gettransfer.presentation.view.PlatronPaymentView

@InjectViewState
class PlatronPaymentPresenter : BaseCardPaymentPresenter<PlatronPaymentView>() {

    override fun attachView(view: PlatronPaymentView) {
        super.attachView(view)
        gateway = PaymentRequest.Gateway.PLATRON
    }
}
