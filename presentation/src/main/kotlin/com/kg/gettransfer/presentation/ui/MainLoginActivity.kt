package com.kg.gettransfer.presentation.ui

import android.os.Bundle
import android.support.annotation.CallSuper
import com.arellomobile.mvp.MvpAppCompatActivity
import com.arellomobile.mvp.presenter.InjectPresenter
import com.kg.gettransfer.R
import com.kg.gettransfer.domain.ApiException
import com.kg.gettransfer.domain.DatabaseException
import com.kg.gettransfer.presentation.view.LogInView.Companion.EXTRA_EMAIL_TO_LOGIN
import com.kg.gettransfer.presentation.view.LogInView.Companion.EXTRA_NEXT_SCREEN
import com.kg.gettransfer.presentation.view.MainLoginView
import org.koin.android.ext.android.inject
import org.koin.standalone.KoinComponent
import ru.terrakok.cicerone.Navigator
import ru.terrakok.cicerone.NavigatorHolder
import ru.terrakok.cicerone.android.support.SupportAppNavigator

/**
 * Main screen for login and registration.
 */
class MainLoginActivity : MvpAppCompatActivity(), MainLoginView, KoinComponent {

    private val navigatorHolder: NavigatorHolder by inject()

    @InjectPresenter
    internal lateinit var presenter: MainLoginPresenter

    private lateinit var navigator: Navigator

    companion object {
        const val INVALID_EMAIL = 1
        const val INVALID_PHONE = 2
        const val INVALID_PASSWORD = 3
    }

    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_login)
    }

    override fun onResume() {
        super.onResume()

        navigator = SupportAppNavigator(this, supportFragmentManager, R.id.container)
        navigatorHolder.setNavigator(navigator)
        val nextScreen = intent?.getStringExtra(EXTRA_NEXT_SCREEN) ?: ""
        val emailOrPhone = intent.getStringExtra(EXTRA_EMAIL_TO_LOGIN) ?: ""
        presenter.showLoginFragment(nextScreen, emailOrPhone)
    }

    override fun onBackPressed() {
        presenter.onBack()
    }

    override fun blockInterface(block: Boolean, useSpinner: Boolean) {
        //TODO remove BaseView or add code.
    }

    override fun setError(finish: Boolean, errId: Int, vararg args: String?) {
        //TODO remove BaseView or add code.
    }

    override fun setError(e: ApiException) {
        //TODO remove BaseView or add code.
    }

    override fun setError(e: DatabaseException) {
        //TODO remove BaseView or add code.
    }
}