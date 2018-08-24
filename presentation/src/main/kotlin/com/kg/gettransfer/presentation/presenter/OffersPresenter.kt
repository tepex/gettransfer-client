package com.kg.gettransfer.presentation.presenter

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.kg.gettransfer.presentation.view.OffersView

@InjectViewState
class OffersPresenter: MvpPresenter<OffersView>() {
    fun onBackCommandClick() {
        viewState.finish()
    }
}