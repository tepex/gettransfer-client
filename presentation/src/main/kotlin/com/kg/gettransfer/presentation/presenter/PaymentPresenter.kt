package com.kg.gettransfer.presentation.presenter

import com.arellomobile.mvp.InjectViewState

import com.kg.gettransfer.domain.CoroutineContexts
import com.kg.gettransfer.domain.interactor.PaymentInteractor

import com.kg.gettransfer.domain.interactor.SystemInteractor
import com.kg.gettransfer.presentation.Screens

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

    fun changePaymentStatus(orderId: Long, status: String) {
        utils.launchAsyncTryCatchFinally({
            viewState.blockInterface(true)
            val payment = paymentInteractor.changeStatusPayment(orderId, true, status)
            if (SUCCESS.equals(payment.status)) {
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
    }}