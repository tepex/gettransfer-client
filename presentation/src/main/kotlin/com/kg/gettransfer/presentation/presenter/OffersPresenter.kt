package com.kg.gettransfer.presentation.presenter

import com.arellomobile.mvp.InjectViewState

import com.kg.gettransfer.domain.interactor.PaymentInteractor

import com.kg.gettransfer.domain.model.BookNowOffer
import com.kg.gettransfer.domain.model.Offer
import com.kg.gettransfer.domain.model.OfferItem
import com.kg.gettransfer.domain.model.Transfer

import com.kg.gettransfer.presentation.model.BookNowOfferModel
import com.kg.gettransfer.presentation.model.OfferItemModel
import com.kg.gettransfer.presentation.model.OfferModel
import com.kg.gettransfer.presentation.model.map

import com.kg.gettransfer.presentation.view.OffersView
import com.kg.gettransfer.presentation.view.OffersView.Sort
import com.kg.gettransfer.presentation.view.Screens

import com.kg.gettransfer.utilities.Analytics

import java.util.Date

import org.koin.core.inject

@InjectViewState
class OffersPresenter : BasePresenter<OffersView>() {

    internal var transferId = 0L
        set(value) {
            field = value
            offerInteractor.lastTransferId = value
        }

    private val paymentInteractor: PaymentInteractor by inject()

    private var transfer: Transfer? = null
    private var offers: List<OfferItem> = emptyList()

    private var sortCategory = Sort.PRICE
    private var sortHigherToLower = false
    var isViewRoot: Boolean = false

    override fun attachView(view: OffersView) {
        super.attachView(view)
        log.debug("OffersPresenter.attachView")
        utils.launchSuspend {
            viewState.blockInterface(true, true)
            fetchResult(SHOW_ERROR) { transferInteractor.getTransfer(transferId) }
                    .also {
                        it.error?.let { e ->
                            if (e.isNotFound()) viewState.setTransferNotFoundError(transferId)
                        }

                        it.hasData()?.let { transfer ->
                            if (transfer.checkStatusCategory() != Transfer.STATUS_CATEGORY_ACTIVE ||
                                    (transfer.checkStatusCategory() == Transfer.STATUS_CATEGORY_ACTIVE &&
                                            transfer.paidPercentage > 0))
                                checkIfNeedNewChain()
                            else {
                                viewState.setTransfer(transferMapper.toView(transfer))
                                checkNewOffersSuspended(transfer)
                            }
                        }
                    }
            viewState.blockInterface(false)
        }
    }

    private fun checkIfNeedNewChain() {
        if (isViewRoot)
            routeForward()
        else router.exit()
    }

    private fun routeForward() {
        isViewRoot = false
        router.newChain(
                Screens.MainPassenger(),
                Screens.Requests,
                Screens.Details(transferId)
        )
    }

    fun checkNewOffers() {
        transfer?.let { tr -> utils.launchSuspend { checkNewOffersSuspended(tr) } }
    }

    private suspend fun checkNewOffersSuspended(transfer: Transfer) {
        this.transfer = transfer
        fetchResult(WITHOUT_ERROR, withCacheCheck = false, checkLoginError = false) { offerInteractor.getOffers(transfer.id) }
                .also {
                    if (it.error == null && transfer.offersUpdatedAt != null) fetchResultOnly { transferInteractor.setOffersUpdatedDate(transfer.id) }
                    if (it.error != null && !it.fromCache) offers = emptyList()
                    else {
                        offers = mutableListOf<OfferItem>().apply {
                            addAll(it.model)
                            notificationManager.clearOffers(it.model.map { offer -> offer.id.toInt() })
                            addAll(transfer.bookNowOffers) }
                    } }
        processOffers()
    }

    override fun onNewOffer(offer: Offer): OfferModel {
        val offerModel = super.onNewOffer(offer)
        if (transferId != offer.transferId) return offerModel
        if (!checkDuplicated(offer))
            offers = offers.toMutableList().apply { add(offer) }
        utils.launchSuspend { processOffers() }
        return offerModel
    }

    private fun checkDuplicated(offer: Offer): Boolean {
        var duplicated = false
        offers.forEach {
            if (it is Offer && it.id == offer.id) {
                offers = offers.toMutableList().apply { set(indexOf(it), offer) }
                duplicated = true
            }
        }
        return duplicated
    }

    fun onRequestInfoClicked() {
        router.navigateTo(Screens.Details(transferId))
    }

