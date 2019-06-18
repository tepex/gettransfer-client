package com.kg.gettransfer.presentation.ui

import android.os.Bundle
import android.os.CountDownTimer
import android.support.annotation.CallSuper
import android.support.v4.content.ContextCompat
import android.support.v7.widget.Toolbar
import android.text.InputFilter
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.kg.gettransfer.R
import com.kg.gettransfer.extensions.isVisible
import com.kg.gettransfer.presentation.presenter.SettingsChangeEmailPresenter
import com.kg.gettransfer.presentation.view.SettingsChangeEmailView
import kotlinx.android.synthetic.main.activity_settings_change_email.*

class SettingsChangeEmailActivity: BaseActivity(), SettingsChangeEmailView {

    private var timerBtnResendCode: CountDownTimer? = null

    @InjectPresenter
    internal lateinit var presenter: SettingsChangeEmailPresenter

    @ProvidePresenter
    fun createSettingsChangeEmailPresenter() = SettingsChangeEmailPresenter()

    override fun getPresenter(): SettingsChangeEmailPresenter = presenter

    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings_change_email)

        new_email_field_input.onTextChanged { presenter.setEmail(it) }
        emailCodeView.filters = arrayOf(InputFilter.AllCaps())
        emailCodeView.onTextChanged {
            if (emailCodeView.length() > SettingsChangeEmailPresenter.MAX_CODE_LENGTH) {
                emailCodeView.setText(it.substring(0, SettingsChangeEmailPresenter.MAX_CODE_LENGTH))
            } else {
                presenter.setCode(it, it.length == emailCodeView.itemCount)
            }
        }

        btnResendCode.setOnClickListener { presenter.onResendCodeClicked() }
        btnChangeEmail.setOnClickListener { presenter.onChangeEmailClicked() }
    }

    override fun onDestroy() {
        super.onDestroy()
        timerBtnResendCode?.cancel()
    }

    override fun setToolbar(email: String?) {
        setToolbar(toolbar as Toolbar, R.string.LNG_CHANGING_EMAIL, subTitle = email)
    }

    override fun setEnabledBtnChangeEmail(enable: Boolean) {
        btnChangeEmail.isEnabled = enable
    }

    override fun showCodeLayout() {
        layoutCode.isVisible = true
        emailCodeView.requestFocus()
    }

    override fun setTimer(resendDelay: Long) {
        btnResendCode.isEnabled = false
        val secInMillis = SettingsChangeEmailPresenter.SEC_IN_MILLIS
        timerBtnResendCode = object : CountDownTimer(resendDelay, secInMillis) {
            override fun onTick(millisUntilFinished: Long) {
                btnResendCode.text =
                        getString(R.string.LNG_LOGIN_RESEND_WAIT, (millisUntilFinished / secInMillis).toString())
                                .plus(" ${getString(R.string.LNG_SEC)}")
            }

            override fun onFinish() {
                btnResendCode.isEnabled = true
                btnResendCode.text = getText(R.string.LNG_LOGIN_RESEND_ALLOW)
            }
        }.start()
    }

    override fun onBackPressed() { presenter.onBackCommandClick() }

    override fun setWrongCodeError() {
        emailCodeView.setTextColor(ContextCompat.getColor(this, R.color.color_gtr_red))
    }
}