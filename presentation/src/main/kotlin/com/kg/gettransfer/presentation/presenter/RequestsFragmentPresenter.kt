package com.kg.gettransfer.presentation.presenter

import android.support.annotation.CallSuper

import com.arellomobile.mvp.InjectViewState

import com.kg.gettransfer.R
import com.kg.gettransfer.domain.ApiException
import com.kg.gettransfer.domain.CoroutineContexts

import com.kg.gettransfer.domain.interactor.SystemInteractor
import com.kg.gettransfer.domain.interactor.TransferInteractor
import com.kg.gettransfer.domain.model.Transfer

import com.kg.gettransfer.presentation.Screens
import com.kg.gettransfer.presentation.model.Mappers
import com.kg.gettransfer.presentation.model.TransferModel

import com.kg.gettransfer.presentation.ui.RequestsActivity
import com.kg.gettransfer.presentation.view.RequestsFragmentView

import ru.terrakok.cicerone.Router

import timber.log.Timber

@InjectViewState
class RequestsFragmentPresenter(cc: CoroutineContexts,
                                router: Router,
                                systemInteractor: SystemInteractor,
                                private val transferInteractor: TransferInteractor,
                                private val categoryName: String): BasePresenter<RequestsFragmentView>(cc, router, systemInteractor) {
    
    private var transfers: List<TransferModel>? = null
                                
    /*override fun onFirstViewAttach() {
        utils.launchAsyncTryCatchFinally({
            viewState.blockInterface(true)
            transfers = when(categoryName) {
                RequestsActivity.CATEGORY_ACTIVE -> transferInteractor.getActiveTransfers()
                RequestsActivity.CATEGORY_COMPLETED -> transferInteractor.getCompletedTransfers()
                else -> transferInteractor.getArchivedTransfers()
            }.map { Mappers.getTransferModel(it,
                                             systemInteractor.locale,
                                             systemInteractor.distanceUnit,
                                             systemInteractor.getTransportTypes()) }
            viewState.setRequests(transfers!!)
        }, { e ->
                if(e is ApiException) viewState.setError(false, R.string.err_server_code, e.code.toString(), e.details)
                else viewState.setError(e)
        }, { viewState.blockInterface(false) }
    }*/
        
    @CallSuper
    override fun attachView(view: RequestsFragmentView) {
        super.attachView(view)
        getTransfers()
        transfers?.let { viewState.setRequests(it) }
    }

    fun getTransfers(){
        utils.launchAsyncTryCatchFinally({
            viewState.blockInterface(true)
            transfers = when(categoryName) {
                RequestsActivity.CATEGORY_ACTIVE -> transferInteractor.getActiveTransfers()
                RequestsActivity.CATEGORY_COMPLETED -> transferInteractor.getCompletedTransfers()
                else -> transferInteractor.getArchivedTransfers()
            }.map { Mappers.getTransferModel(it,
                    systemInteractor.locale,
                    systemInteractor.distanceUnit,
                    systemInteractor.getTransportTypes()) }
            viewState.setRequests(transfers!!)
        }, { e ->
            if(e is ApiException) viewState.setError(false, R.string.err_server_code, e.code.toString(), e.details)
            else viewState.setError(e)
        }, { viewState.blockInterface(false) })
    }

    fun openTransferDetails(id: Long, status: String) {
        transferInteractor.selectedId = id
        when(status){
            Transfer.STATUS_NEW -> router.navigateTo(Screens.OFFERS)
            else -> router.navigateTo(Screens.DETAILS)
        }
    }
}
