package com.kg.gettransfer.presentation.ui

import android.os.Bundle
import androidx.annotation.CallSuper
import androidx.appcompat.widget.Toolbar
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import com.kg.gettransfer.R
import androidx.core.view.isVisible
import com.kg.gettransfer.presentation.presenter.SettingsChangeEmailPresenter
import com.kg.gettransfer.presentation.ui.custom.ActivationCodeView
import com.kg.gettransfer.presentation.view.SettingsChangeEmailView
import kotlinx.android.synthetic.main.activity_settings_change_email.*
import kotlinx.android.synthetic.main.view_input_account_field.view.*

class SettingsChangeEmailActivity : BaseActivity(),
    SettingsChangeEmailView,
    ActivationCodeView.OnActivationCodeListener {

    @InjectPresenter
    internal lateinit var presenter: SettingsChangeEmailPresenter

    @ProvidePresenter
    fun createSettingsChangeEmailPresenter() = SettingsChangeEmailPresenter()

    override fun getPresenter(): SettingsChangeEmailPresenter = presenter

    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings_change_email)

        emailLayout.fieldText.onTextChanged { email ->
            presenter.newEmail = email
            setEnabledBtnChangeEmail(email.isNotEmpty())
        }
        emailLayout.requestInputFieldFocus()

        btnChangeEmail.setOnClickListener { presenter.onChangeEmailClicked() }
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        activationCodeView.listener = this
    }

    override fun onDestroy() {
        super.onDestroy()
        activationCodeView.cancelTimer()
    }

    @Suppress("UnsafeCast")
    override fun setToolbar(email: String?) {
        setToolbar(toolbar as Toolbar, R.string.LNG_CHANGING_EMAIL, subTitle = email)
    }

    override fun showCodeLayout() {
        activationCodeView.isVisible = true
        btnChangeEmail.isVisible = false
        activationCodeView.setFocus()
        emailLayout.disableInputField()
    }

    override fun setTimer(resendDelay: Long) {
        activationCodeView.setTimer(resendDelay)
    }

    override fun setWrongCodeError(details: String) {
        activationCodeView.setWrongCodeError(details)
    }

    override fun onBackPressed() { presenter.onBackCommandClick() }

    private fun setEnabledBtnChangeEmail(enable: Boolean) {
        btnChangeEmail.isEnabled = enable
    }

    override fun onDoneClicked(code: String) {
        presenter.onCodeEntered(code)
    }

    override fun onResendCodeClicked() {
        presenter.onResendCodeClicked()
    }
}
