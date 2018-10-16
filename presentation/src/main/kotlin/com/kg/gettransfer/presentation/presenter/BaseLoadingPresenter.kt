package com.kg.gettransfer.presentation.presenter

import com.kg.gettransfer.domain.CoroutineContexts
import com.kg.gettransfer.domain.interactor.SystemInteractor
import com.kg.gettransfer.presentation.view.BaseLoadingView
import ru.terrakok.cicerone.Router

open class BaseLoadingPresenter<BV: BaseLoadingView>(cc: CoroutineContexts,
                                                     router: Router,
                                                     systemInteractor: SystemInteractor): BasePresenter<BV>(cc, router, systemInteractor)