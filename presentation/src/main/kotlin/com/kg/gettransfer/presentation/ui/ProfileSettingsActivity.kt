package com.kg.gettransfer.presentation.ui

import android.os.Bundle
import androidx.annotation.CallSuper
import androidx.appcompat.widget.Toolbar
import android.view.View

import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter

import com.kg.gettransfer.R
import com.kg.gettransfer.presentation.model.ProfileModel
import com.kg.gettransfer.presentation.presenter.ProfileSettingsPresenter
import com.kg.gettransfer.presentation.view.ProfileSettingsView

import kotlinx.android.synthetic.main.activity_profile_settings.*
import kotlinx.android.synthetic.main.activity_profile_settings.toolbar
import kotlinx.android.synthetic.main.view_settings_editable_field.view.*

class ProfileSettingsActivity : BaseActivity(), ProfileSettingsView {

    @InjectPresenter
    internal lateinit var presenter: ProfileSettingsPresenter

    @ProvidePresenter
    fun profileSettingsPresenterProvider() = ProfileSettingsPresenter()

    override fun getPresenter(): ProfileSettingsPresenter = presenter

    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_settings)
        @Suppress("UnsafeCast")
        setToolbar(toolbar as Toolbar, R.string.LNG_PROFILE)

        nameField.field_input.onTextChanged { presenter.setName(it) }
        initClickListeners()
    }

    private fun initClickListeners() {
        View.OnClickListener { presenter.onChangeEmailClicked() }.also { listener ->
            with(emailField) {
                setOnClickListener(listener)
                field_input.setOnClickListener(listener)
            }
        }
        View.OnClickListener { presenter.onChangePhoneClicked() }.also { listener ->
            with(phoneField) {
                setOnClickListener(listener)
                field_input.setOnClickListener(listener)
            }
        }
        passwordField.setOnClickListener { presenter.onChangePasswordClicked() }
        btnLogout.setOnClickListener { presenter.onLogout() }
        btnSave.setOnClickListener { presenter.onSaveBtnClicked() }
    }

    override fun initFields(profile: ProfileModel) {
        with(profile) {
            nameField.field_input.setText(name)
            if (!email.isNullOrEmpty()) {
                emailField.field_input.setText(email)
            }
            if (!phone.isNullOrEmpty()) {
                phoneField.field_input.setText((phone))
            }
        }
    }

    override fun setEnabledBtnSave(enabled: Boolean) {
        btnSave.isEnabled = enabled
    }

    override fun onBackPressed() {
        presenter.onBackCommandClick()
    }
}
