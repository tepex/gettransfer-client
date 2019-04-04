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
    @RequestsView.TransferTypeAnnotation
    var transferType = tt

    private var transfers: List<TransferModel>? = null

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

            viewState.blockInterface(true)

            when(transferType){
                TRANSFER_ACTIVE -> {
                    if(available != true) {
                        fetchData { transferInteractor.getTransfersActiveCached() }?.let {
                            showTransfers(it)
                        }
                    } else {
                        fetchData { transferInteractor.getTransfersActive() }?.let {
                            showTransfers(it)
                        }
                    }
                }

                TRANSFER_ARCHIVE -> {
                    if(available != true) {
                        fetchData { transferInteractor.getTransfersArchiveCached() }?.let {
                            showTransfers(it)
                        }
                    } else {
                        fetchData { transferInteractor.getTransfersArchive() }?.let {
                            showTransfers(it)
                        }
                    }
                }
            }

            viewState.blockInterface(false)
        }
    }

    private fun showTransfers(transfers: List<Transfer>) {
        this.transfers = transfers.sortedByDescending {
            it.dateToLocal
        }.map { transferMapper.toView(it) }
        with(countEventsInteractor) {
            updateEvents(mapCountNewOffers.plus(mapCountNewMessages), mapCountViewedOffers)
        }
        this.transfers?.let { viewState.setRequests(it) }
    }

    private fun updateEvents(mapCountNewEvents: Map<Long, Int>, mapCountViewedOffers: Map<Long, Int>) {
        transfers = transfers?.also { transfersList ->
            transfersList.forEach {transfer->
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
        utils.launchSuspend{
            with(countEventsInteractor) {
                updateEvents(mapCountNewOffers.plus(mapCountNewMessages), mapCountViewedOffers)
            }
        }
        viewState.notifyData()
    }
}
