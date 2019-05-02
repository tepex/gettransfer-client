package com.kg.gettransfer.presentation.ui

import android.annotation.SuppressLint
import android.os.Bundle

import android.support.annotation.CallSuper
import android.support.annotation.StringRes

import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter

import android.support.v4.app.FragmentTransaction

import com.kg.gettransfer.R
import com.kg.gettransfer.extensions.*

import com.kg.gettransfer.presentation.presenter.LoginPresenterNew

import com.kg.gettransfer.presentation.view.LoginViewNew

import kotlinx.android.synthetic.main.activity_login_new.*

class LoginActivityNew : BaseActivity(), LoginViewNew {
    @InjectPresenter
    internal lateinit var presenter: LoginPresenterNew

    @ProvidePresenter
    fun createLoginPresenterNew() = LoginPresenterNew()

    override fun getPresenter(): LoginPresenterNew = presenter

    companion object {
        const val INVALID_EMAIL     = 1
        const val INVALID_PASSWORD  = 2
    }

    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        presenter.email = intent.getStringExtra(LoginViewNew.EXTRA_EMAIL_TO_LOGIN)
        presenter.screenForReturn = intent.getStringExtra(LoginViewNew.EXTRA_SCREEN_FOR_RETURN)
        presenter.transferId = intent.getLongExtra(LoginViewNew.EXTRA_TRANSFER_ID, 0L)
        presenter.offerId = intent.getLongExtra(LoginViewNew.EXTRA_OFFER_ID, 0L)
        presenter.rate = intent.getIntExtra(LoginViewNew.EXTRA_RATE, 0)

        setContentView(R.layout.activity_login_new)

        etEmail.onTextChanged {
            val emailPhone = it.trim()
            presenter.setEmail(emailPhone)
            btnLogin.isEnabled = emailPhone.isNotEmpty()
        }

        btnLogin.setOnClickListener   { presenter.onLoginClick() }
        ivBack.setOnClickListener { presenter.onBackClick() }

        etEmail.setText(presenter.email)
    }

    @SuppressLint("CommitTransaction")
    override fun showPasswordFragment(show: Boolean) {
        with(supportFragmentManager.beginTransaction()) {
            setAnimation(show, this)
            if (show) {
                presenter.passwordFragmentIsShowing = true
                add(R.id.passwordFragment, SelectCurrencyFragment())
            }
            else {
                presenter.passwordFragmentIsShowing = false
                supportFragmentManager.fragments.firstOrNull()?.let { remove(it) }
            }
        }?.commit()
    }

    @SuppressLint("PrivateResource")
    private fun setAnimation(opens: Boolean, transaction: FragmentTransaction) =
            transaction.apply {
                val anim = if(opens) R.anim.enter_from_right else R.anim.exit_to_right
                setCustomAnimations(anim, anim)
            }

    override fun blockInterface(block: Boolean, useSpinner: Boolean) {
        super.blockInterface(block, useSpinner)
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
}