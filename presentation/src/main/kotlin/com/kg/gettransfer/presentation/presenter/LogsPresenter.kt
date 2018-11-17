package com.kg.gettransfer.presentation.presenter

import com.arellomobile.mvp.InjectViewState

import com.kg.gettransfer.domain.interactor.SystemInteractor

import com.kg.gettransfer.presentation.view.LogsView

import org.koin.standalone.inject

@InjectViewState
class LogsPresenter: BasePresenter<LogsView>() {
    override fun onFirstViewAttach() {
        viewState.setLogs(systemInteractor.logs)
    }

    fun clearLogs() {
        systemInteractor.clearLogs()
        viewState.setLogs(systemInteractor.logs)
    }

    fun onShareClicked() {
        viewState.share(systemInteractor.logsFile)
    }
}
