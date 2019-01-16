package com.kg.gettransfer.presentation.presenter

import android.support.annotation.CallSuper

import com.arellomobile.mvp.InjectViewState

import com.kg.gettransfer.presentation.view.RequestsView

import com.kg.gettransfer.utilities.Analytics

@InjectViewState
class RequestsPresenter : BasePresenter<RequestsView>() {

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
