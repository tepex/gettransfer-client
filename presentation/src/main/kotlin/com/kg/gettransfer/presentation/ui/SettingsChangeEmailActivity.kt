package com.kg.gettransfer.presentation.ui

import android.os.Bundle

import androidx.annotation.CallSuper

import com.kg.gettransfer.R
import com.kg.gettransfer.presentation.model.TitleModel
import com.kg.gettransfer.presentation.presenter.SettingsChangeContactPresenter

import kotlinx.android.synthetic.main.activity_settings_change_email.*
import kotlinx.android.synthetic.main.toolbar.*
import kotlinx.android.synthetic.main.view_input_account_field.view.*
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter

class SettingsChangeEmailActivity : SettingsChangeContactBaseActivity() {

    @InjectPresenter
    internal lateinit var presenter: SettingsChangeContactPresenter

    @ProvidePresenter
    fun createSettingsChangeContactPresenter() = SettingsChangeContactPresenter(true)

    override fun getPresenter(): SettingsChangeContactPresenter = presenter

    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings_change_email)
        initFields(activationCodeView, btnChangeEmail, scrollContent)
        initEmailTextChangeListeners()
    }

    private fun initEmailTextChangeListeners() {
        with(emailLayout) {
            fieldText.onTextChanged { presenter.newContact = it }
            requestInputFieldFocus()
        }
    }

    override fun setToolbar(text: String?) {
        setToolbar(toolbar, TitleModel.Id(R.string.LNG_CHANGE_EMAIL), text)
    }
}
