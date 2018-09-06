package com.kg.gettransfer.presentation.presenter

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.kg.gettransfer.domain.AsyncUtils
import com.kg.gettransfer.domain.CoroutineContexts
import com.kg.gettransfer.domain.interactor.ApiInteractor
import com.kg.gettransfer.domain.model.Account
import com.kg.gettransfer.domain.model.Transfer
import com.kg.gettransfer.presentation.view.RequestsView
import kotlinx.coroutines.experimental.Job
import timber.log.Timber

@InjectViewState
class RequestsPresenter(private val cc: CoroutineContexts,
                        private val apiInteractor: ApiInteractor): MvpPresenter<RequestsView>() {

    companion object {
        @JvmField val STATUS_NEW       = "new"
        @JvmField val STATUS_COMPLETED = "completed"
    }

    private val compositeDisposable = Job()
    private val utils = AsyncUtils(cc)

    var account: Account? = null
    lateinit var transfers: List<Transfer>

    var transfersAll: ArrayList<Transfer> = arrayListOf()
    var transfersActive: ArrayList<Transfer> = arrayListOf()
    var transfersCompleted: ArrayList<Transfer> = arrayListOf()

    override fun onFirstViewAttach() {
        utils.launchAsyncTryCatchFinally(compositeDisposable, {

            utils.asyncAwait {
                account = apiInteractor.getAccount()
                transfers = apiInteractor.getAllTransfers()
            }

            for(transfer in transfers){
                when (transfer.status){
                    STATUS_NEW -> transfersActive.add(transfer)
                    STATUS_COMPLETED -> transfersCompleted.add(transfer)
                    else -> transfersAll.add(transfer)
                }
            }

            viewState.setRequestsFragments(transfersActive, transfersAll, transfersCompleted, account?.distanceUnit!!)
        }, { e ->
            Timber.e(e)
            //viewState.setError(R.string.err_address_service_xxx, false)
        }, { /* viewState.blockInterface(false) */ })
    }

    fun onBackCommandClick() {
        viewState.finish()
    }
}