package com.kg.gettransfer.presentation.ui

import android.content.Context
import android.content.Intent

import android.os.Bundle

import android.support.annotation.CallSuper
import android.support.annotation.StringRes

import android.support.v4.app.Fragment
import android.support.v7.widget.Toolbar

import android.view.View

import com.arellomobile.mvp.MvpAppCompatActivity
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter

import com.kg.gettransfer.R

import com.kg.gettransfer.domain.CoroutineContexts
import com.kg.gettransfer.domain.interactor.ApiInteractor

import com.kg.gettransfer.presentation.Screens
import com.kg.gettransfer.presentation.presenter.LoginPresenter
import com.kg.gettransfer.presentation.view.LoginView

import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.toolbar.view.*

import org.koin.android.ext.android.inject

import ru.terrakok.cicerone.Navigator
import ru.terrakok.cicerone.NavigatorHolder
import ru.terrakok.cicerone.Router
import ru.terrakok.cicerone.android.SupportAppNavigator

class LoginActivity : MvpAppCompatActivity(), LoginView {
    @InjectPresenter
    lateinit var presenter: LoginPresenter

    private val apiInteractor: ApiInteractor by inject()
    private val coroutineContexts: CoroutineContexts by inject()
    private val navigatorHolder: NavigatorHolder by inject()
    private val router: Router by inject()
    
    private val navigator: Navigator = object: SupportAppNavigator(this, Screens.NOT_USED) {
        protected override fun createActivityIntent(context: Context, screenKey: String, data: Any?): Intent? = null
        protected override fun createFragment(screenKey: String, data: Any?): Fragment? = null
    }
	
    @ProvidePresenter
    fun createLoginPresenter(): LoginPresenter = LoginPresenter(coroutineContexts, router, apiInteractor)

    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        setupToolbar()

        btnLogin.isEnabled = false

        etEmail.onTextChanged       { presenter.setEmail(it.trim()) }
        etPassword.onTextChanged    { presenter.setPassword(it.trim()) }
        btnLogin.setOnClickListener { presenter.onLoginClick() }
    }

    @CallSuper
    protected override fun onResume() {
        super.onResume()
        navigatorHolder.setNavigator(navigator)
    }
    
    @CallSuper
    protected override fun onPause() {
        navigatorHolder.removeNavigator()
        super.onPause()
    }
    
    private fun setupToolbar() {
        setSupportActionBar(toolbar as Toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        (toolbar as Toolbar).toolbar_title.setText(R.string.login)
        (toolbar as Toolbar).setNavigationOnClickListener { presenter.onBackCommandClick() }
    }

    override fun enableBtnLogin(enable: Boolean) {
        btnLogin.isEnabled = enable
    }

    override fun blockInterface(block: Boolean) {
        tvLoginError.visibility = View.INVISIBLE
    }
    
    override fun setError(finish: Boolean, @StringRes errId: Int, vararg args: String?) {
        tvLoginError.visibility = View.VISIBLE
    }
    
    override fun onBackPressed() {
        presenter.onBackCommandClick()
    }
}
