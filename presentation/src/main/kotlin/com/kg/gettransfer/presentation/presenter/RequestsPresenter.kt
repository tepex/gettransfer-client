package com.kg.gettransfer.presentation.presenter

import android.support.annotation.CallSuper

import com.arellomobile.mvp.InjectViewState

import com.kg.gettransfer.domain.interactor.TransferInteractor

import com.kg.gettransfer.presentation.view.RequestsView

import com.yandex.metrica.YandexMetrica

import org.koin.standalone.inject

@InjectViewState
class RequestsPresenter: BasePresenter<RequestsView>() {
    private val transferInteractor: TransferInteractor by inject()

    companion object {
        @JvmField val EVENT = "transfers"
        @JvmField val PARAM_KEY = "filter"
    }

    @CallSuper
    override fun attachView(view: RequestsView) {
        super.attachView(view)
        transferInteractor.deleteAllTransfersList()
    }

    fun logEvent(value: String) {
        val map = HashMap<String, Any>()
        map[PARAM_KEY] = value

        mFBA.logEvent(EVENT, createSingeBundle(PARAM_KEY, value))
        eventsLogger.logEvent(EVENT, createSingeBundle(PARAM_KEY, value))
        YandexMetrica.reportEvent(EVENT, map)
    }
}
