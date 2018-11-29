package com.kg.gettransfer.presentation.presenter

import com.arellomobile.mvp.InjectViewState
import com.kg.gettransfer.presentation.view.PaymentErrorView

@InjectViewState
class PaymentErrorPresenter: BasePresenter<PaymentErrorView>() {
    fun sendEmail(emailCarrier: String?){ viewState.sendEmail(emailCarrier, if(emailCarrier == null) systemInteractor.logsFile else null) }
    fun callPhone(phone: String){ viewState.callPhone(phone) }
}
