package com.kg.gettransfer.presentation.presenter

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter

import com.kg.gettransfer.presentation.view.WebPageView
import com.kg.gettransfer.sys.presentation.ConfigsManager

import org.koin.core.KoinComponent
import org.koin.core.inject

import ru.terrakok.cicerone.Router

@InjectViewState
class WebPagePresenter : MvpPresenter<WebPageView>(), KoinComponent {

    private val router: Router by inject()

    fun onBackCommandClick() {
        router.exit()
    }

}
