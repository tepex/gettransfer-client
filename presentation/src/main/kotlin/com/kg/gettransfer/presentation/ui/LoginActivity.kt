package com.kg.gettransfer.presentation.ui

import android.os.Bundle

import android.support.annotation.CallSuper
import android.support.annotation.StringRes

import android.view.View

import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter

import com.kg.gettransfer.R
import com.kg.gettransfer.extensions.markAsNotImplemented

import com.kg.gettransfer.presentation.presenter.LoginPresenter

import com.kg.gettransfer.presentation.view.LoginView

import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity: BaseActivity(), LoginView {
    @InjectPresenter
    internal lateinit var presenter: LoginPresenter
    
    @ProvidePresenter
    fun createLoginPresenter() = LoginPresenter()

    override fun getPresenter(): LoginPresenter = presenter

    companion object {
        const val INVALID_EMAIL     = 1
        const val INVALID_PASSWORD  = 2
    }

    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        presenter.email = intent.getStringExtra(LoginView.EXTRA_EMAIL_TO_LOGIN)
        presenter.screenForReturn = intent.getStringExtra(LoginView.EXTRA_SCREEN_FOR_RETURN)
        presenter.transferId = intent.getLongExtra(LoginView.EXTRA_TRANSFER_ID, 0L)

        setContentView(R.layout.activity_login)
        
        etEmail.onTextChanged         { presenter.setEmail(it.trim()) }
        etPassword.onTextChanged      { presenter.setPassword(it.trim()) }
        btnLogin.setOnClickListener   { presenter.onLoginClick() }
        homeButton.setOnClickListener { presenter.onHomeClick() }
        
        etEmail.setText(presenter.email)
        btnForgotPassword.markAsNotImplemented()
    }

    override fun enableBtnLogin(enable: Boolean) {
        btnLogin.isEnabled = enable
    }

    override fun blockInterface(block: Boolean, useSpinner: Boolean) {
        if (block) tvLoginError.visibility = View.GONE
    }
    
    override fun setError(finish: Boolean, @StringRes errId: Int, vararg args: String?) {
        tvLoginError.visibility = View.VISIBLE
    }

    override fun showError(show: Boolean, message: String?) {
        tvLoginError.apply {
            if (show) {
                visibility = View.VISIBLE
                text = message?:getString(R.string.LNG_BAD_CREDENTIALS_ERROR)
            } else visibility = View.GONE

        }
    }

    override fun showValidationError(show: Boolean, errorType: Int) {
        tvLoginError.apply {
            if (show){
                visibility = View.VISIBLE
                val res = when (errorType) {
                    INVALID_EMAIL    -> R.string.LNG_ERROR_EMAIL
                    INVALID_PASSWORD -> R.string.LNG_LOGIN_PASSWORD
                    else             -> R.string.LNG_BAD_CREDENTIALS_ERROR
                }
                setText(res)
            } else visibility = View.GONE
        }
    }
    
    override fun onBackPressed() { presenter.onBackCommandClick() }
}
