package com.kg.gettransfer.presentation.ui

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

    fun showLoginFragment() {
        router.replaceScreen(Screens.AuthorizationPager(Screens.CLOSE_AFTER_LOGIN, null))
    }

    fun onBack() {
        router.exit()
    }
}
