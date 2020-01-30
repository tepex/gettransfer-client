package com.kg.gettransfer.presentation.ui

import android.os.Bundle
import android.view.View

import androidx.annotation.CallSuper
import androidx.core.view.isVisible

import com.kg.gettransfer.R
import com.kg.gettransfer.presentation.model.TitleModel
import com.kg.gettransfer.presentation.presenter.SettingsChangePhonePresenter
import com.kg.gettransfer.presentation.ui.custom.ActivationCodeView
import com.kg.gettransfer.presentation.view.SettingsChangePhoneView

import kotlinx.android.synthetic.main.activity_settings_change_phone.*
import kotlinx.android.synthetic.main.toolbar.*
import kotlinx.android.synthetic.main.view_input_account_field.*

import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter

class SettingsChangePhoneActivity : BaseActivity(),
    SettingsChangePhoneView,
    ActivationCodeView.OnActivationCodeListener {

    @InjectPresenter
    internal lateinit var presenter: SettingsChangePhonePresenter

    @ProvidePresenter
    fun createSettingsChangeEmailPresenter() = SettingsChangePhonePresenter()

    override fun getPresenter(): SettingsChangePhonePresenter = presenter

    private val phone
        get() = phoneLayout.fieldText.text.toString().trim().replace(" ", "")

    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings_change_phone)
        initPhoneTextChangeListeners()
        btnChangePhone.setOnClickListener { presenter.onChangePhoneClicked() }
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

    private fun initPhoneTextChangeListeners() {
        with(phoneLayout) {
            setOnTextChanged {
                presenter.newPhone = phone
                setEnabledBtnChangePhone(phone.isNotEmpty())
            }
            setOnFocusChangeListener()
        }
    }

    override fun setToolbar(phone: String?) {
        setToolbar(toolbar, TitleModel.Id(R.string.LNG_CHANGING_PHONE), phone)
    }

    override fun showCodeLayout(resendDelay: Long) {
        activationCodeView.isVisible = true
        btnChangePhone.isVisible = false
        activationCodeView.setFocus()

        scrollContent.post { scrollContent.fullScroll(View.FOCUS_DOWN) }

        activationCodeView.setTimer(resendDelay)
    }

    override fun hideCodeLayout() {
        activationCodeView.isVisible = false
        btnChangePhone.isVisible = true

        activationCodeView.cancelTimer()
    }

    override fun setWrongCodeError(details: String) {
        activationCodeView.setWrongCodeError(details)
    }

    override fun onBackPressed() { presenter.onBackCommandClick() }

    private fun setEnabledBtnChangePhone(enable: Boolean) {
        btnChangePhone.isEnabled = enable
    }

    override fun onDoneClicked(code: String) {
        presenter.onCodeEntered(code)
    }

    override fun onResendCodeClicked() {
        presenter.onResendCodeClicked()
    }
}
