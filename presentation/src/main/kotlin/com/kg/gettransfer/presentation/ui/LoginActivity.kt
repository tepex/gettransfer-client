package com.kg.gettransfer.presentation.ui

import android.content.Context
import android.content.Intent

import android.os.Bundle

import android.support.annotation.CallSuper
import android.support.annotation.StringRes

import android.view.View

import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter

import com.kg.gettransfer.R
import com.kg.gettransfer.extensions.markAsNotImplemented

import com.kg.gettransfer.presentation.IntentKeys
import com.kg.gettransfer.presentation.Screens
import com.kg.gettransfer.presentation.presenter.LoginPresenter
import com.kg.gettransfer.presentation.view.LoginView

import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity: BaseActivity(), LoginView {
    @InjectPresenter
    internal lateinit var presenter: LoginPresenter
    
    @ProvidePresenter
    fun createLoginPresenter(): LoginPresenter = LoginPresenter(coroutineContexts, router, systemInteractor)

    protected override var navigator = object: BaseNavigator(this) {
        protected override fun createActivityIntent(context: Context, screenKey: String, data: Any?): Intent? {
            val intent = super.createActivityIntent(context, screenKey, data)
            if (intent != null) return intent

            when (screenKey) {
                Screens.REQUESTS -> return Intent(context, RequestsActivity::class.java)
                /*Screens.CARRIER_MODE -> return Intent(context, CarrierTripsActivity::class.java)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)*/
                Screens.CARRIER_MODE -> return Intent(context, WebPageActivity()::class.java)
                        .putExtra(WebPageActivity.SCREEN, WebPageActivity.SCREEN_CARRIER)
                Screens.REG_CARRIER -> return Intent(context, WebPageActivity()::class.java)
                        .putExtra(WebPageActivity.SCREEN, WebPageActivity.SCREEN_REG_CARRIER)
            }
            return null
        }
    }
    
    override fun getPresenter(): LoginPresenter = presenter

    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        
        etEmail.onTextChanged         { presenter.setEmail(it.trim()) }
        etPassword.onTextChanged      { presenter.setPassword(it.trim()) }
        btnLogin.setOnClickListener   { presenter.onLoginClick() }
        homeButton.setOnClickListener { presenter.onHomeClick() }
        btnForgotPassword.markAsNotImplemented()
        presenter.screenForReturn = intent.getStringExtra(IntentKeys.SCREEN_FOR_RETURN)
    }

    override fun enableBtnLogin(enable: Boolean) {
        btnLogin.isEnabled = enable
    }

    override fun blockInterface(block: Boolean, useSpinner: Boolean) {
        tvLoginError.visibility = View.GONE
    }
    
    override fun setError(finish: Boolean, @StringRes errId: Int, vararg args: String?) {
        tvLoginError.visibility = View.VISIBLE
    }

    override fun showError(show: Boolean) {
        tvLoginError.visibility = if (show) View.VISIBLE else View.GONE
    }
    
    override fun onBackPressed() { presenter.onBackCommandClick() }
}
