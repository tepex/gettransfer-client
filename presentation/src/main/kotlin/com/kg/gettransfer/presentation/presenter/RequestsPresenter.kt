package com.kg.gettransfer.presentation.presenter

import android.support.annotation.CallSuper

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter

import com.kg.gettransfer.R

import com.kg.gettransfer.domain.ApiException
import com.kg.gettransfer.domain.AsyncUtils
import com.kg.gettransfer.domain.CoroutineContexts
import com.kg.gettransfer.domain.interactor.ApiInteractor
import com.kg.gettransfer.domain.model.Transfer
import com.kg.gettransfer.presentation.TransfersConstants
import com.kg.gettransfer.presentation.view.RequestsView

import kotlinx.coroutines.experimental.Job

import timber.log.Timber

@InjectViewState
class RequestsPresenter(private val cc: CoroutineContexts,
                        private val apiInteractor: ApiInteractor): MvpPresenter<RequestsView>() {

    private val compositeDisposable = Job()
    private val utils = AsyncUtils(cc)

    lateinit var transfers: List<Transfer>
    var transfersAll: ArrayList<Transfer> = arrayListOf()
    var transfersActive: ArrayList<Transfer> = arrayListOf()
    var transfersCompleted: ArrayList<Transfer> = arrayListOf()

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

    fun onBackCommandClick() {
        viewState.finish()
    }

    @CallSuper
    override fun onDestroy() {
        compositeDisposable.cancel()
        super.onDestroy()
    }
}
