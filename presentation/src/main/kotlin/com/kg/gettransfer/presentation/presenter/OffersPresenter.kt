package com.kg.gettransfer.presentation.presenter

import android.support.annotation.CallSuper

import com.arellomobile.mvp.InjectViewState

import com.kg.gettransfer.R

import com.kg.gettransfer.domain.ApiException
import com.kg.gettransfer.domain.CoroutineContexts

import com.kg.gettransfer.domain.interactor.SystemInteractor
import com.kg.gettransfer.domain.interactor.TransferInteractor

import com.kg.gettransfer.domain.model.Offer
import com.kg.gettransfer.domain.model.Transfer

import com.kg.gettransfer.presentation.model.Mappers
import com.kg.gettransfer.presentation.model.TransferModel

import com.kg.gettransfer.presentation.view.OffersView

import com.kg.gettransfer.presentation.ui.Utils

import java.text.SimpleDateFormat

import ru.terrakok.cicerone.Router

import timber.log.Timber

@InjectViewState
class OffersPresenter(cc: CoroutineContexts,
                      router: Router,
                      systemInteractor: SystemInteractor,
                      private val transferInteractor: TransferInteractor): BasePresenter<OffersView>(cc, router, systemInteractor) {
    init {
        router.setResultListener(LoginPresenter.RESULT_CODE, { _ -> onFirstViewAttach() })
    }

    @CallSuper
    override fun attachView(view: OffersView) {
        super.attachView(view)
        viewState.setDate(SimpleDateFormat(Utils.DATE_TIME_PATTERN, systemInteractor.locale)
            .format(transferInteractor.transfer!!.dateToLocal))

        viewState.setTransfer(Mappers.getTransferModel(transferInteractor.transfer!!,
                                                       systemInteractor.locale,
                                                       systemInteractor.distanceUnit,
                                                       systemInteractor.getTransportTypes()))
    }

    @CallSuper
    override fun onDestroy() {
        router.removeResultListener(LoginPresenter.RESULT_CODE)
        super.onDestroy()
    }
}
