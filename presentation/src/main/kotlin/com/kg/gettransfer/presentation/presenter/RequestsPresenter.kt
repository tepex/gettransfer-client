package com.kg.gettransfer.presentation.presenter

import moxy.InjectViewState

import com.kg.gettransfer.presentation.view.RequestsView

import com.kg.gettransfer.utilities.Analytics

@InjectViewState
class RequestsPresenter : BasePresenter<RequestsView>() {

    fun logEvent(value: String) = analytics.logEvent(Analytics.EVENT_TRANSFERS, Analytics.PARAM_KEY_FILTER, value)
}
