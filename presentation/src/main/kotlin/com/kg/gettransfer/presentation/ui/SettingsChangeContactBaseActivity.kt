package com.kg.gettransfer.presentation.ui

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ScrollView
import androidx.annotation.CallSuper
import androidx.core.view.isVisible
import com.kg.gettransfer.presentation.presenter.SettingsChangeContactPresenter
import com.kg.gettransfer.presentation.ui.custom.ActivationCodeView
import com.kg.gettransfer.presentation.view.SettingsChangeContactView
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter

abstract class SettingsChangeContactBaseActivity : BaseActivity(),
    SettingsChangeContactView,
    ActivationCodeView.OnActivationCodeListener {

    private lateinit var activationCodeView: ActivationCodeView
    private lateinit var btnChangeContact: Button
    private lateinit var scrollContent: ScrollView

    @InjectPresenter
    internal lateinit var presenter: SettingsChangeContactPresenter

    @ProvidePresenter
    fun createSettingsChangeContactPresenter() = SettingsChangeContactPresenter(true)

    override fun getPresenter(): SettingsChangeContactPresenter = presenter

    protected fun initFields(codeView: ActivationCodeView, button: Button, scrollView: ScrollView) {
        activationCodeView = codeView
        btnChangeContact = button
        scrollContent = scrollView

        btnChangeContact.setOnClickListener { presenter.onChangeContactClicked() }
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

    override fun showCodeLayout(resendDelay: Long) {
        activationCodeView.isVisible = true
        btnChangeContact.isVisible = false
        activationCodeView.setFocus()

        scrollContent.post { scrollContent.fullScroll(View.FOCUS_DOWN) }

        activationCodeView.setTimer(resendDelay)
    }

    override fun hideCodeLayout() {
        activationCodeView.isVisible = false
        btnChangeContact.isVisible = true

        activationCodeView.cancelTimer()
    }

    override fun setEnabledBtnChangeContact(enable: Boolean) {
        btnChangeContact.isEnabled = enable
    }

    override fun setWrongCodeError(details: String) {
        activationCodeView.setWrongCodeError(details)
    }

    override fun onDoneClicked(code: String) {
        presenter.onCodeEntered(code)
    }

    override fun onResendCodeClicked() {
        presenter.onResendCodeClicked()
    }

    override fun onBackPressed() { presenter.onBackCommandClick() }
}
