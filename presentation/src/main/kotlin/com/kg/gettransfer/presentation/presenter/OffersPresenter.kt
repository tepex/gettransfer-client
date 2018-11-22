package com.kg.gettransfer.presentation.presenter

import android.support.annotation.CallSuper

import com.arellomobile.mvp.InjectViewState

import com.kg.gettransfer.domain.ApiException

import com.kg.gettransfer.domain.interactor.OfferInteractor
import com.kg.gettransfer.domain.interactor.TransferInteractor

import com.kg.gettransfer.domain.model.Offer
import com.kg.gettransfer.domain.model.Transfer

import com.kg.gettransfer.presentation.model.Mappers
import com.kg.gettransfer.presentation.model.OfferModel

import com.kg.gettransfer.presentation.view.OffersView
import com.kg.gettransfer.presentation.view.Screens
import com.kg.gettransfer.utilities.Analytics
import com.kg.gettransfer.utilities.Analytics.Companion.EVENT_OFFERS
import com.kg.gettransfer.utilities.Analytics.Companion.OFFER_BOOK
import com.kg.gettransfer.utilities.Analytics.Companion.OFFER_DETAILS
import com.kg.gettransfer.utilities.Analytics.Companion.PARAM_KEY_FILTER
import com.kg.gettransfer.utilities.Analytics.Companion.PRICE_DOWN
import com.kg.gettransfer.utilities.Analytics.Companion.PRICE_UP
import com.kg.gettransfer.utilities.Analytics.Companion.RATING_DOWN
import com.kg.gettransfer.utilities.Analytics.Companion.RATING_UP
import com.kg.gettransfer.utilities.Analytics.Companion.YEAH_FILTER_DOWN
import com.kg.gettransfer.utilities.Analytics.Companion.YEAH_FILTER_UP

import com.yandex.metrica.YandexMetrica

import org.koin.standalone.inject

import timber.log.Timber

@InjectViewState
class OffersPresenter: BasePresenter<OffersView>() {
    private val transferInteractor: TransferInteractor by inject()
    private val offerInteractor: OfferInteractor by inject()

    /*
    init {
        router.setResultListener(LoginPresenter.RESULT_CODE, { _ -> onFirstViewAttach() })
    }
    */

    internal var transferId = 0L
    
    private lateinit var transfer: Transfer 
    private lateinit var offers: List<OfferModel>

    private var sortCategory: String = SORT_PRICE
    private var sortHigherToLower = false

    companion object {
        @JvmField val SORT_YEAR   = "sort_year"
        @JvmField val SORT_RATING = "sort_rating"
        @JvmField val SORT_PRICE  = "sort_price"
    }

    @CallSuper
    override fun attachView(view: OffersView) {
        super.attachView(view)
        utils.launchSuspend {
            viewState.blockInterface(true, true)
            val result = utils.asyncAwait { transferInteractor.getTransfer(transferId) }
            if(result.error != null) {
                Timber.e(result.error!!)
                if(result.error!!.isNotLoggedIn()) viewState.redirectView()
                else if(result.error!!.code != ApiException.NETWORK_ERROR) viewState.setError(result.error!!)
            } else {
                transfer = result.model
                val transferModel = Mappers.getTransferModel(transfer,
                                                             systemInteractor.locale,
                                                             systemInteractor.distanceUnit,
                                                             systemInteractor.transportTypes)
                viewState.setDate(transferModel.dateTime)
                viewState.setTransfer(transferModel)

                val r = utils.asyncAwait{ offerInteractor.getOffers(result.model.id) }
                if(r.error == null) offers = r.model.map { Mappers.getOfferModel(it, systemInteractor.locale) }
                //changeSortType(SORT_PRICE)
                setOffers()
            }
            viewState.blockInterface(false)
        }
    }

    fun onNewOffer(offer: Offer) {
        offerInteractor.newOffer(offer)
        offers = offers.toMutableList().apply { add(Mappers.getOfferModel(offer, systemInteractor.locale)) }
        setOffers()
    }

    fun onRequestInfoClicked() { router.navigateTo(Screens.Details(transferId)) }

    fun onSelectOfferClicked(offer: OfferModel, isShowingOfferDetails: Boolean) {
        if(isShowingOfferDetails) {
            viewState.showBottomSheetOfferDetails(offer)
            logEvent(OFFER_DETAILS)
        } else {
            logEvent(OFFER_BOOK)
            router.navigateTo(Screens.PaymentSettings(transfer.id, offer.id, transfer.dateRefund))
        }
    }

    fun logEvent(value: String) {
        val map = HashMap<String, Any>()
        map[Analytics.PARAM_KEY_NAME] = value

        analytics.logEvent(Analytics.EVENT_BUTTONS, createStringBundle(Analytics.PARAM_KEY_NAME, value), map)
    }

    fun onCancelRequestClicked() {
        viewState.showAlertCancelRequest()
    }

    fun openLoginView() { login("", "") }

    fun cancelRequest(isCancel: Boolean) {
        if(!isCancel) return
        utils.launchSuspend {
            viewState.blockInterface(true, true)
            val result = utils.asyncAwait { transferInteractor.cancelTransfer(transferId, "") }
            if(result.error != null) {
                Timber.e(result.error!!)
                viewState.setError(result.error!!)
            }
            else router.exit()
            viewState.blockInterface(false)
        }
    }

    fun changeSortType(sortType: String) {
        if(sortCategory == sortType) sortHigherToLower = !sortHigherToLower
        else {
            sortCategory = sortType
            when(sortType) {
                SORT_YEAR   -> sortHigherToLower = true
                SORT_RATING -> sortHigherToLower = true
                SORT_PRICE  -> sortHigherToLower = false
            }
        }
        setOffers()
    }
    
    private fun setOffers() {
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
        val map = HashMap<String, Any>()
        map[PARAM_KEY_FILTER] = value

        analytics.logEvent(EVENT_OFFERS, createStringBundle(PARAM_KEY_FILTER, value), map)
    }
}
