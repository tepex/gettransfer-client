package com.kg.gettransfer.presentation.presenter

import com.arellomobile.mvp.InjectViewState

import com.kg.gettransfer.R
import com.kg.gettransfer.domain.ApiException
import com.kg.gettransfer.domain.CoroutineContexts

import com.kg.gettransfer.domain.interactor.SystemInteractor

import com.kg.gettransfer.domain.model.Account
import com.kg.gettransfer.domain.model.Transfer

import com.kg.gettransfer.presentation.TransfersConstants
import com.kg.gettransfer.presentation.ui.Utils
import com.kg.gettransfer.presentation.view.RequestsFragmentView

import java.util.Locale

import ru.terrakok.cicerone.Router

import timber.log.Timber

@InjectViewState
class RequestsFragmentPresenter(cc: CoroutineContexts,
                                router: Router,
                                systemInteractor: SystemInteractor): BasePresenter<RequestsFragmentView>(cc, router, systemInteractor) {

    var transfers = listOf<Transfer>()

    fun setData(categoryName: String) {
        /*
        utils.launchAsyncTryCatchFinally({
            viewState.blockInterface(true)
            val account = utils.asyncAwait { apiInteractor.getAccount() }
            if(account.locale == null) account.locale = Locale.getDefault()
            transfers = when(categoryName) {
                TransfersConstants.CATEGORY_ACTIVE -> apiInteractor.activeTransfers
                TransfersConstants.CATEGORY_COMPLETED -> apiInteractor.completedTransfers
                else -> apiInteractor.allTransfers
            }
            viewState.setRequests(transfers, account.distanceUnit, Utils.createDateTimeFormat(account.locale!!))
        }, { e ->
                if(e is ApiException) viewState.setError(false, R.string.err_server_code, e.code.toString(), e.details)
                else viewState.setError(false, R.string.err_server, e.message)
        }, { viewState.blockInterface(false) })
        */
    }
}
