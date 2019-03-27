package com.kg.gettransfer.presentation.presenter

import com.arellomobile.mvp.InjectViewState
import com.kg.gettransfer.domain.model.Region
import com.kg.gettransfer.presentation.view.SupportView

@InjectViewState
class SupportPresenter: BasePresenter<SupportView>() {

    fun checkRegion() {
        when(systemInteractor.region) {
            Region.EUROPE  -> viewState.showEuropeanRegion()
            Region.AMERICA -> viewState.showAmericanRegion()
            Region.ASIA    -> viewState.showAsianRegion()
        }
    }

    fun setRegion(region: Region) {
        systemInteractor.region = region
    }
}