package com.kg.gettransfer.presentation.presenter

import com.kg.gettransfer.core.presentation.WorkerManager
import com.kg.gettransfer.extensions.createStartChain
import com.kg.gettransfer.presentation.view.BaseView
import com.kg.gettransfer.presentation.view.Screens
import kotlinx.coroutines.launch
import org.koin.core.inject
import org.koin.core.parameter.parametersOf

open class BaseHandleUrlPresenter<BV : BaseView> : BasePresenter<BV>() {

    val worker: WorkerManager by inject { parametersOf("BaseHandleUrlPresenter") }

    override fun onFirstViewAttach() {}

    fun createOrder(fromPlaceId: String?, toPlaceId: String?, promo: String?) = worker.main.launch {
        checkInitialization()
        with(orderInteractor) {
            fromPlaceId?.let { fetchResult(SHOW_ERROR) { updatePoint(false, it) } }
            toPlaceId?.let   { fetchResult(SHOW_ERROR) { updatePoint(true, it) } }
            promo?.let { promoCode = it }
            if (isCanCreateOrder()) {
                router.createStartChain(Screens.CreateOrder)
            } else {
                router.newRootScreen(Screens.MainPassenger())
            }
        }
    }

    suspend fun checkInitialization() {
        configsManager.coldStart(worker.backgroundScope)
        if (!sessionInteractor.isInitialized) {
            fetchResult(SHOW_ERROR) { sessionInteractor.coldStart() }
        }
    }

    override fun onDestroy() {
        worker.cancel()
        super.onDestroy()
    }
}
