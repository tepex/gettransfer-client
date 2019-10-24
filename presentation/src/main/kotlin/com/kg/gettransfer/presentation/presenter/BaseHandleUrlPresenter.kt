package com.kg.gettransfer.presentation.presenter

import com.kg.gettransfer.core.presentation.WorkerManager
import com.kg.gettransfer.extensions.createStartChain
import com.kg.gettransfer.presentation.view.BaseHandleUrlView
import com.kg.gettransfer.sys.presentation.ConfigsManager
import kotlinx.coroutines.launch
import org.koin.core.inject
import org.koin.core.parameter.parametersOf

open class BaseHandleUrlPresenter<BV : BaseHandleUrlView> : BasePresenter<BV>() {

    val worker: WorkerManager by inject { parametersOf("BaseHandleUrlPresenter") }
    val configsManager: ConfigsManager by inject()

    override fun onFirstViewAttach() {}

    fun createOrder(fromPlaceId: String?, toPlaceId: String?, promo: String?) = worker.main.launch {
        checkInitialization()
        with(orderInteractor) {
            fromPlaceId?.let { fetchResult(SHOW_ERROR) { updatePoint(false, it) } }
            toPlaceId?.let   { fetchResult(SHOW_ERROR) { updatePoint(true, it) } }
            promo?.let { promoCode = it }
            if (isCanCreateOrder()) {
                router.createStartChain(com.kg.gettransfer.presentation.view.Screens.CreateOrder)
            } else {
                router.newRootScreen(com.kg.gettransfer.presentation.view.Screens.MainPassenger())
            }
        }
    }

    suspend fun checkInitialization() {
        getPreferences().getModel().endpoint?.let { initEndpoint(it) }
        if (!configsManager.configsInitialized) {
            configsManager.coldStart(worker.backgroundScope)
        }
        if (!sessionInteractor.isInitialized) {
            fetchResult(SHOW_ERROR) { sessionInteractor.coldStart() }
        }
    }

    override fun onDestroy() {
        worker.cancel()
        super.onDestroy()
    }
}
