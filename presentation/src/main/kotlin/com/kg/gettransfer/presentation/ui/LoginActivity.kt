package com.kg.gettransfer.presentation.ui

import android.content.Context
import android.content.Intent

import android.os.Bundle

import android.support.annotation.CallSuper
import android.support.annotation.StringRes

import android.support.v7.widget.Toolbar

import android.view.View

import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter

import com.kg.gettransfer.R

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

class LoginActivity: BaseActivity(), LoginView {
    @InjectPresenter
    internal lateinit var presenter: LoginPresenter
    
    @ProvidePresenter
    fun createLoginPresenter(): LoginPresenter = LoginPresenter(coroutineContexts, router, systemInteractor)
    
    protected override var navigator = BaseNavigator(this)
    
    override fun getPresenter(): LoginPresenter = presenter

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
    
    override fun onBackPressed() { presenter.onBackCommandClick() }
}
