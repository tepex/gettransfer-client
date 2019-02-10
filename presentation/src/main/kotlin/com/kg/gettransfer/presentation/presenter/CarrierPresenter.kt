package com.kg.gettransfer.presentation.presenter

import com.kg.gettransfer.domain.interactor.SystemInteractor
import com.kg.gettransfer.presentation.view.BaseView

open class CarrierPresenter<BV : BaseView> : BasePresenter<BV>(), SystemInteractor.CarrierModeListener {
    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        with(systemInteractor){ if (carrierModeListener == null) carrierModeListener = this@CarrierPresenter }

    }

    override fun detachView(view: BV) {
        super.detachView(view)
        systemInteractor.carrierModeListener = null
    }

    override fun startSendingCoordinates() {
 //       transferInteractor.sendOwnCoordinates()
    }

    override fun stopSendingCoordinates() {
        with(systemInteractor){
            if (carrierModeListener === this@CarrierPresenter) carrierModeListener = null
        }
    }
}