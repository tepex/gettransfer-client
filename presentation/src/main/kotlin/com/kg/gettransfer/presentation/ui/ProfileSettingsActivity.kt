package com.kg.gettransfer.presentation.ui

import android.os.Bundle
import android.support.annotation.CallSuper
import android.support.v7.widget.Toolbar
import android.view.View
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
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
        setToolbar(toolbar as Toolbar, R.string.LNG_MENU_TITLE_SETTINGS)

        nameField.field_input.onTextChanged { presenter.setName(it) }

        View.OnClickListener { presenter.onChangeEmailClicked() }.let {
            with(emailField) {
                setOnClickListener(it)
                field_input.setOnClickListener(it)
            }
        }
        passwordField.setOnClickListener { presenter.onChangePasswordClicked() }
        btnSave.setOnClickListener { presenter.onSaveBtnClicked() }
    }

    override fun initFields(profile: ProfileModel) {
        with(profile) {
            nameField.field_input.setText(name)
            emailField.field_input.setText(email)
            phoneField.field_input.setText(phone)
        }
    }

    override fun setEnabledBtnSave(enabled: Boolean) {
        btnSave.isEnabled = enabled
    }

    override fun onBackPressed() {
        presenter.onBackCommandClick()
    }
}