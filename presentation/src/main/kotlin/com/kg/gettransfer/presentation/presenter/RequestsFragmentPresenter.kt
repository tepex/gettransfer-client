package com.kg.gettransfer.presentation.presenter

import android.support.annotation.CallSuper

import com.arellomobile.mvp.InjectViewState

import com.kg.gettransfer.R
import com.kg.gettransfer.domain.ApiException
import com.kg.gettransfer.domain.CoroutineContexts

import com.kg.gettransfer.domain.interactor.SystemInteractor
import com.kg.gettransfer.domain.interactor.TransferInteractor

import com.kg.gettransfer.presentation.Screens
import com.kg.gettransfer.presentation.view.RequestsFragmentView

import ru.terrakok.cicerone.Router

import timber.log.Timber

@InjectViewState
class RequestsFragmentPresenter(cc: CoroutineContexts,
                                router: Router,
                                systemInteractor: SystemInteractor,
                                private val transferInteractor: TransferInteractor): BasePresenter<RequestsFragmentView>(cc, router, systemInteractor) {

    @CallSuper
    override fun attachView(view: RequestsFragmentView) {
        super.attachView(view)
    }
    
    fun setData(categoryName: String) {
        /*
        utils.launchAsyncTryCatchFinally({
            viewState.blockInterface(true)
            transfers = when(categoryName) {
                TransfersConstants.CATEGORY_ACTIVE -> transferInteractor.activeTransfers
                TransfersConstants.CATEGORY_COMPLETED -> transferInteractor.completedTransfers
                else -> transferInteractor.allTransfers
            }
            viewState.setRequests(transfers, account.distanceUnit, Utils.createDateTimeFormat(account.locale!!))
        }, { e ->
                if(e is ApiException) viewState.setError(false, R.string.err_server_code, e.code.toString(), e.details)
                else viewState.setError(false, R.string.err_server, e.message)
        }, { viewState.blockInterface(false) })
        */
    }

    /*
    fun openTransferDetails(transfer: Transfer) {
        //transferInteractor.transferDetails = transfer
        router.navigateTo(Screens.DETAILS)
    }
    */
}
