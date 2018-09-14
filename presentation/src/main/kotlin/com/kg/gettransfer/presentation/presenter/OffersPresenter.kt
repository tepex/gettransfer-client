package com.kg.gettransfer.presentation.presenter

import android.support.annotation.CallSuper

import com.arellomobile.mvp.InjectViewState

import com.kg.gettransfer.R

import com.kg.gettransfer.domain.ApiException
import com.kg.gettransfer.domain.CoroutineContexts
import com.kg.gettransfer.domain.interactor.ApiInteractor

import com.kg.gettransfer.domain.model.Offer
import com.kg.gettransfer.domain.model.Transfer

import com.kg.gettransfer.presentation.view.OffersView
import com.kg.gettransfer.presentation.ui.Utils

import java.text.SimpleDateFormat
import java.util.Locale

import ru.terrakok.cicerone.Router

import timber.log.Timber

@InjectViewState
class OffersPresenter(cc: CoroutineContexts,
                      router: Router,
                      apiInteractor: ApiInteractor): BasePresenter<OffersView>(cc, router, apiInteractor) {
                      
    val transfer = apiInteractor.getLastTransfer()
    
    init {
        router.setResultListener(LoginPresenter.RESULT_CODE, { _ -> onFirstViewAttach() })
    }
    
    @CallSuper
    override fun attachView(view: OffersView) {
        super.attachView(view)
        val account = apiInteractor.getAccount()
        viewState.setDate(SimpleDateFormat(Utils.DATE_TIME_PATTERN, account.locale ?: Locale.getDefault()).format(transfer.dateToLocal))
    }
    
    @CallSuper
    override fun onDestroy() {
        router.removeResultListener(LoginPresenter.RESULT_CODE)
        super.onDestroy()
    }
}
