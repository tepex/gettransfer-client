package com.kg.gettransfer.presentation.presenter

import android.support.annotation.CallSuper

import com.arellomobile.mvp.InjectViewState
import com.kg.gettransfer.data.mapper.TransferMapper

import com.kg.gettransfer.domain.model.Offer

import com.kg.gettransfer.domain.model.sortDescendant
import com.kg.gettransfer.domain.model.Transfer
import com.kg.gettransfer.domain.model.Transfer.Companion.filterActive
import com.kg.gettransfer.domain.model.Transfer.Companion.filterCompleted
import com.kg.gettransfer.domain.eventListeners.CounterEventListener

import com.kg.gettransfer.presentation.model.OfferModel
import com.kg.gettransfer.presentation.model.TransferModel

import com.kg.gettransfer.presentation.view.RequestsFragmentView
import com.kg.gettransfer.presentation.view.RequestsView
import com.kg.gettransfer.presentation.view.RequestsView.TransferTypeAnnotation.Companion.TRANSFER_ACTIVE
import com.kg.gettransfer.presentation.view.RequestsView.TransferTypeAnnotation.Companion.TRANSFER_ARCHIVE
import com.kg.gettransfer.presentation.view.Screens
import org.koin.standalone.inject

import timber.log.Timber

@InjectViewState
class RequestsFragmentPresenter(@RequestsView.TransferTypeAnnotation
                                tt: Int) : BasePresenter<RequestsFragmentView>(), CounterEventListener {
    @RequestsView.TransferTypeAnnotation
    var transferType = tt

    private var transfers: List<TransferModel>? = null

    @CallSuper
    override fun attachView(view: RequestsFragmentView) {
        super.attachView(view)
        getTransfers()
        countEventsInteractor.addCounterListener(this)
    }

    @CallSuper
    override fun detachView(view: RequestsFragmentView?) {
        super.detachView(view)
        countEventsInteractor.removeCounterListener(this)
    }

    private fun getTransfers() {
        utils.launchSuspend {
            viewState.blockInterface(true)

            val result = when(categoryName) {
                RequestsActivity.CATEGORY_ACTIVE    -> utils.asyncAwait { transferInteractor.getActiveTransfers() }
                RequestsActivity.CATEGORY_COMPLETED -> utils.asyncAwait { transferInteractor.getCompletedTransfers() }
                else                                -> utils.asyncAwait { transferInteractor.getArchivedTransfers() }
            }

            result?.let { showTransfers(it) }
            viewState.blockInterface(false)
        }
    }

    private fun showTransfers(transfers: List<Transfer>) {

        val filteredSorted = transfers.sortedByDescending {
            it.dateToLocal
        }.map { transferMapper.toView(it) }
        with(countEventsInteractor) {
            updateEvents(mapCountNewOffers.plus(mapCountNewMessages), mapCountViewedOffers)
        }
        this.transfers?.let { viewState.setRequests(it) }
    }

    private fun updateEvents(mapCountNewEvents: Map<Long, Int>, mapCountViewedOffers: Map<Long, Int>) {
        transfers = transfers?.let { transfersList ->
            for (i in 0 until transfersList.size) {
                val transfer = transfersList[i]
                mapCountNewEvents[transfer.id]?.let {
                    val eventsCount = it - (mapCountViewedOffers[transfer.id] ?: 0)
                    if (eventsCount > 0) transfer.eventsCount = eventsCount
                }
            }
            transfersList
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
