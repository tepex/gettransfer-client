package com.kg.gettransfer.presentation.presenter

import android.os.Handler
import com.arellomobile.mvp.InjectViewState
import com.kg.gettransfer.domain.eventListeners.CoordinateEventListener

import com.kg.gettransfer.domain.eventListeners.CounterEventListener
import com.kg.gettransfer.domain.interactor.CoordinateInteractor
import com.kg.gettransfer.domain.model.Coordinate
import com.kg.gettransfer.domain.model.Offer
import com.kg.gettransfer.domain.model.Transfer
import com.kg.gettransfer.presentation.delegate.DriverCoordinate
import com.kg.gettransfer.presentation.model.TransferModel

import com.kg.gettransfer.presentation.model.map

import com.kg.gettransfer.sys.presentation.ConfigsManager

import com.kg.gettransfer.presentation.view.RequestsFragmentView
import com.kg.gettransfer.presentation.view.RequestsView
import com.kg.gettransfer.presentation.view.RequestsView.TransferTypeAnnotation.Companion.TRANSFER_ACTIVE
import com.kg.gettransfer.presentation.view.RequestsView.TransferTypeAnnotation.Companion.TRANSFER_ARCHIVE
import com.kg.gettransfer.presentation.view.Screens
import org.koin.core.inject
import java.util.*

@InjectViewState
class RequestsCategoryPresenter(@RequestsView.TransferTypeAnnotation tt: Int) :
    BasePresenter<RequestsFragmentView>(), CounterEventListener, CoordinateEventListener {

    private val coordinateInteractor: CoordinateInteractor by inject()

    @RequestsView.TransferTypeAnnotation
    var transferType = tt

    private var transfers: List<Transfer>? = null
    private var eventsCount: Map<Long, Int>? = null
    private var driverCoordinate: DriverCoordinate? = null
    private val configsManager: ConfigsManager by inject()

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.blockInterface(true, true)
    }

    override fun attachView(view: RequestsFragmentView) {
        super.attachView(view)
        countEventsInteractor.addCounterListener(this)
        getTransfers()
    }

    override fun detachView(view: RequestsFragmentView?) {
        super.detachView(view)
        transfers = null
        countEventsInteractor.removeCounterListener(this)
        coordinateInteractor.removeCoordinateListener(this)
        driverCoordinate?.requestCoordinates = false
        driverCoordinate = null
    }

    private fun getTransfers() {
        utils.launchSuspend {
            transfers = when (transferType) {
                TRANSFER_ACTIVE -> fetchData(checkLoginError = false) { transferInteractor.getTransfersActive() }
                TRANSFER_ARCHIVE -> fetchData(checkLoginError = false) { transferInteractor.getTransfersArchive() }
                else -> throw IllegalArgumentException("Wrong transfer type in ${this@RequestsCategoryPresenter::class.java.name}")
            }?.sortedByDescending { it.dateToLocal }
            if (transferType == TRANSFER_ACTIVE && !transfers.isNullOrEmpty()) {
                coordinateInteractor.addCoordinateListener(this@RequestsCategoryPresenter)
                driverCoordinate = DriverCoordinate(Handler())
            }
            viewState.updateCardWithDriverCoordinates(6442L)
            prepareDataAsync()
            viewState.blockInterface(false)
        }
    }

    private suspend fun prepareDataAsync() {
        transfers?.let { trs ->
            if (trs.isNotEmpty()) {
                val transportTypes = configsManager.configs.transportTypes.map { it.map() }
                utils.compute {
                    transfers?.map {
                        it.map(transportTypes)
                    }?.map {
                        if (it.status == Transfer.Status.PERFORMED && isShowOfferInfo(it)) {
                            it.copy(matchedOffer = getOffer(it.id))
                        } else it
                    }
                }?.also { viewList ->
                    viewState.updateTransfers(viewList)
                    updateEventsCount()
                }
            } else {
                viewState.onEmptyList()
            }
        }
    }

    private fun isShowOfferInfo(transfer: TransferModel): Boolean {
        val durationInMinutes = transfer.duration?.times(MINUTES_PER_HOUR) ?: transfer.time ?: return false
        return transfer.dateTimeReturnTZ?.let { checkDateForShowingOfferInfo(it, durationInMinutes) } ?: false ||
            checkDateForShowingOfferInfo(transfer.dateTimeTZ, durationInMinutes)
    }

    private fun checkDateForShowingOfferInfo(date: Date, duration: Int): Boolean {
        val dateNow = Calendar.getInstance().time
        val dateStart = Calendar.getInstance().apply {
            time = date
            add(Calendar.HOUR, HOURS_BEFORE_TRIP_FOR_SHOWING_OFFER)
        }.time
        val dateEnd = Calendar.getInstance().apply {
            time = date
            add(Calendar.MINUTE, duration)
        }.time
        return dateNow.after(dateStart) && dateNow.before(dateEnd)
    }

    private suspend fun getOffer(transferId: Long): Offer? {
        return fetchData { offerInteractor.getOffers(transferId) }
            ?.let {
                if (it.size == 1) {
                    it.first()
                } else null
            }
    }

    private fun updateEventsCount() {
        eventsCount = with(countEventsInteractor) {
            getEventsCount(mapCountNewOffers.plus(mapCountNewMessages), mapCountViewedOffers)
        }
        eventsCount?.let { viewState.updateEvents(it) }
    }

    private fun getEventsCount(
        mapCountNewEvents: Map<Long, Int>,
        mapCountViewedOffers: Map<Long, Int>
    ): Map<Long, Int> {
        val eventsMap = mutableMapOf<Long, Int>()
        transfers?.forEach { transfer ->
            mapCountNewEvents[transfer.id]?.let {
                val eventsCount = it - (mapCountViewedOffers[transfer.id] ?: 0)
                if (eventsCount > 0) eventsMap[transfer.id] = eventsCount
            }
        }
        return eventsMap
    }

    override fun onLocationReceived(coordinate: Coordinate) {
        coordinate.transferId?.let { viewState.updateCardWithDriverCoordinates(it) }
    }

    fun openTransferDetails(id: Long, status: Transfer.Status, paidPercentage: Int, pendingPaymentId: Int?) {
        log.debug("Open Transfer details. id: $id")
        if (status == Transfer.Status.NEW && paidPercentage == 0 && pendingPaymentId == null) {
            router.navigateTo(Screens.Offers(id))
        } else {
            router.navigateTo(Screens.Details(id))
        }
    }

    fun onChatClick(transferId: Long) {
        router.navigateTo(Screens.Chat(transferId))
    }

    override fun updateCounter() {
        updateEventsCount()
    }

    fun onGetBookClicked() {
        router.exit()
    }

    companion object {
        const val HOURS_BEFORE_TRIP_FOR_SHOWING_OFFER = -24
        const val MINUTES_PER_HOUR = 60
    }
}
