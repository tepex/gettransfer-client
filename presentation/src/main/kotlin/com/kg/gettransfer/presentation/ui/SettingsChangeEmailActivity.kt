package com.kg.gettransfer.presentation.ui

import android.os.Bundle
import android.view.View

import androidx.annotation.CallSuper
import androidx.core.view.isVisible

import com.kg.gettransfer.R
import com.kg.gettransfer.presentation.model.TitleModel
import com.kg.gettransfer.presentation.presenter.SettingsChangeContactPresenter
import com.kg.gettransfer.presentation.ui.custom.ActivationCodeView
import com.kg.gettransfer.presentation.view.SettingsChangeContactView

import kotlinx.android.synthetic.main.activity_settings_change_email.*
import kotlinx.android.synthetic.main.toolbar.*
import kotlinx.android.synthetic.main.view_input_account_field.view.*

import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter

class SettingsChangeEmailActivity : BaseActivity(),
    SettingsChangeContactView,
    ActivationCodeView.OnActivationCodeListener {

    @InjectPresenter
    internal lateinit var presenter: SettingsChangeContactPresenter

    @ProvidePresenter
    fun createSettingsChangeContactPresenter() = SettingsChangeContactPresenter(true)

    override fun getPresenter(): SettingsChangeContactPresenter = presenter

    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings_change_email)

        emailLayout.fieldText.onTextChanged { email ->
            presenter.newContact = email
            setEnabledBtnChangeEmail(email.isNotEmpty())
        }
        emailLayout.requestInputFieldFocus()

        btnChangeEmail.setOnClickListener { presenter.onChangeContactClicked() }
    }

    @CallSuper
    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        activationCodeView.listener = this
    }

    @CallSuper
    override fun onDestroy() {
        super.onDestroy()
        activationCodeView.cancelTimer()
    }

    override fun setToolbar(text: String?) {
        setToolbar(toolbar, TitleModel.Id(R.string.LNG_CHANGING_EMAIL), text)
    }

    override fun showCodeLayout(resendDelay: Long) {
        activationCodeView.isVisible = true
        btnChangeEmail.isVisible = false
        activationCodeView.setFocus()
        scrollContent.post { scrollContent.fullScroll(View.FOCUS_DOWN) }

        activationCodeView.setTimer(resendDelay)
    }

    override fun hideCodeLayout() {
        activationCodeView.isVisible = false
        btnChangeEmail.isVisible = true

        activationCodeView.cancelTimer()
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
