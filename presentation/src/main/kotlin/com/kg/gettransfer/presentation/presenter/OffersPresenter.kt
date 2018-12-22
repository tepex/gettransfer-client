package com.kg.gettransfer.presentation.presenter

import android.support.annotation.CallSuper

import com.arellomobile.mvp.InjectViewState

import com.kg.gettransfer.domain.ApiException

import com.kg.gettransfer.domain.interactor.OfferInteractor
import com.kg.gettransfer.domain.interactor.TransferInteractor

import com.kg.gettransfer.domain.model.Offer
import com.kg.gettransfer.domain.model.Transfer

import com.kg.gettransfer.presentation.mapper.OfferMapper
import com.kg.gettransfer.presentation.mapper.TransferMapper

import com.kg.gettransfer.presentation.model.OfferModel

import com.kg.gettransfer.presentation.ui.SystemUtils

import com.kg.gettransfer.presentation.view.OffersView
import com.kg.gettransfer.presentation.view.Screens

import com.kg.gettransfer.utilities.Analytics

import com.yandex.metrica.YandexMetrica

import org.koin.standalone.inject

import timber.log.Timber

@InjectViewState
class OffersPresenter : BasePresenter<OffersView>() {
    private val transferInteractor: TransferInteractor by inject()
    private val offerInteractor: OfferInteractor by inject()

    private val offerMapper: OfferMapper by inject()
    private val transferMapper: TransferMapper by inject()

    internal var transferId = 0L
        set(value) {
            field = value
            offerInteractor.lastTransferId = value
        }

    private var transfer: Transfer? = null
    private lateinit var offers: List<OfferModel>

    private var sortCategory: String = SORT_PRICE
    private var sortHigherToLower = false

    companion object {
        @JvmField val RATING_UP   = "rating_asc"
        @JvmField val RATING_DOWN = "rating_desc"

        @JvmField val PRICE_UP    = "price_asc"
        @JvmField val PRICE_DOWN  = "price_desc"

        @JvmField val YEAH_FILTER_UP   = "year_asc"
        @JvmField val YEAH_FILTER_DOWN = "year_desc"

        @JvmField val SORT_YEAR   = "sort_year"
        @JvmField val SORT_RATING = "sort_rating"
        @JvmField val SORT_PRICE  = "sort_price"
    }

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
        if (result.error != null) {
            offers = emptyList<OfferModel>()
            Timber.e(result.error)
        } else {
            offers = result.model.map { offer -> offerMapper.toView(offer) }
            //changeSortType(SORT_PRICE)
            processOffers()
        }
    }

    /*
    fun onNewOffer(offer: Offer) {
        offerInteractor.newOffer(offer)
        offers = offers.toMutableList().apply { add(offerMapper.toView(offer)) }
        utils.launchSuspend { processOffers() }
    }
    */

    fun onRequestInfoClicked() { router.navigateTo(Screens.Details(transferId)) }

    fun onSelectOfferClicked(offer: OfferModel, isShowingOfferDetails: Boolean) {
        transfer?.let {
            if (isShowingOfferDetails) {
                viewState.showBottomSheetOfferDetails(offer)
                logEvent(Analytics.OFFER_DETAILS)
            } else {
                logEvent(Analytics.OFFER_BOOK)
                router.navigateTo(Screens.PaymentSettings(it.id, offer.id, it.dateRefund, it.paymentPercentages))
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

    fun openLoginView() { login("", "") }

    fun cancelRequest(isCancel: Boolean) {
        if(!isCancel) return
        logEvent(Analytics.CANCEL_TRANSFER_BTN)
        utils.launchSuspend {
            viewState.blockInterface(true, true)
            val result = utils.asyncAwait { transferInteractor.cancelTransfer(transferId, "") }
            if(result.error != null) {
                Timber.e(result.error!!)
                viewState.setError(result.error!!)
            }
            else onBackCommandClick()
            viewState.blockInterface(false)
        }
    }

    fun changeSortType(sortType: String) {
        if (sortCategory == sortType) sortHigherToLower = !sortHigherToLower
        else {
            sortCategory = sortType
            when (sortType) {
                SORT_YEAR   -> sortHigherToLower = true
                SORT_RATING -> sortHigherToLower = true
                SORT_PRICE  -> sortHigherToLower = false
            }
        }
        processOffers()
    }

    private fun processOffers() {
        sortOffers()
        viewState.setOffers(offers)
        viewState.setSortState(sortCategory, sortHigherToLower)
    }

    private fun sortOffers() {
        var sortType = ""
        offers = when(sortCategory) {
            SORT_YEAR -> {
                sortType = if(sortHigherToLower) YEAH_FILTER_DOWN else YEAH_FILTER_UP
                offers.sortedWith(compareBy { it.vehicle.year })
            }
            SORT_RATING -> {
                sortType = if(sortHigherToLower) RATING_DOWN else RATING_UP
                offers.sortedWith(compareBy { it.ratings?.average })
            }
            SORT_PRICE -> {
                sortType = if(sortHigherToLower) PRICE_DOWN else PRICE_UP
                offers.sortedWith(compareBy { it.price.amount })
            }
            else -> offers
        }
        if(sortHigherToLower) offers = offers.reversed()
        logFilterEvent(sortType)
    }

    private fun logFilterEvent(value: String) {
        val map = mutableMapOf<String, Any>()
        map[Analytics.PARAM_KEY_FILTER] = value

        analytics.logEvent(Analytics.EVENT_OFFERS, createStringBundle(Analytics.PARAM_KEY_FILTER, value), map)
    }

}