    fun onSelectOfferClicked(offerItem: OfferItemModel, isShowingOfferDetails: Boolean) {
        transfer?.let {
            if (isShowingOfferDetails) {
                viewState.showBottomSheetOfferDetails(offerItem)
                logButtons(Analytics.OFFER_DETAILS)
            } else {
                logButtons(Analytics.OFFER_BOOK)
                paymentInteractor.selectedTransfer = transfer
                paymentInteractor.selectedOffer = when (offerItem) {
                    is OfferModel -> offers.filter { offer -> offer is Offer }.find { offer -> (offer as Offer).id == offerItem.id }
                    is BookNowOfferModel -> offers.filter { offer -> offer is BookNowOffer }.find { offer -> (offer as BookNowOffer).transportType.id == offerItem.transportType.id }
                }
                viewState.blockInterface(true, true)
                router.navigateTo(Screens.PaymentOffer())
            }
        }
    }

    fun logButtons(event: String) {
        analytics.logEventToFirebase(event, null)
        analytics.logEventToYandex(event, null)
    }

    fun onCancelRequestClicked() {
        viewState.showAlertCancelRequest()
    }

    override fun onBackCommandClick() {
        if (isViewRoot)
            router.newRootScreen(Screens.MainPassenger(true)).also { isViewRoot = false }
        else super.onBackCommandClick()
    }

    fun cancelRequest(isCancel: Boolean) {
        if (!isCancel) return
        logButtons(Analytics.CANCEL_TRANSFER_BTN)
        utils.launchSuspend {
            viewState.blockInterface(true, true)
            fetchResult (withCacheCheck = false) { transferInteractor.cancelTransfer(transferId, "") }
                    .also { it.isSuccess()?.let { onBackCommandClick() } }
            viewState.blockInterface(false)
        }
    }

    fun changeSortType(sortType: Sort) {
        if (sortCategory == sortType) sortHigherToLower = !sortHigherToLower
        else {
            sortCategory = sortType
            sortHigherToLower = when (sortType) {
                Sort.YEAR   -> true
                Sort.RATING -> true
                Sort.PRICE  -> false
            }
        }
        processOffers()
    }

    private fun processOffers() {
        sortOffers()
        with (countEventsInteractor) {
            val countNewOffers = (mapCountNewOffers[transferId] ?: 0) - (mapCountViewedOffers[transferId] ?: 0)
            if (countNewOffers > 0) increaseViewedOffersCounter(transferId, countNewOffers)
        }
        viewState.setOffers(offers.map {
            when (it) {
                is Offer -> offerMapper.toView(it)
                is BookNowOffer -> it.map()
            }
        })
        viewState.setSortState(sortCategory, sortHigherToLower)
    }

    private fun sortOffers() {
        val sortType: SortType
        offers = when (sortCategory) {
            Sort.YEAR -> {
                sortType = if (sortHigherToLower) SortType.YEAR_DESC else SortType.YEAR_ASC
                offers.sortedWith(compareBy {
                    when (it) {
                        is Offer -> it.vehicle.year
                        else -> 0
                    }
                })
            }
            Sort.RATING -> {
                sortType = if (sortHigherToLower) SortType.RATING_DESC else SortType.RATING_ASC
                offers.sortedWith(compareBy {
                    when (it) {
                        is Offer -> it.ratings?.average
                        else -> 0
                    }
                })
            }
            Sort.PRICE -> {
                sortType = if (sortHigherToLower) SortType.PRICE_DESC else SortType.PRICE_ASC
                offers.sortedWith(compareBy {
                    when (it) {
                        is Offer-> it.price.amount
                        is BookNowOffer -> it.amount
                    }
                })
            }
        }
        if (sortHigherToLower) offers = offers.reversed()
        logFilterEvent(sortType)
    }

    private fun logFilterEvent(sortType: SortType) {
        val map = mutableMapOf<String, Any>()
        val value = sortType.name.toLowerCase()
        map[Analytics.PARAM_KEY_FILTER] = value

        analytics.logEvent(Analytics.EVENT_OFFERS, createStringBundle(Analytics.PARAM_KEY_FILTER, value), map)
    }

    fun updateBanners() {
        viewState.setBannersVisible(offers.isNotEmpty())
    }

    enum class SortType {
        RATING_ASC, RATING_DESC,
        PRICE_ASC, PRICE_DESC,
        YEAR_ASC, YEAR_DESC;
    }
}
