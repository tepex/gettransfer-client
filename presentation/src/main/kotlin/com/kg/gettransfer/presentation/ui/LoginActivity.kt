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

import com.kg.gettransfer.presentation.presenter.LoginPresenter

import com.kg.gettransfer.presentation.view.LoginView
import com.kg.gettransfer.presentation.view.SmsCodeView

import kotlinx.android.synthetic.main.activity_login_new.*
import kotlinx.android.synthetic.main.view_input_password.*
import java.lang.UnsupportedOperationException

class LoginActivity : BaseActivity(), LoginView {
    @InjectPresenter
    internal lateinit var presenter: LoginPresenter

    @ProvidePresenter
    fun createLoginPresenter() = LoginPresenter()

    override fun getPresenter(): LoginPresenter = presenter

    var smsCodeView: SmsCodeView? = null

    companion object {
        const val INVALID_EMAIL     = 1
        const val INVALID_PHONE     = 2
        const val INVALID_PASSWORD  = 3
    }

    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        presenter.emailOrPhone = intent.getStringExtra(LoginView.EXTRA_EMAIL_TO_LOGIN)
        presenter.nextScreen = intent.getStringExtra(LoginView.EXTRA_NEXT_SCREEN)
        presenter.transferId = intent.getLongExtra(LoginView.EXTRA_TRANSFER_ID, 0L)
        presenter.offerId = intent.getLongExtra(LoginView.EXTRA_OFFER_ID, 0L)
        presenter.rate = intent.getIntExtra(LoginView.EXTRA_RATE, 0)

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

    override fun setEmail(login: String) {
        etEmail.setText(login)
        etPassword.requestFocus()
    }

    @SuppressLint("CommitTransaction")
    override fun showPasswordFragment(show: Boolean, showingView: Int) {
        with(supportFragmentManager.beginTransaction()) {
            setAnimation(show, this)
            if (show) {
                layoutLogin.isVisible = false
                presenter.showingFragment = showingView
                replace(R.id.passwordFragment, when (showingView) {
                    LoginPresenter.PASSWORD_VIEW -> PasswordFragment()
                    LoginPresenter.SMS_CODE_VIEW -> SmsCodeFragment()
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
            if (error.isNotFound()) {
                presenter.showNoAccountError()
                return
            }
            smsCodeView?.let {
                showErrorText(true, error.details)
                return
            }
            Utils.showError(this, false, error.details)
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

    override fun showErrorText(show: Boolean, text: String?) {
        smsCodeView?.showErrorText(show, text)
    }

    override fun onBackPressed() { presenter.onBackCommandClick() }
}