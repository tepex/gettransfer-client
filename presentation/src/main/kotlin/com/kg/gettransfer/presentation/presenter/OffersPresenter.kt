package com.kg.gettransfer.presentation.presenter

import android.support.annotation.CallSuper

import com.arellomobile.mvp.InjectViewState

import com.kg.gettransfer.domain.ApiException
import com.kg.gettransfer.domain.CoroutineContexts
import com.kg.gettransfer.domain.InternetNotAvailableException

import com.kg.gettransfer.domain.interactor.OfferInteractor
import com.kg.gettransfer.domain.interactor.SystemInteractor
import com.kg.gettransfer.domain.interactor.TransferInteractor

import com.kg.gettransfer.domain.model.Offer

import com.kg.gettransfer.presentation.Screens

import com.kg.gettransfer.presentation.model.Mappers
import com.kg.gettransfer.presentation.model.OfferModel
import com.kg.gettransfer.presentation.model.TransferModel

import com.kg.gettransfer.presentation.ui.Utils
import com.kg.gettransfer.presentation.view.OffersView

import ru.terrakok.cicerone.Router

import timber.log.Timber

@InjectViewState
class OffersPresenter(cc: CoroutineContexts,
                      router: Router,
                      systemInteractor: SystemInteractor,
                      private val transferInteractor: TransferInteractor,
                      private val offerInteractor: OfferInteractor):
    BasePresenter<OffersView>(cc, router, systemInteractor) {

    init {
        router.setResultListener(LoginPresenter.RESULT_CODE, { _ -> onFirstViewAttach() })
    }

    private lateinit var transferModel: TransferModel
    private lateinit var offers: List<OfferModel>

    private var sortCategory: String = SORT_PRICE
    private var sortHigherToLower = false

    companion object {
        @JvmField val EVENT = "offers"

        @JvmField val PARAM_KEY_FILTER  = "filter"
        @JvmField val PARAM_KEY_BUTTON  = "button"

        @JvmField val RATING_UP   = "rating_asc"
        @JvmField val RATING_DOWN = "rating_desc"
        @JvmField val PRICE_UP    = "price_asc"
        @JvmField val PRICE_DOWN  = "price_desc"

        @JvmField val CAR_INFO_CLICKED = "car_info"
        
        @JvmField val YEAH_FILTER_UP   = "year_asc"
        @JvmField val YEAH_FILTER_DOWN = "year_desc"
        
        @JvmField val SORT_YEAR   = "sort_year"
        @JvmField val SORT_RATING = "sort_rating"
        @JvmField val SORT_PRICE  = "sort_price"
    }

    @CallSuper
    override fun attachView(view: OffersView) {
        super.attachView(view)
        utils.launchSuspend {
            viewState.blockInterface(true, true)
            val result = utils.asyncAwait { transferInteractor.getTransfer(transferInteractor.selectedId!!) }
            if(result.error != null) {
                Timber.e(result.error!!)
                if(result.error!!.isNotLoggedIn()) viewState.redirectView()
                else if(result.error!!.code != ApiException.NETWORK_ERROR) viewState.setError(result.error!!)
            } else {
                transferModel = Mappers.getTransferModel(result.model,
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

    @CallSuper
    override fun onDestroy() {
        router.removeResultListener(LoginPresenter.RESULT_CODE)
        super.onDestroy()
    }
    
    fun onNewOffer(offer: Offer) {
        offerInteractor.newOffer(offer)
        offers = offers.toMutableList().apply { add(Mappers.getOfferModel(offer, systemInteractor.locale)) }
        setOffers()
    }

    fun onRequestInfoClicked() {
        router.navigateTo(Screens.DETAILS)
    }

    fun onSelectOfferClicked(offer: OfferModel, isShowingOfferDetails: Boolean) {
        if(isShowingOfferDetails) viewState.showBottomSheetOfferDetails(offer)
        else {
            offerInteractor.selectedOfferId = offer.id
            offerInteractor.transferId = transferInteractor.selectedId
            router.navigateTo(Screens.PAYMENT_SETTINGS, transferModel.refund_date)
        }
    }

    fun onCancelRequestClicked() {
        viewState.showAlertCancelRequest()
    }

    fun openLoginView() {
        login()
    }

    fun cancelRequest(isCancel: Boolean) {
        if(!isCancel) return
        utils.launchSuspend {
            viewState.blockInterface(true, true)
            val result = utils.asyncAwait { transferInteractor.cancelTransfer("") }
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
                SORT_YEAR -> sortHigherToLower = true
                SORT_RATING -> sortHigherToLower = true
                SORT_PRICE -> sortHigherToLower = false
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

    private fun logFilterEvent(value: String) { mFBA.logEvent(EVENT, createSingeBundle(PARAM_KEY_FILTER, value)) }
    private fun logButtonEvent(value: String) { mFBA.logEvent(EVENT, createSingeBundle(PARAM_KEY_BUTTON, value)) }    
}
