package com.kg.gettransfer.presentation.presenter

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.kg.gettransfer.presentation.view.LicenceAgreementView

@InjectViewState
class LicenceAgreementPresenter(): MvpPresenter<LicenceAgreementView>() {
    fun onBackCommandClick() {
        viewState.finish()
    }
}