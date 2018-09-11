package com.kg.gettransfer.presentation.presenter

import android.support.annotation.CallSuper

import com.arellomobile.mvp.InjectViewState

import com.kg.gettransfer.R

import com.kg.gettransfer.domain.ApiException
import com.kg.gettransfer.domain.CoroutineContexts

import com.kg.gettransfer.domain.interactor.ApiInteractor

import com.kg.gettransfer.domain.model.Transfer

import com.kg.gettransfer.presentation.TransfersConstants
import com.kg.gettransfer.presentation.view.RequestsView

import ru.terrakok.cicerone.Router

import timber.log.Timber

@InjectViewState
class RequestsPresenter(cc: CoroutineContexts,
                        router: Router,
                        apiInteractor: ApiInteractor): BasePresenter<RequestsView>(cc, router, apiInteractor) {

    private lateinit var transfers: List<Transfer>
    private var transfersAll: ArrayList<Transfer> = arrayListOf()
    private var transfersActive: ArrayList<Transfer> = arrayListOf()
    private var transfersCompleted: ArrayList<Transfer> = arrayListOf()

    override fun onFirstViewAttach() {
        utils.launchAsyncTryCatchFinally(compositeDisposable, {
            viewState.blockInterface(true)
            transfers = utils.asyncAwait { apiInteractor.getAllTransfers() }
            
            for(transfer in transfers) {
                when(transfer.status) {
                    TransfersConstants.STATUS_NEW -> transfersActive.add(transfer)
                    TransfersConstants.STATUS_COMPLETED -> transfersCompleted.add(transfer)
                    else -> transfersAll.add(transfer)
                }
            }

            apiInteractor.activeTransfers = transfersActive
            apiInteractor.allTransfers = transfersAll
            apiInteractor.completedTransfers = transfersCompleted

            viewState.setRequestsFragments()
        }, { e ->
                if(e is ApiException) viewState.setError(false, R.string.err_server_code, e.code.toString(), e.details)
                else viewState.setError(false, R.string.err_server, e.message)
        }, { viewState.blockInterface(false) })
    }
}
