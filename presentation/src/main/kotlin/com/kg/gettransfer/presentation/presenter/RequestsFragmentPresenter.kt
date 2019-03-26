package com.kg.gettransfer.presentation.presenter

import android.support.annotation.CallSuper

import com.arellomobile.mvp.InjectViewState

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
import com.kg.gettransfer.presentation.view.Screens

import timber.log.Timber

@InjectViewState
class RequestsFragmentPresenter : BasePresenter<RequestsFragmentView>(), CounterEventListener {

    lateinit var categoryName: String

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
            /*
            val result = when(categoryName) {
                RequestsActivity.CATEGORY_ACTIVE    -> utils.asyncAwait { transferInteractor.getActiveTransfers() }
                RequestsActivity.CATEGORY_COMPLETED -> utils.asyncAwait { transferInteractor.getCompletedTransfers() }
                else                                -> utils.asyncAwait { transferInteractor.getArchivedTransfers() }
            }
            */
            fetchData { transferInteractor.getAllTransfers() }?.let { showTransfers(it) }
            viewState.blockInterface(false)
        }
    }

    private fun showTransfers(transfers: List<Transfer>) {
        val filtered = when (categoryName) {
            RequestsView.CATEGORY_ACTIVE    -> transfers.filterActive()
            RequestsView.CATEGORY_COMPLETED -> transfers.filterCompleted()
            else                            -> transfers
        }
        this.transfers = filtered.sortedByDescending {
            it.dateToLocal
        }.map { transferMapper.toView(it) }
        with(countEventsInteractor) {
            updateEvents(mapCountNewOffers.plus(mapCountNewMessages), mapCountViewedOffers)
        }
        if (this.transfers != null) viewState.setRequests(this.transfers!!)
    }

    private fun updateEvents(mapCountNewEvents: Map<Long, Int>, mapCountViewedOffers: Map<Long, Int>) {
        if(transfers != null) {
            for (i in 0 until transfers!!.size) {
                val transfer = transfers!![i]
                if (mapCountNewEvents[transfer.id] != null) {
                    val eventsCount = mapCountNewEvents.getValue(transfer.id) - (mapCountViewedOffers[transfer.id] ?: 0)
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
