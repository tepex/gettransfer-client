package com.kg.gettransfer.presentation.presenter

import com.arellomobile.mvp.MvpPresenter

import com.kg.gettransfer.R
import com.kg.gettransfer.presentation.view.MainLoginView
import com.kg.gettransfer.presentation.view.Screens
import com.kg.gettransfer.presentation.view.Screens.CLOSE_AFTER_LOGIN

import org.koin.standalone.KoinComponent
import org.koin.standalone.inject

import ru.terrakok.cicerone.Router
import ru.terrakok.cicerone.android.support.SupportAppNavigator

class MainLoginPresenter : MvpPresenter<MainLoginView>(), KoinComponent {

    private val router: Router by inject()

    fun showLoginFragment(nextScreen: String, emailOrPhone: String) {
        router.replaceScreen(Screens.AuthorizationPager(nextScreen, emailOrPhone))
    }

    fun onBack() {
        router.exit()
    }
}
