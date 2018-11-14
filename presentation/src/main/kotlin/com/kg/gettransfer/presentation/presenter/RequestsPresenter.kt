package com.kg.gettransfer.presentation.presenter

import android.support.annotation.CallSuper

import com.arellomobile.mvp.InjectViewState

import com.kg.gettransfer.domain.CoroutineContexts

import com.kg.gettransfer.domain.interactor.SystemInteractor
import com.kg.gettransfer.domain.interactor.TransferInteractor

import com.kg.gettransfer.presentation.view.RequestsView
import com.yandex.metrica.YandexMetrica

import ru.terrakok.cicerone.Router

@InjectViewState
class RequestsPresenter(cc: CoroutineContexts,
                        router: Router,
                        systemInteractor: SystemInteractor,
                        private val transferInteractor: TransferInteractor): BasePresenter<RequestsView>(cc, router, systemInteractor){

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
        YandexMetrica.reportEvent(EVENT, map)
    }
}
                        