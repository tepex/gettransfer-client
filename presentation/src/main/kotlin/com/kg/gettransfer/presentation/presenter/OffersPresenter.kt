package com.kg.gettransfer.presentation.presenter

import android.support.annotation.CallSuper

import com.arellomobile.mvp.InjectViewState

import com.kg.gettransfer.R

import com.kg.gettransfer.domain.ApiException
import com.kg.gettransfer.domain.CoroutineContexts

import com.kg.gettransfer.domain.interactor.OffersInteractor
import com.kg.gettransfer.domain.interactor.SystemInteractor
import com.kg.gettransfer.domain.interactor.TransferInteractor

import com.kg.gettransfer.domain.model.Offer
import com.kg.gettransfer.domain.model.Transfer
import com.kg.gettransfer.presentation.Screens

import com.kg.gettransfer.presentation.model.Mappers
import com.kg.gettransfer.presentation.model.OfferModel
import com.kg.gettransfer.presentation.model.TransferModel

import com.kg.gettransfer.presentation.ui.OffersActivity

import com.kg.gettransfer.presentation.view.OffersView

import com.kg.gettransfer.presentation.ui.Utils

import java.text.SimpleDateFormat

import ru.terrakok.cicerone.Router

import timber.log.Timber

@InjectViewState
class OffersPresenter(cc: CoroutineContexts,
                      router: Router,
                      systemInteractor: SystemInteractor,
                      private val transferInteractor: TransferInteractor,
                      private val offersInteractor: OffersInteractor): BasePresenter<OffersView>(cc, router, systemInteractor) {
    init {
        router.setResultListener(LoginPresenter.RESULT_CODE, { _ -> onFirstViewAttach() })
    }

    private var transfer: Transfer? = null
    private var offers: List<Offer>? = null

    private var sortCategory: String? = null
    private var sortHigherToLower = true

    companion object {
        @JvmField val EVENT = "offers"

        @JvmField val PARAM_KEY_FILTER = "filter"
        @JvmField val PARAM_KEY_BUTTON  = "button"

        @JvmField val YEAH_FILTER_UP = "year_asc"
        @JvmField val YEAH_FILTER_DOWN = "year_desc"
        @JvmField val RATING_UP = "rating_asc"
        @JvmField val RATING_DOWN = "rating_desc"
        @JvmField val PRICE_UP = "price_asc"
        @JvmField val PRICE_DOWN = "price_desc"

        @JvmField val CAR_INFO_CLICKED = "car_info"
    }

    @CallSuper
    override fun attachView(view: OffersView) {
        super.attachView(view)
        /*viewState.setDate(SimpleDateFormat(Utils.DATE_TIME_PATTERN, systemInteractor.locale)
            .format(transferInteractor.transfer!!.dateToLocal))*/
        /*viewState.setDate(Utils.getFormatedDate(systemInteractor.locale, transferInteractor.transfer!!.dateToLocal))

        viewState.setTransfer(Mappers.getTransferModel(transferInteractor.transfer!!,
                                                       systemInteractor.locale,
                                                       systemInteractor.distanceUnit,
                                                       systemInteractor.getTransportTypes()))*/

        utils.launchAsyncTryCatchFinally({
            viewState.blockInterface(true)

            transfer = transferInteractor.transfer
            if(transfer == null) transfer = utils.asyncAwait{ transferInteractor.getTransfer() }
            offers = offersInteractor.getOffers(transfer!!.id).map { Mappers.getOfferModel(it) }

            viewState.setDate(Utils.getFormatedDate(systemInteractor.locale, transfer!!.dateToLocal))

            viewState.setTransfer(Mappers.getTransferModel(transfer!!,
                                                           systemInteractor.locale,
                                                           systemInteractor.distanceUnit,
                                                           systemInteractor.getTransportTypes()))

            //viewState.setOffers(offers!!)
            changeSortType(OffersActivity.SORT_PRICE)
        }, { e -> Timber.e(e)
            viewState.setError(e)
        }, { viewState.blockInterface(false) })
    }

    @CallSuper
    override fun onDestroy() {
        router.removeResultListener(LoginPresenter.RESULT_CODE)
        super.onDestroy()
    }

    fun onRequestInfoClicked() {
        router.navigateTo(Screens.DETAILS)
    }

    fun onSelectOfferClicked(offer: OfferModel) {
        transferInteractor.selectedOffer = offers?.first { it.id == offer.id }
        router.navigateTo(Screens.PAYMENT_SETTINGS)
    }

    fun onCancelRequestClicked() {
        viewState.showAlertCancelRequest()
    }

    fun cancelRequest(isCancel: Boolean) {
        if (isCancel) {
            utils.launchAsyncTryCatchFinally({
                utils.asyncAwait { transferInteractor.cancelTransfer("") }
                router.exit()
            }, { e ->
                Timber.e(e)
                viewState.setError(e)
            }, { viewState.blockInterface(false) })
        }
    }

    fun changeSortType(sortType: String) {
        if (sortCategory == sortType) sortHigherToLower = !sortHigherToLower
        else {
            sortCategory = sortType
            sortHigherToLower = true
        }
        sortOffers()
        viewState.setOffers(Mappers.getOfferModels(offers!!))
        viewState.setSortState(sortCategory!!, sortHigherToLower)
    }

    private fun sortOffers() {
        if (offers != null) {
            var sortType = ""
            offers = when (sortCategory) {
                OffersActivity.SORT_YEAR -> {
                    sortType = if (sortHigherToLower) YEAH_FILTER_DOWN else YEAH_FILTER_UP
                    offers!!.sortedWith(compareBy { it.vehicle.year })
                }
                OffersActivity.SORT_RATING -> {
                    sortType = if (sortHigherToLower) RATING_DOWN else RATING_UP
                    offers!!.sortedWith(compareBy { it.carrier.ratings.average })
                }
                OffersActivity.SORT_PRICE -> {
                    sortType = if (sortHigherToLower) PRICE_DOWN else PRICE_UP
                    offers!!.sortedWith(compareBy { it.price.amount })
                }
                else -> {
                    offers!!
                }
            }
            if (sortHigherToLower) offers = offers!!.reversed()
            logFilterEvent(sortType)
        }
    }

    private fun logFilterEvent(value: String) {
        mFBA.logEvent(EVENT, createSingeBundle(PARAM_KEY_FILTER, value))
    }

    private fun logButtonEvent(value: String) {
        mFBA.logEvent(EVENT, createSingeBundle(PARAM_KEY_BUTTON, value))
    }
}
