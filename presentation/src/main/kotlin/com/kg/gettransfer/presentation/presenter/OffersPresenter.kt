package com.kg.gettransfer.presentation.presenter

import android.support.annotation.CallSuper
import android.util.Log

import com.arellomobile.mvp.InjectViewState
import com.kg.gettransfer.domain.ApiException

import com.kg.gettransfer.domain.ApiException
import com.kg.gettransfer.domain.CoroutineContexts
import com.kg.gettransfer.domain.interactor.OfferInteractor
import com.kg.gettransfer.domain.interactor.SystemInteractor
import com.kg.gettransfer.domain.interactor.TransferInteractor

import com.kg.gettransfer.domain.model.Offer
import com.kg.gettransfer.domain.model.OfferListener

import com.kg.gettransfer.presentation.Screens
import com.kg.gettransfer.presentation.model.Mappers
import com.kg.gettransfer.presentation.model.OfferModel
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
    BasePresenter<OffersView>(cc, router, systemInteractor), OfferListener {

    init {
        router.setResultListener(LoginPresenter.RESULT_CODE, { _ -> onFirstViewAttach() })
    }

    private lateinit var offers: List<OfferModel>

    private var sortCategory: String? = null
    private var sortHigherToLower = true

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

        utils.launchAsyncTryCatchFinally({
            viewState.blockInterface(true, true)
            
            val transfer = utils.asyncAwait{ transferInteractor.getTransfer(transferInteractor.selectedId!!) }
            val transferModel = Mappers.getTransferModel(transfer,
                                                         systemInteractor.locale,
                                                         systemInteractor.distanceUnit,
                                                         systemInteractor.transportTypes!!)

            offers = offerInteractor.getOffers(transfer.id).map { Mappers.getOfferModel(it, systemInteractor.locale) }
            viewState.setDate(transferModel.dateTime)
            viewState.setTransfer(transferModel)
            changeSortType(SORT_PRICE)
            offerInteractor.setListener(this@OffersPresenter)
        }, { e -> Timber.e(e)
            if(e is ApiException && e.code == ApiException.NOT_LOGGED_IN)
                viewState.redirectView()
            else viewState.setError(e)
        }, { viewState.blockInterface(false) })

    }

    @CallSuper
    override fun onDestroy() {
        router.removeResultListener(LoginPresenter.RESULT_CODE)
        offerInteractor.removeListener(this)
        super.onDestroy()
    }

    fun onRequestInfoClicked() {
        router.navigateTo(Screens.DETAILS)
    }

    fun onSelectOfferClicked(offer: OfferModel, isShowingOfferDetails: Boolean) {
        if(isShowingOfferDetails) viewState.showBottomSheetOfferDetails(offer)
        else {
            offerInteractor.selectedOfferId = offer.id
            offerInteractor.transferId = transferInteractor.selectedId
            router.navigateTo(Screens.PAYMENT_SETTINGS)
        }
    }

    fun onCancelRequestClicked() {
        viewState.showAlertCancelRequest()
    }

    fun openLoginView(){
        login()
    }

    fun cancelRequest(isCancel: Boolean) {
        if(isCancel) {
            utils.launchAsyncTryCatchFinally({
                viewState.blockInterface(true, true)
                utils.asyncAwait { transferInteractor.cancelTransfer("") }
                router.exit()
            }, { e ->
                Timber.e(e)
                viewState.setError(e)
            }, { viewState.blockInterface(false) })
        }
    }

    fun changeSortType(sortType: String) {
        if(sortCategory == sortType) sortHigherToLower = !sortHigherToLower
        else {
            sortCategory = sortType
            sortHigherToLower = true
        }
        sortOffers()
        viewState.setOffers(offers)
        viewState.setSortState(sortCategory!!, sortHigherToLower)
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
    
    override fun onNewOffer(offer: Offer) {
        viewState.addNewOffer(Mappers.getOfferModel(offer, systemInteractor.locale))
    }
    
    override fun onError(e: ApiException) { Timber.e(e) }
}
