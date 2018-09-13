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

import ru.terrakok.cicerone.Router

import timber.log.Timber

@InjectViewState
class OffersPresenter(cc: CoroutineContexts,
                      router: Router,
                      apiInteractor: ApiInteractor): BasePresenter<OffersView>(cc, router, apiInteractor) {

    private lateinit var allTransfers: List<Transfer>
    private lateinit var transfer: Transfer
    private lateinit var archivedTransfers: List<Transfer>
    private lateinit var activeTransfers: List<Transfer>
    private lateinit var offers: List<Offer>

    init {
        router.setResultListener(LoginPresenter.RESULT_CODE, { _ -> onFirstViewAttach() })
    }
    
    override fun onFirstViewAttach() {
        utils.launchAsyncTryCatchFinally({
            viewState.blockInterface(true)
            utils.asyncAwait {
                allTransfers = apiInteractor.getAllTransfers()
                transfer = apiInteractor.getTransfer(allTransfers.get(0).id)
                archivedTransfers = apiInteractor.getTransfersArchive()
                activeTransfers = apiInteractor.getTransfersActive()
                offers = apiInteractor.getOffers(allTransfers.get(0).id)
            }
        }, { e ->
                if(e is ApiException) {
                    if(e.isNotLoggedIn()) login()
                    else viewState.setError(false, R.string.err_server_code, e.code.toString(), e.details)
                }
                else viewState.setError(false, R.string.err_server, e.message)
        }, { viewState.blockInterface(false) })
    }
    
    @CallSuper
    override fun onDestroy() {
        router.removeResultListener(LoginPresenter.RESULT_CODE)
        super.onDestroy()
    }
}
