package com.kg.gettransfer.presentation.ui

import android.os.Bundle

import androidx.annotation.CallSuper

import com.kg.gettransfer.R
import com.kg.gettransfer.presentation.model.TitleModel

import kotlinx.android.synthetic.main.activity_settings_change_email.*
import kotlinx.android.synthetic.main.toolbar.*
import kotlinx.android.synthetic.main.view_input_account_field.view.*

class SettingsChangeEmailActivity : SettingsChangeContactBaseActivity() {

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
        setToolbar(toolbar, TitleModel.Id(R.string.LNG_CHANGING_EMAIL), text)
    }
}
