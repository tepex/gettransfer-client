package com.kg.gettransfer.presentation.presenter

import android.support.annotation.CallSuper

import com.arellomobile.mvp.InjectViewState

import com.kg.gettransfer.R

import com.kg.gettransfer.domain.model.Offer

import com.kg.gettransfer.domain.model.sortDescendant
import com.kg.gettransfer.domain.model.Transfer
import com.kg.gettransfer.domain.model.Transfer.Companion.filterActive
import com.kg.gettransfer.domain.model.Transfer.Companion.filterCompleted

import com.kg.gettransfer.presentation.mapper.TransferMapper
import com.kg.gettransfer.presentation.model.OfferModel

import com.kg.gettransfer.presentation.model.TransferModel

import com.kg.gettransfer.presentation.view.RequestsFragmentView
import com.kg.gettransfer.presentation.view.RequestsView
import com.kg.gettransfer.presentation.view.Screens

import org.koin.standalone.inject

import timber.log.Timber

@InjectViewState
class RequestsFragmentPresenter : BasePresenter<RequestsFragmentView>() {
    private val transferMapper: TransferMapper by inject()

    lateinit var categoryName: String

    private var transfers: List<TransferModel>? = null
    private lateinit var transferIds: List<Long>

    @CallSuper
    override fun attachView(view: RequestsFragmentView) {
        super.attachView(view)
        getTransfers()
        transfers?.let { viewState.setRequests(it) }
        transferIds = systemInteractor.transferIds
    }

    private fun getTransfers() {
        utils.launchSuspend {
            viewState.blockInterface(true)
            /*
            val result = when(categoryName) {
                RequestsActivity.CATEGORY_ACTIVE    -> utils.asyncAwait { transferInteractor.getActiveTransfers() }
                RequestsActivity.CATEGORY_COMPLETED -> utils.asyncAwait { transferInteractor.getCompletedTransfers() }
                else                                -> utils.asyncAwait { transferInteractor.getArchivedTransfers() }
            }
            */
            val result = utils.asyncAwait { transferInteractor.getAllTransfers() }
            if (result.error != null && !result.fromCache) viewState.setError(result.error!!) else showTransfers(result.model)
            viewState.blockInterface(false)
        }
    }

    private fun showTransfers(transfers: List<Transfer>) {
        val filtered = when (categoryName) {
            RequestsView.CATEGORY_ACTIVE    -> transfers.filterActive()
            RequestsView.CATEGORY_COMPLETED -> transfers.filterCompleted()
            else                            -> transfers
        }
        viewState.setRequests(filtered.sortDescendant().map { transferMapper.toView(it) })
        viewState.setCountEvents(transferIds)
    }

    fun openTransferDetails(id: Long, status: Transfer.Status) {
        Timber.d("Open Transfer details. id: $id")
        when (status) {
            Transfer.Status.NEW -> router.navigateTo(Screens.Offers(id))
            else                -> router.navigateTo(Screens.Details(id))
        }
    }

    override fun onNewOffer(offer: Offer): OfferModel {
        utils.launchSuspend{ viewState.setCountEvents(transferIds) }
        return super.onNewOffer(offer)
    }
}
