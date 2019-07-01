package com.kg.gettransfer.presentation.presenter

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter

import com.kg.gettransfer.R
import com.kg.gettransfer.domain.interactor.SessionInteractor
import com.kg.gettransfer.domain.interactor.SystemInteractor
import com.kg.gettransfer.presentation.view.WebPageView

import org.koin.core.KoinComponent
import org.koin.core.inject

import ru.terrakok.cicerone.Router

@InjectViewState
class WebPagePresenter : MvpPresenter<WebPageView>(), KoinComponent {

    private val systemInteractor: SystemInteractor by inject()
    private val sessionInteractor: SessionInteractor by inject()
    protected val router: Router by inject()

    val termsUrl: String
        get() = "/${systemInteractor.mobileConfigs.termsUrl}"

    val baseUrl: String
        get() = systemInteractor.endpoint.url

    fun onBackCommandClick() {
        router.exit()
    }
}
