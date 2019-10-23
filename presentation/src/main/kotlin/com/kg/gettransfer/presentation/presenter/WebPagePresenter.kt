package com.kg.gettransfer.presentation.presenter

import moxy.InjectViewState
import moxy.MvpPresenter

import com.kg.gettransfer.presentation.view.WebPageView
import com.kg.gettransfer.sys.presentation.ConfigsManager

import org.koin.core.KoinComponent
import org.koin.core.inject

import ru.terrakok.cicerone.Router

@InjectViewState
class WebPagePresenter : MvpPresenter<WebPageView>(), KoinComponent {

    private val configsManager: ConfigsManager by inject()
    private val router: Router by inject()

    val termsUrl = "/${configsManager.mobile.termsUrl}"

    fun onBackCommandClick() {
        router.exit()
    }

}
