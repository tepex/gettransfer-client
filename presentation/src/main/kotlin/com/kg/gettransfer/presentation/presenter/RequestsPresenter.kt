package com.kg.gettransfer.presentation.presenter

import android.support.annotation.CallSuper

import com.arellomobile.mvp.InjectViewState

import com.kg.gettransfer.domain.interactor.TransferInteractor

import com.kg.gettransfer.presentation.view.RequestsView
import com.kg.gettransfer.utilities.Analytics
import com.kg.gettransfer.utilities.Analytics.Companion.PARAM_KEY_FILTER

import com.yandex.metrica.YandexMetrica

import org.koin.standalone.inject

@InjectViewState
class RequestsPresenter: BasePresenter<RequestsView>() {
    private val transferInteractor: TransferInteractor by inject()

    @CallSuper
    override fun attachView(view: RequestsView) {
        super.attachView(view)
        transferInteractor.deleteAllTransfersList()
    }

    fun logEvent(value: String) {
        val map = HashMap<String, Any>()
        map[PARAM_KEY_FILTER] = value

        analytics.logEvent(Analytics.EVENT_TRANSFERS, createStringBundle(PARAM_KEY_FILTER, value), map)
    }
}
