package com.kg.gettransfer.presentation.presenter

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.kg.gettransfer.presentation.view.CreateOrderView

@InjectViewState
class CreateOrderPresenter: MvpPresenter<CreateOrderView>() {
    fun onBackCommandClick() {
        viewState.finish()
    }
}