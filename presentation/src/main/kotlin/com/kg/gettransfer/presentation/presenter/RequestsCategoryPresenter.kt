package com.kg.gettransfer.presentation.presenter

import android.os.Handler

import com.arellomobile.mvp.InjectViewState

import com.kg.gettransfer.core.presentation.WorkerManager

import com.kg.gettransfer.domain.eventListeners.AccountChangedListener
import com.kg.gettransfer.domain.eventListeners.CoordinateEventListener
import com.kg.gettransfer.domain.eventListeners.CounterEventListener
import com.kg.gettransfer.domain.interactor.CoordinateInteractor
import com.kg.gettransfer.domain.model.Coordinate
import com.kg.gettransfer.domain.model.Transfer

import com.kg.gettransfer.presentation.delegate.DriverCoordinate
import com.kg.gettransfer.presentation.model.TransferModel
import com.kg.gettransfer.presentation.model.map

import com.kg.gettransfer.presentation.view.RequestsFragmentView
import com.kg.gettransfer.presentation.view.RequestsView
import com.kg.gettransfer.presentation.view.RequestsView.TransferTypeAnnotation.Companion.TRANSFER_ACTIVE
import com.kg.gettransfer.presentation.view.RequestsView.TransferTypeAnnotation.Companion.TRANSFER_ARCHIVE
import com.kg.gettransfer.presentation.view.Screens

import com.kg.gettransfer.sys.presentation.ConfigsManager

import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

import org.koin.core.KoinComponent
import org.koin.core.inject
import org.koin.core.parameter.parametersOf

import java.util.Calendar
import java.util.Date

@InjectViewState
@Suppress("TooManyFunctions")
class RequestsCategoryPresenter(
    @RequestsView.TransferTypeAnnotation tt: Int
) : BasePresenter<RequestsFragmentView>(),
    KoinComponent,
    CounterEventListener,
    CoordinateEventListener,
    AccountChangedListener {

    private val worker: WorkerManager by inject { parametersOf("RequestsCategoryPresenter") }
    private val coordinateInteractor: CoordinateInteractor by inject()

    @RequestsView.TransferTypeAnnotation
    var transferType = tt

    private var transfers: List<Transfer>? = null
    private var driverCoordinate: DriverCoordinate? = null
    private val configsManager: ConfigsManager by inject()
    private var pagesCount: Int? = null // for pagination

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.blockInterface(true, true)
    }

    override fun attachView(view: RequestsFragmentView) {
        super.attachView(view)
        countEventsInteractor.addCounterListener(this)
        sessionInteractor.addAccountChangedListener(this)
        getTransfers()
    }

    override fun detachView(view: RequestsFragmentView?) {
        super.detachView(view)
        transfers = null
        countEventsInteractor.removeCounterListener(this)
        coordinateInteractor.removeCoordinateListener(this)
        driverCoordinate?.requestCoordinates = false
        driverCoordinate = null
        sessionInteractor.removeAccountChangedListener(this)
    }

    @Suppress("MandatoryBracesIfStatements")
    fun getTransfers(page: Int = 1) {
        worker.main.launch {
            transfers = when (transferType) {
                TRANSFER_ACTIVE -> withContext(worker.bg) {
                    if (isBusinessAccount()) getAllTransfersAnsPagesCount(page, Transfer.STATUS_CATEGORY_ACTIVE)
                    else transferInteractor.getTransfersActive().model
                }
                TRANSFER_ARCHIVE -> withContext(worker.bg) {
                    if (isBusinessAccount()) getAllTransfersAnsPagesCount(page, Transfer.STATUS_CATEGORY_ARCHIVE)
                    else transferInteractor.getTransfersArchive().model
                }
                else -> error("Wrong transfer type in ${this@RequestsCategoryPresenter::class.java.name}")
            }.sortedByDescending { it.dateToLocal }

            setupCoordinate()
            prepareDataAsync()
        }
    }

    @Suppress("NestedBlockDepth", "UnnecessaryLet")
    private fun setupCoordinate() {
        if (transferType == TRANSFER_ACTIVE && !transfers.isNullOrEmpty()) {
            coordinateInteractor.addCoordinateListener(this@RequestsCategoryPresenter)
            if (driverCoordinate == null) {
                driverCoordinate = DriverCoordinate(Handler())
            } else {
                driverCoordinate?.let { coordinate ->
                    coordinate.transfersIds = transfers?.let { transfer ->
                        transfer.map { it.id }
                    }
                }
            }
        }
    }

    private suspend fun getAllTransfersAnsPagesCount(page: Int, status: String): List<Transfer> {
        val allTransfers = transferInteractor.getAllTransfers(getUserRole(), page, status)
        pagesCount = allTransfers.model.second
        return allTransfers.model.first
    }

    private suspend fun prepareDataAsync() {
        worker.main.launch {
            transfers?.let { trs ->
                if (trs.isNotEmpty()) {
                    withContext(worker.bg) {
                        val transportTypes = configsManager.configs.transportTypes.map { it.map() }
                        transfers?.map { it.map(transportTypes) }?.map { transferModel ->
                            if (transferModel.status == Transfer.Status.PERFORMED && isShowOfferInfo(transferModel)) {
                                val offer = offerInteractor.getOffers(transferModel.id).model.let { list ->
                                    if (list.size == 1) list.first() else null
                                }
                                transferModel.copy(matchedOffer = offer)
                            } else {
                                transferModel
                            }
                        }
                    }?.also { transfersList ->
                        viewState.updateTransfers(transfersList, pagesCount)
                        viewState.blockInterface(false)
                        updateEventsCount()
                    }
                    sendAnalytics()
                } else {
                    viewState.blockInterface(false)
                    viewState.onEmptyList()
                }
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

    private fun sendAnalytics() {
        when (transferType) {
            TRANSFER_ACTIVE -> transfers?.let { analytics.EcommercePurchase().sendAnalytics(it) }
        }
    }

    private fun updateEventsCount() {
        worker.main.launch {
            withContext(worker.bg) {
                with(countEventsInteractor) {
                    getEventsCount(mapCountNewOffers.plus(mapCountNewMessages), mapCountViewedOffers)
                }
            }.run { viewState.updateEvents(this) }
        }
    }

    private fun getEventsCount(
        mapCountNewEvents: Map<Long, Int>,
        mapCountViewedOffers: Map<Long, Int>
    ): Map<Long, Int> {
        val eventsMap = mutableMapOf<Long, Int>()
        transfers?.forEach { transfer ->
            mapCountNewEvents[transfer.id]?.let { count ->
                val eventsCount = count - (mapCountViewedOffers[transfer.id] ?: 0)
                if (eventsCount > 0) {
                    eventsMap[transfer.id] = eventsCount
                }
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

    override fun accountChanged() {
        getTransfers()
    }

    companion object {
        const val HOURS_BEFORE_TRIP_FOR_SHOWING_OFFER = -24
        const val MINUTES_PER_HOUR = 60
    }
}
