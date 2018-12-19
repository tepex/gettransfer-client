package com.kg.gettransfer.presentation.presenter

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter

import com.kg.gettransfer.R
import com.kg.gettransfer.domain.interactor.SystemInteractor
import com.kg.gettransfer.presentation.view.WebPageView
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject

@InjectViewState
class WebPagePresenter: MvpPresenter<WebPageView>(), KoinComponent {
    private val systemInteractor: SystemInteractor by inject()

    fun onBackCommandClick() {
        viewState.finish()
    }

    fun getLang() = systemInteractor.locale.language
}
