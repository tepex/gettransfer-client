package com.kg.gettransfer.presentation.presenter

import android.support.annotation.CallSuper

import com.arellomobile.mvp.InjectViewState

import com.kg.gettransfer.domain.model.Offer

import com.kg.gettransfer.domain.model.sortDescendant
import com.kg.gettransfer.domain.model.Transfer
import com.kg.gettransfer.domain.model.Transfer.Companion.filterActive
import com.kg.gettransfer.domain.model.Transfer.Companion.filterCompleted

import com.kg.gettransfer.presentation.model.OfferModel
import com.kg.gettransfer.presentation.model.TransferModel

import com.kg.gettransfer.presentation.view.RequestsFragmentView
import com.kg.gettransfer.presentation.view.RequestsView
import com.kg.gettransfer.presentation.view.RequestsView.TransferTypeAnnotation.Companion.TRANSFER_ACTIVE
import com.kg.gettransfer.presentation.view.RequestsView.TransferTypeAnnotation.Companion.TRANSFER_ARCHIVE
import com.kg.gettransfer.presentation.view.Screens

import timber.log.Timber

@InjectViewState
class RequestsFragmentPresenter(@RequestsView.TransferTypeAnnotation
                                tt: Int) : BasePresenter<RequestsFragmentView>() {
    private val transferMapper: TransferMapper by inject()

    @RequestsView.TransferTypeAnnotation
    var transferType = tt

    private lateinit var transferIds: List<Long>

    @CallSuper
    override fun attachView(view: RequestsFragmentView) {
        super.attachView(view)
        getTransfers()
        transferIds = systemInteractor.transferIds
    }

    private fun getTransfers() {
        utils.launchSuspend {
            viewState.blockInterface(true)

            val result = when(transferType) {
                TRANSFER_ACTIVE    -> fetchData { transferInteractor.getTransfersActive() }
                TRANSFER_ARCHIVE -> fetchData { transferInteractor.getTransfersArchive() }
                else                                -> fetchData { transferInteractor.getTransfersActive() }
            }

            result?.let { showTransfers(it) }
            viewState.blockInterface(false)
        }
    }

    private fun showTransfers(transfers: List<Transfer>) {

        val filteredSorted = transfers.sortedByDescending {
            it.dateToLocal
        }

        viewState.setRequests(filteredSorted.map { transferMapper.toView(it) })
        viewState.setCountEvents(transferIds)
    }

    fun openTransferDetails(id: Long, status: Transfer.Status, paidPercentage: Int) {
        Timber.d("Open Transfer details. id: $id")
        if (status == Transfer.Status.NEW && paidPercentage == 0) {
            router.navigateTo(Screens.Offers(id))
        } else {
            router.navigateTo(Screens.Details(id))
        }
    }

    override fun onNewOffer(offer: Offer): OfferModel {
        utils.launchSuspend{ viewState.setCountEvents(transferIds) }
        return super.onNewOffer(offer)
    }
}
