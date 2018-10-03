package com.kg.gettransfer.presentation.presenter

import com.arellomobile.mvp.InjectViewState

import com.kg.gettransfer.domain.CoroutineContexts

import com.kg.gettransfer.domain.interactor.SystemInteractor

import com.kg.gettransfer.presentation.view.RequestsView

import ru.terrakok.cicerone.Router

@InjectViewState
class RequestsPresenter(cc: CoroutineContexts,
                        router: Router,
                        systemInteractor: SystemInteractor): BasePresenter<RequestsView>(cc, router, systemInteractor){

    companion object {
        @JvmField val EVENT = "transfers"

        @JvmField val PARAM_KEY = "filter"

    }
    fun logEvent(value: String){
        mFBA.logEvent(EVENT, createSingeBundle(PARAM_KEY, value))
    }
}
                        