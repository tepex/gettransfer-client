package com.kg.gettransfer.presentation.presenter

import com.arellomobile.mvp.InjectViewState

import com.kg.gettransfer.domain.CoroutineContexts
import com.kg.gettransfer.domain.interactor.PaymentInteractor
import com.kg.gettransfer.domain.interactor.SystemInteractor

import com.kg.gettransfer.presentation.Screens
import com.kg.gettransfer.presentation.model.Mappers
import com.kg.gettransfer.presentation.model.PaymentStatusRequestModel
import com.kg.gettransfer.presentation.view.PaymentView

import ru.terrakok.cicerone.Router
import timber.log.Timber

@InjectViewState
class PaymentPresenter(cc: CoroutineContexts,
                       router: Router,
                       systemInteractor: SystemInteractor,
                       private val paymentInteractor: PaymentInteractor): BasePresenter<PaymentView>(cc, router, systemInteractor) {

    companion object {
        private const val SUCCESS = "success"
    }

    fun changePaymentStatus(orderId: Long, success: Boolean) {
        utils.launchAsyncTryCatchFinally({
            viewState.blockInterface(true)
            val model = PaymentStatusRequestModel(null, orderId, true, success)
            val payment = paymentInteractor.changeStatusPayment(Mappers.getPaymentStatusRequest(model))
            if(SUCCESS.equals(payment.status)) {
                router.navigateTo(Screens.PASSENGER_MODE)
                viewState.showSuccessfulMessage()
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
}
