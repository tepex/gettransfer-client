package com.kg.gettransfer.presentation.presenter

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter

import com.kg.gettransfer.R
import com.kg.gettransfer.domain.interactor.SystemInteractor
import com.kg.gettransfer.presentation.view.WebPageView

import com.kg.gettransfer.sys.domain.GetTermsUrlInteractor

import org.koin.core.KoinComponent
import org.koin.core.inject

import ru.terrakok.cicerone.Router

@InjectViewState
class WebPagePresenter : MvpPresenter<WebPageView>(), KoinComponent {

    private val getTermsUrl: GetTermsUrlInteractor by inject()

    private val systemInteractor: SystemInteractor by inject()
    protected val router: Router by inject()

    val termsUrl = "/${getTermsUrl()}"

    val baseUrl: String
        get() = systemInteractor.endpoint.url

    fun onBackCommandClick() {
        router.exit()
    }
}
