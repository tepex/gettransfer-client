package com.kg.gettransfer.presentation.presenter

import android.support.annotation.CallSuper

import com.arellomobile.mvp.InjectViewState

import com.kg.gettransfer.domain.interactor.TransferInteractor

import com.kg.gettransfer.presentation.view.RequestsView

import com.kg.gettransfer.utilities.Analytics

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
        val map = mutableMapOf<String, Any>()
        map[Analytics.PARAM_KEY_FILTER] = value

        analytics.logEvent(Analytics.EVENT_TRANSFERS, createStringBundle(Analytics.PARAM_KEY_FILTER, value), map)
    }
}
