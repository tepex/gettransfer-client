package com.kg.gettransfer.presentation.presenter

import android.support.annotation.CallSuper
import android.util.Log

import com.arellomobile.mvp.InjectViewState

import com.kg.gettransfer.domain.ApiException

import com.kg.gettransfer.domain.model.Offer
import com.kg.gettransfer.domain.model.Transfer

import com.kg.gettransfer.presentation.mapper.TransferMapper
import com.kg.gettransfer.presentation.mapper.TransportTypeMapper
import com.kg.gettransfer.presentation.model.*

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
    var itemsExpanded: Boolean? = null
        get() = field ?: offerInteractor.offerViewExpanded
    set(value) {
        field = value
        offerInteractor.offerViewExpanded = value!!
    }
    var isViewRoot: Boolean = false

    @CallSuper
    override fun attachView(view: OffersView) {
        super.attachView(view)
        Timber.d("OffersPresenter.attachView")
        utils.launchSuspend {
            viewState.blockInterface(true, true)
            val result = utils.asyncAwait { transferInteractor.getTransfer(transferId) }
            result.error?.let { checkResultError(it) }
            if (result.error != null) {
                val err = result.error!!
                Timber.e(err)
                //if (err.isNotLoggedIn()) viewState.redirectView()
                //else if (err.code != ApiException.NETWORK_ERROR) viewState.setError(err)
                if (err.isNotFound()) viewState.setError(ApiException(ApiException.NOT_FOUND, "Transfer $transferId not found!"))
            }
            if(result.error == null || (result.error != null && result.fromCache)) {
                if (result.model.checkStatusCategory() != Transfer.STATUS_CATEGORY_ACTIVE) {
                    if (isViewRoot) {
                        isViewRoot = false
                        router.navigateTo(Screens.Details(transferId))
                    }
                    else router.exit()
                }
                else {
                    val transferModel = transferMapper.toView(result.model)
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
        if (result.error != null && !result.fromCache) {
            offers = emptyList()
            Timber.e(result.error)
        } else {
            offers = mutableListOf<OfferItem>().apply {
                addAll(result.model.map { offerMapper.toView(it) })
                notificationManager.clearOffers(result.model.map { it.id.toInt() })
                addAll(transferModel.bookNowOffers)
            }
        }
        //changeSortType(SORT_PRICE)
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
        var duplicated: Boolean = false
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
                                it.paymentPercentages,
                                null
                        ))
                    is BookNowOfferModel -> router.navigateTo(Screens.PaymentOffer(
                        it.id,
                        null,
                        it.dateRefund,
                        it.paymentPercentages,
                        offer.transportType.id.toString()
                    ))
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

    /*fun openLoginView() {
        login("", "")
    }*/

    fun cancelRequest(isCancel: Boolean) {
        if (!isCancel) return
        logButtons(Analytics.CANCEL_TRANSFER_BTN)
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

    fun hasAnyRate(carrier: CarrierModel) =
            with(carrier.ratings) {
                return@with (driver != null && driver != NO_RATE) ||
                        (vehicle != null && vehicle != NO_RATE) ||
                        (fair != null && fair != NO_RATE) ||
                        carrier.approved


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
