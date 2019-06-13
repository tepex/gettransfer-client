package com.kg.gettransfer.presentation.presenter

import android.support.annotation.CallSuper
import com.arellomobile.mvp.InjectViewState
import com.kg.gettransfer.domain.eventListeners.CounterEventListener
import com.kg.gettransfer.domain.model.Transfer
import com.kg.gettransfer.extensions.finishChainAndBackTo
import com.kg.gettransfer.presentation.view.RequestsFragmentView
import com.kg.gettransfer.presentation.view.RequestsView
import com.kg.gettransfer.presentation.view.RequestsView.TransferTypeAnnotation.Companion.TRANSFER_ACTIVE
import com.kg.gettransfer.presentation.view.RequestsView.TransferTypeAnnotation.Companion.TRANSFER_ARCHIVE
import com.kg.gettransfer.presentation.view.Screens
import timber.log.Timber
import java.lang.IllegalArgumentException

@InjectViewState
class RequestsCategoryPresenter(@RequestsView.TransferTypeAnnotation tt: Int) :
        BasePresenter<RequestsFragmentView>(), CounterEventListener {

    @RequestsView.TransferTypeAnnotation
    var transferType = tt

    private var transfers: List<Transfer>? = null
    private var eventsCount: Map<Long, Int>? = null

    @CallSuper
    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.blockInterface(true, true)
    }

    @CallSuper
    override fun attachView(view: RequestsFragmentView) {
        super.attachView(view)
        countEventsInteractor.addCounterListener(this)
        getTransfers()
    }

    @CallSuper
    override fun detachView(view: RequestsFragmentView?) {
        super.detachView(view)
        transfers = null
        countEventsInteractor.removeCounterListener(this)
    }

    private fun getTransfers() {
        utils.launchSuspend {
            transfers = when (transferType) {
                TRANSFER_ACTIVE  ->  fetchData { transferInteractor.getTransfersActive() }
                TRANSFER_ARCHIVE ->  fetchData(checkLoginError = false) { transferInteractor.getTransfersArchive() }
                else             ->  throw IllegalArgumentException("Wrong transfer type in ${this@RequestsCategoryPresenter::class.java.name}")
            }?.sortedByDescending { it.dateToLocal }
            prepareDataAsync()
            viewState.blockInterface(false)
        }
    }

    private suspend fun prepareDataAsync() {
        transfers?.let {
            if (it.isNotEmpty()) {
                utils
                        .compute { transfers?.map { t -> transferMapper.toView(t) } }
                        ?.also { viewList ->
                            viewState.updateTransfers(viewList)
                            updateEventsCount()
                        }
            } else
                viewState.onEmptyList()
        }
    }

    private fun updateEventsCount() {
        eventsCount = with(countEventsInteractor) {
            getEventsCount(mapCountNewOffers.plus(mapCountNewMessages), mapCountViewedOffers)
        }
        eventsCount?.let { viewState.updateEvents(it) }
    }

    private fun getEventsCount(mapCountNewEvents: Map<Long, Int>, mapCountViewedOffers: Map<Long, Int>): Map<Long, Int> {
        val eventsMap = mutableMapOf<Long, Int>()
        transfers?.forEach { transfer ->
            mapCountNewEvents[transfer.id]?.let {
                val eventsCount = it - (mapCountViewedOffers[transfer.id] ?: 0)
                if (eventsCount > 0) eventsMap[transfer.id] = eventsCount
            }
        }
        return eventsMap
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
        updateEventsCount()
    }

    fun onGetBookClicked() {
        router.finishChainAndBackTo(Screens.MainPassenger())
    }
}
