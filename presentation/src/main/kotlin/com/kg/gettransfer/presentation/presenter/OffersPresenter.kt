package com.kg.gettransfer.presentation.presenter

import android.support.annotation.CallSuper

import com.arellomobile.mvp.InjectViewState

import com.kg.gettransfer.domain.ApiException

import com.kg.gettransfer.domain.model.Offer
import com.kg.gettransfer.domain.model.Transfer

import com.kg.gettransfer.presentation.mapper.TransferMapper
import com.kg.gettransfer.presentation.mapper.TransportTypeMapper
import com.kg.gettransfer.presentation.model.BookNowOfferModel
import com.kg.gettransfer.presentation.model.OfferItem

import com.kg.gettransfer.presentation.model.OfferModel
import com.kg.gettransfer.presentation.model.TransportTypeModel

import com.kg.gettransfer.presentation.ui.SystemUtils

import com.kg.gettransfer.presentation.view.OffersView
import com.kg.gettransfer.presentation.view.OffersView.Sort
import com.kg.gettransfer.presentation.view.Screens

import com.kg.gettransfer.utilities.Analytics

import org.koin.standalone.inject

import timber.log.Timber

@InjectViewState
class OffersPresenter : BasePresenter<OffersView>() {
    private val transferMapper: TransferMapper by inject()
    private val transportTypeMapper: TransportTypeMapper by inject()

    internal var transferId = 0L
        set(value) {
            field = value
            offerInteractor.lastTransferId = value
        }

    private var transfer: Transfer? = null
    private var offers: List<OfferItem> = emptyList()
    private lateinit var transportTypes: List<TransportTypeModel>

    private var sortCategory = Sort.PRICE
    private var sortHigherToLower = false

    @CallSuper
    override fun attachView(view: OffersView) {
        super.attachView(view)
        Timber.d("OffersPresenter.attachView")
        utils.launchSuspend {
            viewState.blockInterface(true, true)
            val result = utils.asyncAwait { transferInteractor.getTransfer(transferId) }
            if (result.error != null) {
                val err = result.error!!
                Timber.e(err)
                if (err.isNotLoggedIn()) viewState.redirectView()
                else if (err.code != ApiException.NETWORK_ERROR) viewState.setError(err)
            } else {
                if (result.model.checkStatusCategory() != Transfer.STATUS_CATEGORY_ACTIVE) router.exit()
                else {
                    val transferModel = transferMapper.toView(result.model)
                    viewState.setDate(SystemUtils.formatDateTime(transferModel.dateTime))
                    viewState.setTransfer(transferModel)
                    transportTypes = systemInteractor.transportTypes.map { transportTypeMapper.toView(it) }
                    checkNewOffersSuspended(result.model)
                }
            }
            viewState.blockInterface(false)
        }
    }

    fun checkNewOffers() {
        transfer?.let { tr -> utils.launchSuspend { checkNewOffersSuspended(tr) } }
    }

    private suspend fun checkNewOffersSuspended(transfer: Transfer) {
        this.transfer = transfer
        val result = utils.asyncAwait { offerInteractor.getOffers(transfer.id) }
        val transferModel = transferMapper.toView(transfer)
        if (result.error != null) {
            offers = emptyList()
            Timber.e(result.error)
        } else {
            offers = mutableListOf<OfferItem>().apply {
                addAll(result.model.map { offerMapper.toView(it) })
                transferModel.bookNowOffers?.let { addAll(it) }
            }
        }
        //changeSortType(SORT_PRICE)
        processOffers()
    }

    override fun onNewOffer(offer: Offer): OfferModel {
        val offerModel = super.onNewOffer(offer)
        offers = offers.toMutableList().apply { add(offerModel) }
        utils.launchSuspend { processOffers() }
        return offerModel
    }

    fun onRequestInfoClicked() {
        router.navigateTo(Screens.Details(transferId))
    }

    fun onSelectOfferClicked(offer: OfferItem, isShowingOfferDetails: Boolean) {
        transfer?.let {
            if (isShowingOfferDetails) {
                viewState.showBottomSheetOfferDetails(offer)
                logEvent(Analytics.OFFER_DETAILS)
            } else {
                logEvent(Analytics.OFFER_BOOK)
                when(offer) {
                    is OfferModel -> router.navigateTo(Screens.PaymentSettings(
                        it.id,
                        offer.id,
                        it.dateRefund,
                        it.paymentPercentages,
                        ""
                    ))
                    is BookNowOfferModel -> router.navigateTo(Screens.PaymentSettings(
                        it.id,
                        null,
                        it.dateRefund,
                        it.paymentPercentages,
                        offer.transportType.id.name
                    ))
                }
            }
        }
    }

    fun logEvent(value: String) {
        val map = mutableMapOf<String, Any>()
        map[Analytics.PARAM_KEY_NAME] = value

        analytics.logEvent(Analytics.EVENT_BUTTONS, createStringBundle(Analytics.PARAM_KEY_NAME, value), map)
    }

    fun onCancelRequestClicked() {
        viewState.showAlertCancelRequest()
    }

    fun openLoginView() {
        login("", "")
    }

    fun cancelRequest(isCancel: Boolean) {
        if (!isCancel) return
        logEvent(Analytics.CANCEL_TRANSFER_BTN)
        utils.launchSuspend {
            viewState.blockInterface(true, true)
            val result = utils.asyncAwait { transferInteractor.cancelTransfer(transferId, "") }
            if (result.error != null) {
                Timber.e(result.error!!)
                viewState.setError(result.error!!)
            } else onBackCommandClick()
            viewState.blockInterface(false)
        }
    }

    fun changeSortType(sortType: Sort) {
        if (sortCategory == sortType) sortHigherToLower = !sortHigherToLower
        else {
            sortCategory = sortType
            when (sortType) {
                Sort.YEAR -> sortHigherToLower = true
                Sort.RATING -> sortHigherToLower = true
                Sort.PRICE -> sortHigherToLower = false
            }
        }
        processOffers()
    }

    private fun processOffers() {
        sortOffers()
        decreaseCountEvents()
        viewState.setOffers(offers)
        viewState.setSortState(sortCategory, sortHigherToLower)
    }

    private fun decreaseCountEvents() {
        var transferIds = systemInteractor.transferIds
        if (transferIds.isNotEmpty()) {
            transferIds = transferIds.toMutableList().apply { removeAll { (it == transferId) } }
            val seenOffers = systemInteractor.transferIds.size - transferIds.size
            val eventsCount = systemInteractor.eventsCount
            systemInteractor.eventsCount = eventsCount - seenOffers
            systemInteractor.transferIds = transferIds
        }
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

    enum class SortType {
        RATING_ASC, RATING_DESC,
        PRICE_ASC, PRICE_DESC,
        YEAR_ASC, YEAR_DESC;
    }
}
