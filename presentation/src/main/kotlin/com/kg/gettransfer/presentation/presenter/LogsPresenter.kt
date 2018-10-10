package com.kg.gettransfer.presentation.presenter

import com.arellomobile.mvp.InjectViewState
import com.kg.gettransfer.domain.CoroutineContexts
import com.kg.gettransfer.domain.interactor.SystemInteractor
import com.kg.gettransfer.presentation.view.LogsView
import ru.terrakok.cicerone.Router

@InjectViewState
class LogsPresenter(cc: CoroutineContexts,
                    router: Router,
                    systemInteractor: SystemInteractor): BasePresenter<LogsView>(cc, router, systemInteractor) {

    override fun onFirstViewAttach() {
        viewState.setLogs(systemInteractor.getLogs())
    }

    fun clearLogs(){
        systemInteractor.clearLogs()
        viewState.setLogs(systemInteractor.getLogs())
    }

    fun onShareClicked(){
        viewState.share(systemInteractor.getLogsFile())
    }
}