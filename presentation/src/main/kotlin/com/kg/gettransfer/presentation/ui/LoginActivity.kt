package com.kg.gettransfer.presentation.ui

import android.os.Bundle

import android.support.annotation.CallSuper
import android.support.annotation.StringRes

import android.view.View

import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter

import com.kg.gettransfer.R
import com.kg.gettransfer.domain.model.Offer
import com.kg.gettransfer.extensions.*
import com.kg.gettransfer.presentation.model.OfferModel

import com.kg.gettransfer.presentation.presenter.LoginPresenter

import com.kg.gettransfer.presentation.view.LoginView

import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : BaseActivity(), LoginView {
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
        btnForgotPassword.setOnClickListener { presenter.onPassForgot() }
    }

    override fun enableBtnLogin(enable: Boolean) {
        btnLogin.isEnabled = enable
    }

    override fun blockInterface(block: Boolean, useSpinner: Boolean) {
        if (block) tvLoginError.isVisible = false
    }

    override fun setError(finish: Boolean, @StringRes errId: Int, vararg args: String?) {
        tvLoginError.isVisible = true
    }

    override fun showError(show: Boolean, message: String?) {
        tvLoginError.isVisible = show
        if (show) tvLoginError.text = message ?: getString(R.string.LNG_BAD_CREDENTIALS_ERROR)
    }

    override fun showValidationError(show: Boolean, errorType: Int) {
        tvLoginError.isVisible = show
        if (show) tvLoginError.setText(when (errorType) {
            INVALID_EMAIL    -> R.string.LNG_ERROR_EMAIL
            INVALID_PASSWORD -> R.string.LNG_LOGIN_PASSWORD
            else             -> R.string.LNG_BAD_CREDENTIALS_ERROR
        })
    }

    override fun onBackPressed() { presenter.onBackCommandClick() }

    override fun showOffer(offer: OfferModel) {
    }
}
