package com.kg.gettransfer.presentation.presenter

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.kg.gettransfer.domain.AsyncUtils
import com.kg.gettransfer.domain.CoroutineContexts
import com.kg.gettransfer.domain.interactor.ApiInteractor
import com.kg.gettransfer.domain.model.Offer
import com.kg.gettransfer.domain.model.Transfer
import com.kg.gettransfer.presentation.view.OffersView
import kotlinx.coroutines.experimental.Job
import timber.log.Timber

@InjectViewState
class OffersPresenter(private val cc: CoroutineContexts,
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

            utils.asyncAwait {
                allTransfers = apiInteractor.getAllTransfers()
                transfer = apiInteractor.getTransfer(allTransfers.get(0).id)
                archivedTransfers = apiInteractor.getTransfersArchive()
                activeTransfers = apiInteractor.getTransfersActive()
                offers = apiInteractor.getOffers(allTransfers.get(0).id)
            }
        }, { e ->
            Timber.e(e)
            //viewState.setError(R.string.err_address_service_xxx, false)
        }, { /* viewState.blockInterface(false) */ })

    }

    fun onBackCommandClick() {
        viewState.finish()
    }
}