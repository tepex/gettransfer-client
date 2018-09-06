package com.kg.gettransfer.presentation.presenter

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.kg.gettransfer.domain.AsyncUtils
import com.kg.gettransfer.domain.CoroutineContexts
import com.kg.gettransfer.domain.interactor.ApiInteractor
import com.kg.gettransfer.domain.model.Account
import com.kg.gettransfer.domain.model.Transfer
import com.kg.gettransfer.presentation.TransfersConstants
import com.kg.gettransfer.presentation.view.RequestsFragmentView
import kotlinx.coroutines.experimental.Job
import timber.log.Timber

@InjectViewState
class RequestsFragmentPresenter(private val cc: CoroutineContexts,
                                private val apiInteractor: ApiInteractor): MvpPresenter<RequestsFragmentView>(){

    private val compositeDisposable = Job()
    private val utils = AsyncUtils(cc)

    var account: Account? = null
    var transfers = listOf<Transfer>()
    var distanceUnit = ""

    fun setData(categoryName: String){
        utils.launchAsyncTryCatchFinally(compositeDisposable, {

            utils.asyncAwait {
                account = apiInteractor.getAccount()
            }
            distanceUnit = account?.distanceUnit!!
            transfers = when(categoryName){
                TransfersConstants.CATEGORY_ACTIVE -> apiInteractor.activeTransfers
                TransfersConstants.CATEGORY_COMPLETED -> apiInteractor.completedTransfers
                else -> apiInteractor.allTransfers
            }
            viewState.setRequests(transfers, distanceUnit)
        }, { e ->
            Timber.e(e)
            //viewState.setError(R.string.err_address_service_xxx, false)
        }, { /* viewState.blockInterface(false) */ })
    }
}