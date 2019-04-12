package com.kg.gettransfer.presentation.presenter

import android.support.annotation.CallSuper
import com.arellomobile.mvp.InjectViewState
import com.kg.gettransfer.domain.eventListeners.CounterEventListener
import com.kg.gettransfer.domain.model.Transfer
import com.kg.gettransfer.presentation.model.TransferModel
import com.kg.gettransfer.presentation.view.RequestsFragmentView
import com.kg.gettransfer.presentation.view.RequestsView
import com.kg.gettransfer.presentation.view.RequestsView.TransferTypeAnnotation.Companion.TRANSFER_ACTIVE
import com.kg.gettransfer.presentation.view.RequestsView.TransferTypeAnnotation.Companion.TRANSFER_ARCHIVE
import com.kg.gettransfer.presentation.view.Screens
import timber.log.Timber

@InjectViewState
class RequestsFragmentPresenter(@RequestsView.TransferTypeAnnotation tt: Int) :
        BasePresenter<RequestsFragmentView>(), CounterEventListener {

    companion object {
        private const val PAGE_SIZE = 4
    }

    @RequestsView.TransferTypeAnnotation
    var transferType = tt

    private var transfers: List<Transfer>? = null
    private var recordCount = 0
    private var updating = false

    @CallSuper
    override fun attachView(view: RequestsFragmentView) {
        super.attachView(view)
        viewState.showTransfers()
        countEventsInteractor.addCounterListener(this)
    }

    @CallSuper
    override fun detachView(view: RequestsFragmentView?) {
        super.detachView(view)
        countEventsInteractor.removeCounterListener(this)
    }

    fun getTransfers(available: Boolean?) {
        utils.launchSuspend {

            when(transferType){
                TRANSFER_ACTIVE -> {
                    if(available != true) {
                        fetchData { transferInteractor.getTransfersActiveCached() }?.let {
                            transfers = it.sortedByDescending {
                                it.dateToLocal
                            }
                            updateTransfers()
                        }
                    } else {
                        fetchData { transferInteractor.getTransfersActive() }?.let {
                            transfers = it.sortedByDescending {
                                it.dateToLocal
                            }
                            updateTransfers()
                        }
                    }
                }

                TRANSFER_ARCHIVE -> {
                    if(available != true) {
                        fetchData { transferInteractor.getTransfersArchiveCached() }?.let {
                            transfers = it.sortedByDescending {
                                it.dateToLocal
                            }
                            updateTransfers()
                        }
                    } else {
                        fetchData { transferInteractor.getTransfersArchive() }?.let {
                            transfers = it.sortedByDescending {
                                it.dateToLocal
                            }
                            updateTransfers()
                        }
                    }
                }
            }
            if (transferType == TRANSFER_ARCHIVE)
                viewState.blockInterface(false)
        }
    }

    fun updateTransfersSuspend() {
        if(!updating) {
            updating = true
            utils.launchSuspend {
                updateTransfers()
            }
        }
    }

    protected fun updateTransfers() {
        transfers?.let {
            if(recordCount < it.size) {
                val end = Math.min(PAGE_SIZE, it.size - recordCount)
                val viewModel = it.subList(recordCount, recordCount + end)
                        .map { transferMapper.toView(it) }
                        .let {
                            with(countEventsInteractor) {
                                updateEvents(mapCountNewOffers.plus(mapCountNewMessages), mapCountViewedOffers, it)
                            }
                        }

                viewState.updateTransfers(viewModel)
                viewState.setScrollListener()

                recordCount += PAGE_SIZE
                if (recordCount >= it.size) {
                    recordCount = it.size
                }

                updating = false
            }
        }
    }

    private fun updateEvents(mapCountNewEvents: Map<Long, Int>, mapCountViewedOffers: Map<Long, Int>, transfersList: List<TransferModel>): List<TransferModel> {
        return transfersList.also { list ->
            list.forEach {transfer->
                mapCountNewEvents[transfer.id]?.let {
                    val eventsCount = it - (mapCountViewedOffers[transfer.id] ?: 0)
                    if (eventsCount > 0) transfer.eventsCount = eventsCount
                }
            }
        }
    }

    fun openTransferDetails(id: Long, status: Transfer.Status, paidPercentage: Int) {
        Timber.d("Open Transfer details. id: $id")
        if (status == Transfer.Status.NEW && paidPercentage == 0) {
            router.navigateTo(Screens.Offers(id))
        } else {
            router.navigateTo(Screens.Details(id))
        }
    }

    override fun updateCounter() {
        updateTransfers()
        /*utils.launchSuspend{
            with(countEventsInteractor) {
                updateEvents(mapCountNewOffers.plus(mapCountNewMessages), mapCountViewedOffers)
            }
        }
        viewState.notifyData()*/
    }
}
