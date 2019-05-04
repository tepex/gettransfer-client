package com.kg.gettransfer.presentation.ui

import android.annotation.SuppressLint
import android.os.Bundle

import android.support.annotation.CallSuper
import android.support.annotation.StringRes

import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter

import android.support.v4.app.FragmentTransaction
import android.support.v7.widget.Toolbar

import com.kg.gettransfer.R
import com.kg.gettransfer.domain.ApiException

import com.kg.gettransfer.presentation.presenter.LoginPresenterNew

import com.kg.gettransfer.presentation.view.LoginViewNew
import com.kg.gettransfer.presentation.view.SmsCodeView

import kotlinx.android.synthetic.main.activity_login_new.*

class LoginActivityNew : BaseActivity(), LoginViewNew {
    @InjectPresenter
    internal lateinit var presenter: LoginPresenterNew

    @ProvidePresenter
    fun createLoginPresenterNew() = LoginPresenterNew()

    override fun getPresenter(): LoginPresenterNew = presenter

    var smsCodeView: SmsCodeView? = null

    companion object {
        const val INVALID_EMAIL     = 1
        const val INVALID_PHONE     = 2
        const val INVALID_PASSWORD  = 3
    }

    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        presenter.emailOrPhone = intent.getStringExtra(LoginViewNew.EXTRA_EMAIL_TO_LOGIN)
        presenter.screenForReturn = intent.getStringExtra(LoginViewNew.EXTRA_SCREEN_FOR_RETURN)
        presenter.transferId = intent.getLongExtra(LoginViewNew.EXTRA_TRANSFER_ID, 0L)
        presenter.offerId = intent.getLongExtra(LoginViewNew.EXTRA_OFFER_ID, 0L)
        presenter.rate = intent.getIntExtra(LoginViewNew.EXTRA_RATE, 0)

        setContentView(R.layout.activity_login_new)
        setToolbar(toolbar as Toolbar, R.string.LNG_MENU_TITLE_LOGIN)

        etEmail.onTextChanged {
            val emailPhone = it.trim()
            presenter.setEmailOrPhone(emailPhone)
            btnLogin.isEnabled = emailPhone.isNotEmpty()
        }

        btnLogin.setOnClickListener { presenter.onContinueClick() }

        etEmail.setText(presenter.emailOrPhone)
    }

    @SuppressLint("CommitTransaction")
    override fun showPasswordFragment(show: Boolean, isPhone: Boolean) {
        with(supportFragmentManager.beginTransaction()) {
            setAnimation(show, this)
            if (show) {
                presenter.passwordFragmentIsShowing = true
                if (isPhone) {
                    add(R.id.passwordFragment, SmsCodeFragment())
                } else {
                    add(R.id.passwordFragment, PasswordFragment())
                }
            }
            else {
                presenter.passwordFragmentIsShowing = false
                supportFragmentManager.fragments.firstOrNull()?.let { smsCodeView = null; remove(it) }
            }
        }?.commit()
    }

    override fun updateTimerResendCode() {
        smsCodeView?.updateTimerResendCode()
    }

    @SuppressLint("PrivateResource")
    private fun setAnimation(opens: Boolean, transaction: FragmentTransaction) =
            transaction.apply {
                val anim = if(opens) R.anim.enter_from_right else R.anim.exit_to_right
                setCustomAnimations(anim, anim)
            }

    override fun setError(finish: Boolean, @StringRes errId: Int, vararg args: String?) {}

    override fun showError(show: Boolean, error: ApiException) {
        if (show) Utils.showError(this, false, when{
            error.isNotFound() -> getString(R.string.LNG_ERROR_ACCOUNT)
            else -> error.details
        })
    }

    override fun showValidationError(show: Boolean, errorType: Int) {
        val errStringRes = when (errorType) {
            INVALID_EMAIL    -> R.string.LNG_ERROR_EMAIL
            INVALID_PHONE    -> R.string.LNG_ERROR_PHONE
            INVALID_PASSWORD -> R.string.LNG_LOGIN_PASSWORD
            else             -> R.string.LNG_BAD_CREDENTIALS_ERROR
        }
        if(show) Utils.showError(this, false, getString(errStringRes))
    }

    override fun onBackPressed() { presenter.onBackCommandClick() }
}