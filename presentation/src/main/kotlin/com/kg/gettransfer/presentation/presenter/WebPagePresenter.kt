package com.kg.gettransfer.presentation.presenter

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.kg.gettransfer.R
import com.kg.gettransfer.presentation.ui.WebPageActivity
import com.kg.gettransfer.presentation.view.WebPageView

@InjectViewState
class WebPagePresenter(private val screen: String): MvpPresenter<WebPageView>() {

    override fun onFirstViewAttach() {
        when(screen){
            WebPageActivity.SCREEN_LICENSE -> {viewState.initActivity(R.string.license_agreement, R.string.licence_agreement_url)}
            WebPageActivity.SCREEN_REG_CARRIER -> {viewState.initActivity(R.string.become_carrier, R.string.registration_carrier_url)}
        }
    }

    fun onBackCommandClick() {
        viewState.finish()
    }
}