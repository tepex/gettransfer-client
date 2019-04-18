package com.kg.gettransfer.presentation.presenter

import android.support.annotation.CallSuper

import com.arellomobile.mvp.InjectViewState

import com.kg.gettransfer.domain.ApiException

import com.kg.gettransfer.domain.model.Offer
import com.kg.gettransfer.domain.model.Transfer

import com.kg.gettransfer.presentation.mapper.TransportTypeMapper
import com.kg.gettransfer.presentation.model.OfferItem
import com.kg.gettransfer.presentation.model.OfferModel
import com.kg.gettransfer.presentation.model.BookNowOfferModel
import com.kg.gettransfer.presentation.model.CarrierModel

import com.kg.gettransfer.presentation.view.OffersView
import com.kg.gettransfer.presentation.view.OffersView.Sort
import com.kg.gettransfer.presentation.view.Screens

import com.kg.gettransfer.utilities.Analytics

import org.koin.standalone.inject

import timber.log.Timber

@InjectViewState
class OffersPresenter : BasePresenter<OffersView>() {
    private val transportTypeMapper: TransportTypeMapper by inject()

    internal var transferId = 0L
        set(value) {
            field = value
            offerInteractor.lastTransferId = value
        }

    private var transfer: Transfer? = null
    private var offers: List<OfferItem> = emptyList()

    private var sortCategory = Sort.PRICE
    private var sortHigherToLower = false
    var isViewRoot: Boolean = false

    @CallSuper
    override fun attachView(view: OffersView) {
        super.attachView(view)
        Timber.d("OffersPresenter.attachView")
        utils.launchSuspend {
            viewState.blockInterface(true, true)
            fetchResult(SHOW_ERROR) { transferInteractor.getTransfer(transferId) }
                    .also {
                        it.error?.let { e ->
                            if (e.isNotFound())
                                viewState.setError(ApiException(ApiException.NOT_FOUND, "Transfer $transferId not found!"))
                        }

                        it.hasData()?.let { transfer ->
                            if (transfer.checkStatusCategory() != Transfer.STATUS_CATEGORY_ACTIVE)
                                routeToScreen()
                            else {
                                viewState.setTransfer(transferMapper.toView(transfer))
                                checkNewOffersSuspended(transfer)
                            }
                        }
                    }
            viewState.blockInterface(false)
        }
    }

    private fun routeToScreen() {
        if (isViewRoot) {
            isViewRoot = false
            router.newChain(Screens.Main, Screens.Requests, Screens.Details(transferId))
        }
        else router.exit()
    }

    fun checkNewOffers() {
        transfer?.let { tr -> utils.launchSuspend { checkNewOffersSuspended(tr) } }
    }

    private suspend fun checkNewOffersSuspended(transfer: Transfer) {
        this.transfer = transfer
        fetchResult(WITHOUT_ERROR, withCacheCheck = false, checkLoginError = false) { offerInteractor.getOffers(transfer.id) }
                .also {
                    if (it.error != null && !it.fromCache) offers = emptyList()
                    else {
                        offers = mutableListOf<OfferItem>().apply {
                            addAll(it.model.map { offer -> offerMapper.toView(offer) })
                            notificationManager.clearOffers(it.model.map { offer -> offer.id.toInt() })
                            addAll(transferMapper.toView(transfer).bookNowOffers) }
                    } }
        processOffers()
    }

    override fun onNewOffer(offer: Offer): OfferModel {
        val offerModel = super.onNewOffer(offer)
        if (transferId != offer.transferId) return offerModel
        if (!checkDuplicated(offerModel))
            offers = offers.toMutableList().apply { add(offerModel) }
        utils.launchSuspend { processOffers() }
        return offerModel
    }

    private fun checkDuplicated(offer: OfferModel): Boolean {
        var duplicated = false
        offers.forEach {
            if (it is OfferModel && it.id == offer.id) {
                offers = offers.toMutableList().apply { set(indexOf(it), offer) }
                duplicated = true
            }
        }
        return duplicated
    }

    fun onRequestInfoClicked() {
        router.navigateTo(Screens.Details(transferId))
    }

    fun onSelectOfferClicked(offer: OfferItem, isShowingOfferDetails: Boolean) {
        transfer?.let {
            if (isShowingOfferDetails) {
                viewState.showBottomSheetOfferDetails(offer)
                logButtons(Analytics.OFFER_DETAILS)
            } else {
                logButtons(Analytics.OFFER_BOOK)
                when(offer) {
                    is OfferModel ->
                        router.navigateTo(Screens.PaymentOffer(
                                it.id,
                                offer.id,
                                it.dateRefund,
                                it.paymentPercentages!!,
                                null))
                    is BookNowOfferModel ->
                        router.navigateTo(Screens.PaymentOffer(
                                it.id,
                        null,
                                it.dateRefund,
                                it.paymentPercentages!!,
                                offer.transportType.id.toString()))
                }
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
            router.navigateTo(Screens.Main).also { isViewRoot = false }
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
        viewState.setOffers(offers)
        viewState.setSortState(sortCategory, sortHigherToLower)
    }

    private fun sortOffers() {
        val sortType: SortType
        offers = when (sortCategory) {
            Sort.YEAR -> {
                sortType = if (sortHigherToLower) SortType.YEAR_DESC else SortType.YEAR_ASC
                offers.sortedWith(compareBy {
                    when (it) {
                        is OfferModel -> it.vehicle.year
                        else -> 0
                    }
                })
            }
            Sort.RATING -> {
                sortType = if (sortHigherToLower) SortType.RATING_DESC else SortType.RATING_ASC
                offers.sortedWith(compareBy {
                    when (it) {
                        is OfferModel -> it.ratings?.average
                        else -> 0
                    }
                })
            }
            Sort.PRICE -> {
                sortType = if (sortHigherToLower) SortType.PRICE_DESC else SortType.PRICE_ASC
                offers.sortedWith(compareBy {
                    when (it) {
                        is OfferModel -> it.price.amount
                        is BookNowOfferModel -> it.amount
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

    fun hasAnyRate(carrier: CarrierModel) =
            with(carrier.ratings) {
                return@with (driver != null && driver != NO_RATE) ||
                        (vehicle != null && vehicle != NO_RATE) ||
                        (fair != null && fair != NO_RATE) ||
                        carrier.approved


            }

    fun updateBanners() {
        viewState.setBannersVisible(offers.isNotEmpty())
    }

    enum class SortType {
        RATING_ASC, RATING_DESC,
        PRICE_ASC, PRICE_DESC,
        YEAR_ASC, YEAR_DESC;
    }

    companion object {
        const val NO_RATE = 0f
    }
}
