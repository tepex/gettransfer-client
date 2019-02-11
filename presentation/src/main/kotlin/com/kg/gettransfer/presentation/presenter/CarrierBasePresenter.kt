package com.kg.gettransfer.presentation.presenter

import android.support.annotation.CallSuper
import android.util.Log
import com.arellomobile.mvp.InjectViewState
import com.kg.gettransfer.domain.interactor.SystemInteractor
import com.kg.gettransfer.presentation.view.CarrierBaseView
import com.kg.gettransfer.presentation.view.Screens


open class CarrierBasePresenter<CV: CarrierBaseView>: BasePresenter<CV>(), SystemInteractor.CarrierModeListener {

    @CallSuper
    override fun onFirstViewAttach() {
        Log.i("FindService", "1Base")
        systemInteractor.carrierModeListener = this
        systemInteractor.lastMode = Screens.CARRIER_MODE
    }

    @CallSuper
    override fun attachView(view: CV) {
        super.attachView(view)
    }

    @CallSuper
    override fun detachView(view: CV) {
        super.detachView(view)
        with(systemInteractor){
            if (carrierModeListener === this@CarrierBasePresenter)  //check by reference(!) to remove listener, when leave driver mode
                carrierModeListener = null
        }
    }

    override fun startSendingCoordinates() {
        Log.i("FindService", "start")
        viewState.openCoordinateService()
    }

    override fun stopSendingCoordinates() {
        Log.i("FindService", "stop")
        systemInteractor.carrierModeListener = null
        viewState.stopCoordinateService()
    }
}