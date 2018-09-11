package com.kg.gettransfer.presentation.presenter

import com.arellomobile.mvp.InjectViewState

import com.kg.gettransfer.R
import com.kg.gettransfer.domain.ApiException
import com.kg.gettransfer.domain.CoroutineContexts

import com.kg.gettransfer.domain.interactor.ApiInteractor

import com.kg.gettransfer.domain.model.Account
import com.kg.gettransfer.domain.model.Transfer

import com.kg.gettransfer.presentation.TransfersConstants
import com.kg.gettransfer.presentation.view.RequestsFragmentView

import ru.terrakok.cicerone.Router

import timber.log.Timber

@InjectViewState
class RequestsFragmentPresenter(cc: CoroutineContexts,
                                router: Router,
                                apiInteractor: ApiInteractor): BasePresenter<RequestsFragmentView>(cc, router, apiInteractor) {

    var transfers = listOf<Transfer>()
    var distanceUnit = ""

    fun setData(categoryName: String){
        utils.launchAsyncTryCatchFinally(compositeDisposable, {
            viewState.blockInterface(true)
            account = utils.asyncAwait { apiInteractor.getAccount() }
            distanceUnit = account.distanceUnit!!
            transfers = when(categoryName) {
                TransfersConstants.CATEGORY_ACTIVE -> apiInteractor.activeTransfers
                TransfersConstants.CATEGORY_COMPLETED -> apiInteractor.completedTransfers
                else -> apiInteractor.allTransfers
            }
            viewState.setRequests(transfers, distanceUnit)
        }, { e ->
                if(e is ApiException) viewState.setError(false, R.string.err_server_code, e.code.toString(), e.details)
                else viewState.setError(false, R.string.err_server, e.message)
        }, { viewState.blockInterface(false) })
    }
}
