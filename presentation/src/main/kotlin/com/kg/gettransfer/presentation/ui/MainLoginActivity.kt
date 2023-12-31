package com.kg.gettransfer.presentation.ui

import android.content.Context
import android.os.Build
import android.os.Bundle

import androidx.annotation.CallSuper

import moxy.MvpAppCompatActivity
import moxy.presenter.InjectPresenter

import com.kg.gettransfer.R

import com.kg.gettransfer.domain.ApiException
import com.kg.gettransfer.domain.DatabaseException
import com.kg.gettransfer.domain.interactor.SessionInteractor
import com.kg.gettransfer.extensions.setStatusBarColor

import com.kg.gettransfer.presentation.presenter.MainLoginPresenter

import com.kg.gettransfer.presentation.view.LogInView
import com.kg.gettransfer.presentation.view.MainLoginView

import com.kg.gettransfer.utilities.LocaleManager

import org.koin.android.ext.android.inject
import org.koin.core.KoinComponent

import ru.terrakok.cicerone.Navigator
import ru.terrakok.cicerone.NavigatorHolder
import ru.terrakok.cicerone.android.support.SupportAppNavigator

/**
 * Main screen for login and registration.
 */
class MainLoginActivity : MvpAppCompatActivity(), MainLoginView, KoinComponent {

    private val navigatorHolder: NavigatorHolder by inject()
    private val localeManager: LocaleManager by inject()
    private val sessionInteractor: SessionInteractor by inject()

    @InjectPresenter
    internal lateinit var presenter: MainLoginPresenter

    private var navigator: Navigator = SupportAppNavigator(this, supportFragmentManager, R.id.container)

    @CallSuper
    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(localeManager.updateResources(newBase, sessionInteractor.locale))
    }

    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.N_MR1) {
            localeManager.updateResources(this, sessionInteractor.locale)
        }
        setContentView(R.layout.activity_user_login)
        setStatusBarColor(R.color.colorWhite)
        presenter.showLoginFragment(intent.getStringExtra(LogInView.EXTRA_PARAMS))
    }

    override fun onResumeFragments() {
        super.onResumeFragments()
        navigatorHolder.setNavigator(navigator)
    }

    override fun onPause() {
        navigatorHolder.removeNavigator()
        super.onPause()
    }

    override fun onBackPressed() {
        presenter.onBack()
    }

    @CallSuper
    override fun onDestroy() {
        super.onDestroy()
//        AppWatcher.objectWatcher.watch(this)
    }

    @Suppress("EmptyFunctionBlock")
    override fun blockInterface(block: Boolean, useSpinner: Boolean) {
        // TODO remove BaseView or add code.
    }

    @Suppress("EmptyFunctionBlock")
    override fun setError(finish: Boolean, errId: Int, vararg args: String?) {
        // TODO remove BaseView or add code.
    }

    @Suppress("EmptyFunctionBlock")
    override fun setError(e: ApiException) {
        // TODO remove BaseView or add code.
    }

    @Suppress("EmptyFunctionBlock")
    override fun setError(e: DatabaseException) {
        // TODO remove BaseView or add code.
    }

    override fun setTransferNotFoundError(transferId: Long, dismissCallBack: (() -> Unit)?) {
        TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
    }

    companion object {
        const val INVALID_EMAIL = 1
        const val INVALID_PHONE = 2
        const val INVALID_PASSWORD = 3
    }
}
