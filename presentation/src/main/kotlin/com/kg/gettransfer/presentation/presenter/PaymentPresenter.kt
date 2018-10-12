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

     fun changeStatusPayment(orderId: Long, status: String) {
         utils.launchAsyncTryCatchFinally({
             viewState.blockInterface(true)
             paymentInteractor.changeStatusPayment(0L, orderId, true, status)
             router.navigateTo(Screens.DETAILS)
         }, {
             e -> Timber.e(e)
             viewState.setError(e)
         }, { viewState.blockInterface(false) })

     }
}