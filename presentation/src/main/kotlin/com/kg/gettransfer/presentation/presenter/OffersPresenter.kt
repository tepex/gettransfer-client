package com.kg.gettransfer.presentation.presenter

import com.arellomobile.mvp.InjectViewState

import com.kg.gettransfer.domain.interactor.PaymentInteractor

import com.kg.gettransfer.domain.model.BookNowOffer
import com.kg.gettransfer.domain.model.Offer
import com.kg.gettransfer.domain.model.OfferItem
import com.kg.gettransfer.domain.model.TransportType
import com.kg.gettransfer.domain.model.Transfer

import com.kg.gettransfer.presentation.model.BookNowOfferModel
import com.kg.gettransfer.presentation.model.OfferItemModel
import com.kg.gettransfer.presentation.model.OfferModel
import com.kg.gettransfer.presentation.model.map

import com.kg.gettransfer.presentation.view.OffersView
import com.kg.gettransfer.presentation.view.OffersView.Sort
import com.kg.gettransfer.presentation.view.Screens

import com.kg.gettransfer.utilities.Analytics

import org.koin.core.inject

@Suppress("TooManyFunctions")
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
            val result = fetchResult(SHOW_ERROR) { transferInteractor.getTransfer(transferId) }
            result.error?.let { e ->
                if (e.isNotFound()) {
                    viewState.setTransferNotFoundError(transferId)
                }
            }

            result.hasData()?.let { transfer ->
                if (transfer.checkStatusCategory() != Transfer.STATUS_CATEGORY_ACTIVE ||
                    transfer.checkStatusCategory() == Transfer.STATUS_CATEGORY_ACTIVE &&
                    transfer.paidPercentage > 0) {
                    checkIfNeedNewChain()
                } else {
                    viewState.setTransfer(transfer.map(systemInteractor.transportTypes.map { it.map() }))
                    checkNewOffersSuspended(transfer)
                }
            }
            viewState.blockInterface(false)
        }
    }

    private fun checkIfNeedNewChain() {
        if (isViewRoot) routeForward() else router.exit()
    }

    private fun routeForward() {
        isViewRoot = false
        router.newChain(
            Screens.MainPassenger(),
            Screens.Requests,
            Screens.Details(transferId)
        )
    }

    fun checkNewOffers(hideRefreshSpinner: Boolean = false) {
        transfer?.let { tr ->
            utils.launchSuspend {
                checkNewOffersSuspended(tr)
                if (hideRefreshSpinner) {
                    viewState.hideRefreshSpinner()
                }
            }
        }
    }

    private suspend fun checkNewOffersSuspended(transfer: Transfer) {
        this.transfer = transfer
        fetchResult(WITHOUT_ERROR, withCacheCheck = false, checkLoginError = false) {
            offerInteractor.getOffers(transfer.id)
        }.also { result ->
            if (result.error == null && transfer.offersUpdatedAt != null) {
                fetchResultOnly { transferInteractor.setOffersUpdatedDate(transfer.id) }
            }
            if (result.error != null && !result.fromCache) {
                offers = emptyList()
            } else {
                offers = mutableListOf<OfferItem>().apply {
                    addAll(result.model)
                    notificationManager.clearOffers(result.model.map { offer -> offer.id.toInt() })
                    addAll(transfer.bookNowOffers)
                }
            }
        }
        processOffers()
    }

    override fun onNewOffer(offer: Offer): OfferModel {
        val offerModel = super.onNewOffer(offer)
        if (transferId != offer.transferId) {
            return offerModel
        }
        if (!checkDuplicated(offer)) {
            offers = offers.toMutableList().apply { add(offer) }
        }
        utils.launchSuspend { processOffers() }
        return offerModel
    }

    private fun checkDuplicated(offer: Offer): Boolean {
        var duplicated = false
        offers.forEach { item ->
            if (item is Offer && item.id == offer.id) {
                offers = offers.toMutableList().apply { set(indexOf(item), offer) }
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
                analytics.logSingleEvent(Analytics.OFFER_DETAILS)
            } else {
                analytics.logSingleEvent(Analytics.OFFER_BOOK)
                paymentInteractor.selectedTransfer = transfer
                paymentInteractor.selectedOffer = when (offerItem) {
                    is OfferModel        -> offers.find { it is Offer && it.id == offerItem.id }
                    is BookNowOfferModel ->
                        offers.find { it is BookNowOffer && it.transportType.id == offerItem.transportType.id }
                }
                viewState.blockInterface(true, true)
                router.navigateTo(Screens.PaymentOffer())
            }
        }
    }

    override fun onBackCommandClick() {
        if (isViewRoot) {
            router.newRootScreen(Screens.MainPassenger(true)).also { isViewRoot = false }
        } else {
            super.onBackCommandClick()
        }
    }

    fun cancelRequest(isCancel: Boolean) {
        if (isCancel) {
            analytics.logSingleEvent(Analytics.CANCEL_TRANSFER_BTN)
            utils.launchSuspend {
                viewState.blockInterface(true, true)
                fetchResult(withCacheCheck = false) {
                    transferInteractor.cancelTransfer(transferId, "")
                }.also { it.isSuccess()?.let { onBackCommandClick() } }
                viewState.blockInterface(false)
            }
        }
    }

    fun changeSortType(sortType: Sort) {
        if (sortCategory == sortType) {
            sortHigherToLower = !sortHigherToLower
        } else {
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
        with(countEventsInteractor) {
            val countNewOffers = (mapCountNewOffers[transferId] ?: 0) - (mapCountViewedOffers[transferId] ?: 0)
            if (countNewOffers > 0) {
                increaseViewedOffersCounter(transferId, countNewOffers)
            }
        }
        viewState.setOffers(offers.map { offer ->
            when (offer) {
                is Offer        -> offerMapper.toView(offer)
                is BookNowOffer -> offer.map()
            }
        })
        viewState.setSortState(sortCategory, sortHigherToLower)
    }

    private fun sortOffers() {
        val (sortType, comparator) = when (sortCategory) {
            Sort.YEAR   -> getByYear()
            Sort.RATING -> getByRating()
            Sort.PRICE  -> getByPrice()
        }
        offers.sortedWith(comparator)
        if (sortHigherToLower) {
            offers = offers.reversed()
        }
        analytics.logEvent(Analytics.EVENT_OFFERS, Analytics.PARAM_KEY_FILTER, sortType.name.toLowerCase())
    }

    private fun getByYear(): Pair<SortType, Comparator<OfferItem>> {
        val sortType = if (sortHigherToLower) SortType.YEAR_DESC else SortType.YEAR_ASC
        val comparator = compareBy<OfferItem> { offer ->
            when (offer) {
                is Offer        -> offer.vehicle.year
                is BookNowOffer -> 0
            }
        }
        return sortType to comparator
    }

    private fun getByRating(): Pair<SortType, Comparator<OfferItem>> {
        val sortType = if (sortHigherToLower) SortType.RATING_DESC else SortType.RATING_ASC
        val comparator = compareBy<OfferItem> { offer ->
            when (offer) {
                is Offer        -> offer.ratings?.average
                is BookNowOffer -> 0
            }
        }
        return sortType to comparator
    }

    private fun getByPrice(): Pair<SortType, Comparator<OfferItem>> {
        val sortType = if (sortHigherToLower) SortType.PRICE_DESC else SortType.PRICE_ASC
        val comparator = compareBy<OfferItem> { offer ->
            when (offer) {
                is Offer        -> offer.price.amount
                is BookNowOffer -> offer.amount
            }
        }
        return sortType to comparator
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
