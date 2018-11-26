package com.kg.gettransfer.presentation.presenter

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter

import com.kg.gettransfer.R
import com.kg.gettransfer.presentation.view.WebPageView

@InjectViewState
class WebPagePresenter: MvpPresenter<WebPageView>() {
    fun onBackCommandClick() {
        viewState.finish()
    }
}
