package com.kg.gettransfer.presentation.presenter

import com.arellomobile.mvp.InjectViewState

import com.kg.gettransfer.R

import com.kg.gettransfer.domain.ApiException
import com.kg.gettransfer.domain.CoroutineContexts

import com.kg.gettransfer.domain.interactor.SystemInteractor

import com.kg.gettransfer.domain.model.Transfer

import com.kg.gettransfer.presentation.view.RequestsView

import ru.terrakok.cicerone.Router

@InjectViewState
class RequestsPresenter(cc: CoroutineContexts,
                        router: Router,
                        systemInteractor: SystemInteractor): BasePresenter<RequestsView>(cc, router, systemInteractor) {

    private lateinit var transfers: List<Transfer>
    private var transfersAll: ArrayList<Transfer> = arrayListOf()
    private var transfersActive: ArrayList<Transfer> = arrayListOf()
    private var transfersCompleted: ArrayList<Transfer> = arrayListOf()

    override fun onFirstViewAttach() {
        /*
        utils.launchAsyncTryCatchFinally({
            viewState.blockInterface(true)
            transfers = utils.asyncAwait { apiInteractor.getAllTransfers() }
            
            for(transfer in transfers) {
                when(transfer.status) {
                    TransfersConstants.STATUS_NEW -> addToActive(transfer)
                    TransfersConstants.STATUS_DRAFT -> addToActive(transfer)
                    TransfersConstants.STATUS_PERFORMED -> addToActive(transfer)
                    TransfersConstants.STATUS_PENDING -> addToActive(transfer)

                    TransfersConstants.STATUS_COMPLETED -> transfersCompleted.add(transfer)
                    TransfersConstants.STATUS_NOT_COMPLETED -> transfersCompleted.add(transfer)

                    TransfersConstants.STATUS_OUTDATED -> transfersAll.add(transfer)
                    TransfersConstants.STATUS_CANCELED -> transfersAll.add(transfer)
                    TransfersConstants.STATUS_REJECTED -> transfersAll.add(transfer)
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
        */
    }

    fun addToActive(transfer: Transfer){
        transfersActive.add(transfer)
        transfersAll.add(transfer)
    }
}
