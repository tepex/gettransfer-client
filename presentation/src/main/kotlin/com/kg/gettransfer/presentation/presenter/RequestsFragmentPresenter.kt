package com.kg.gettransfer.presentation.presenter

import android.support.annotation.CallSuper

import com.arellomobile.mvp.InjectViewState

import com.kg.gettransfer.R

import com.kg.gettransfer.domain.interactor.TransferInteractor

import com.kg.gettransfer.domain.model.Transfer.Status

import com.kg.gettransfer.presentation.mapper.TransferMapper

import com.kg.gettransfer.presentation.model.TransferModel

import com.kg.gettransfer.presentation.view.RequestsFragmentView
import com.kg.gettransfer.presentation.view.Screens

import org.koin.standalone.inject

import timber.log.Timber

@InjectViewState
class RequestsFragmentPresenter : BasePresenter<RequestsFragmentView>() {
    private val transferInteractor: TransferInteractor by inject()

    private val transferMapper: TransferMapper by inject()
    //private val categoryName: String by inject()

    private var transfers: List<TransferModel>? = null

    @CallSuper
    override fun attachView(view: RequestsFragmentView) {
        super.attachView(view)
        getTransfers()
        transfers?.let { viewState.setRequests(it) }
    }

    fun getTransfers() {
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
            if (result.error != null) viewState.setError(result.error!!)
            else {
                transfers = result.model.map { transferMapper.toView(it) }
                viewState.setRequests(transfers!!)
            }
            viewState.blockInterface(false)
        }
    }

    fun openTransferDetails(id: Long, status: Status) {
        Timber.d("Open Transfer details. id: $id")
        when (status) {
            Status.NEW -> router.navigateTo(Screens.Offers(id))
            else -> router.navigateTo(Screens.Details(id))
        }
    }
}
