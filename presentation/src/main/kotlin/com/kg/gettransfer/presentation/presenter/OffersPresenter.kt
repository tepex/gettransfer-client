package com.kg.gettransfer.presentation.presenter

import android.support.annotation.CallSuper

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter

import com.kg.gettransfer.R

import com.kg.gettransfer.domain.ApiException
import com.kg.gettransfer.domain.AsyncUtils
import com.kg.gettransfer.domain.CoroutineContexts
import com.kg.gettransfer.domain.interactor.ApiInteractor
import com.kg.gettransfer.domain.model.Offer
import com.kg.gettransfer.domain.model.Transfer

import com.kg.gettransfer.presentation.Screens
import com.kg.gettransfer.presentation.view.OffersView

import kotlinx.coroutines.experimental.Job

import ru.terrakok.cicerone.Router

import timber.log.Timber

@InjectViewState
class OffersPresenter(private val cc: CoroutineContexts,
                      private val router: Router,
                      private val apiInteractor: ApiInteractor): MvpPresenter<OffersView>() {

    private val compositeDisposable = Job()
    private val utils = AsyncUtils(cc)

    lateinit var allTransfers: List<Transfer>
    lateinit var transfer: Transfer
    lateinit var archivedTransfers: List<Transfer>
    lateinit var activeTransfers: List<Transfer>
    lateinit var offers: List<Offer>

    override fun onFirstViewAttach() {
        utils.launchAsyncTryCatchFinally(compositeDisposable, {
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
    
    private fun login() {
        Timber.d("go to login")
        router.navigateTo(Screens.LOGIN) 
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
