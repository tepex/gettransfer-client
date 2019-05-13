package com.kg.gettransfer.presentation.ui

import android.annotation.SuppressLint
import android.os.Bundle

import android.support.annotation.CallSuper
import android.support.annotation.StringRes

import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter

import android.support.v4.app.FragmentTransaction

import com.kg.gettransfer.R
import com.kg.gettransfer.domain.ApiException
import com.kg.gettransfer.extensions.isVisible

import com.kg.gettransfer.presentation.presenter.LoginPresenterNew

import com.kg.gettransfer.presentation.view.LoginViewNew
import com.kg.gettransfer.presentation.view.SmsCodeView

import kotlinx.android.synthetic.main.activity_login_new.*
import kotlinx.android.synthetic.main.view_input_password.*
import java.lang.UnsupportedOperationException

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

        initTextChangeListeners()
        initClickListeners()

        etEmail.setText(presenter.emailOrPhone)
    }

    private fun initClickListeners() {
        ivBtnBack.setOnClickListener { presenter.onBackCommandClick() }
        btnLogin.setOnClickListener { presenter.onLoginClick() }
        btn_requestCode.setOnClickListener { presenter.sendVerificationCode() }
    }

    private fun initTextChangeListeners() {
        etEmail.onTextChanged {
            val emailPhone = it.trim()
            presenter.setEmailOrPhone(emailPhone)
            btnLogin.isEnabled = emailPhone.isNotEmpty() && etPassword.text?.isNotEmpty() ?: false
        }
        etPassword.onTextChanged {
            presenter.setPassword(it)
            btnLogin.isEnabled = etEmail.text?.isNotEmpty() ?: false && it.isNotEmpty()
        }

    }

    @SuppressLint("CommitTransaction")
    override fun showPasswordFragment(show: Boolean, showingView: Int) {
        with(supportFragmentManager.beginTransaction()) {
            setAnimation(show, this)
            if (show) {
                layoutLogin.isVisible = false
                presenter.showingFragment = showingView
                replace(R.id.passwordFragment, when (showingView) {
                    LoginPresenterNew.PASSWORD_VIEW -> PasswordFragment()
                    LoginPresenterNew.SMS_CODE_VIEW -> SmsCodeFragment()
                    else -> throw UnsupportedOperationException()
                })
            } else {
                layoutLogin.isVisible = true
                presenter.showingFragment = null
                supportFragmentManager.fragments.firstOrNull()?.let { smsCodeView = null; remove(it) }
            }
        }?.commit()
    }

    @SuppressLint("PrivateResource")
    private fun setAnimation(opens: Boolean, transaction: FragmentTransaction) =
            transaction.apply {
                val anim = if(opens) R.anim.enter_from_right else R.anim.exit_to_right
                setCustomAnimations(anim, anim)
            }

    override fun setError(finish: Boolean, @StringRes errId: Int, vararg args: String?) {
        Utils.showError(this, false, getString(errId, *args))
    }

    override fun showError(show: Boolean, error: ApiException) {
        if (show) {
            smsCodeView?.let {
                showErrorText(true)
                return
            }
            Utils.showError(this, false, when {
                error.isNotFound() -> getString(R.string.LNG_ERROR_ACCOUNT)
                else -> error.details
            })
        }
    }

    override fun showLoginInfo(title: Int, info: Int) {
        Utils.showLoginDialog(this, message = getString(info), title = getString(title))
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

    override fun showChangePasswordDialog() {
        Utils.showAlertSetNewPassword(this) { presenter.openPreviousScreen(it) }
    }

    override fun updateTimerResendCode() {
        smsCodeView?.updateTimerResendCode()
    }

    override fun showErrorText(show: Boolean) {
        smsCodeView?.showErrorText(show)
    }

    override fun onBackPressed() { presenter.onBackCommandClick() }
}