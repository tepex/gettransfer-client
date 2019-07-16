package com.kg.gettransfer.presentation.presenter

import com.arellomobile.mvp.MvpPresenter

import com.kg.gettransfer.presentation.view.MainLoginView
import com.kg.gettransfer.presentation.view.Screens

import org.koin.core.KoinComponent
import org.koin.core.inject

import ru.terrakok.cicerone.Router

class MainLoginPresenter : MvpPresenter<MainLoginView>(), KoinComponent {

    private val router: Router by inject()

    fun showLoginFragment(params: String) {
        router.replaceScreen(Screens.AuthorizationPager(params))
    }

    fun onBack() {
        router.exit()
    }
}
