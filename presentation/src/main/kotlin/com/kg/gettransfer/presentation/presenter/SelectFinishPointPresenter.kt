package com.kg.gettransfer.presentation.presenter

import com.arellomobile.mvp.InjectViewState
import com.kg.gettransfer.domain.CoroutineContexts
import com.kg.gettransfer.domain.interactor.RouteInteractor
import com.kg.gettransfer.domain.interactor.SystemInteractor
import com.kg.gettransfer.presentation.view.SelectFinishPointView
import ru.terrakok.cicerone.Router

@InjectViewState
class SelectFinishPointPresenter(cc: CoroutineContexts,
                                 router: Router,
                                 systemInteractor: SystemInteractor,
                                 private val routeInteractor: RouteInteractor): BasePresenter<SelectFinishPointView>(cc, router, systemInteractor){
}