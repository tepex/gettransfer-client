package com.kg.gettransfer.presentation.ui

import android.os.Bundle
import androidx.annotation.CallSuper
import androidx.appcompat.widget.Toolbar
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.kg.gettransfer.R
import com.kg.gettransfer.presentation.presenter.SettingsChangePasswordPresenter
import com.kg.gettransfer.presentation.view.SettingsChangePasswordView
import kotlinx.android.synthetic.main.activity_settings_change_password.*
import kotlinx.android.synthetic.main.activity_settings_change_password.btnSave
import kotlinx.android.synthetic.main.view_input_password.view.*

class SettingsChangePasswordActivity: BaseActivity(), SettingsChangePasswordView {

    @InjectPresenter
    internal lateinit var presenter: SettingsChangePasswordPresenter

    @ProvidePresenter
    fun createSettingsChangePasswordPresenter() = SettingsChangePasswordPresenter()

    override fun getPresenter(): SettingsChangePasswordPresenter = presenter

    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings_change_password)
        setToolbar(toolbar as Toolbar, R.string.LNG_LOGIN_PASSWORD_SECTION)
        newPasswordLayout.etPassword.requestFocus()

        newPasswordLayout.etPassword.onTextChanged { presenter.setPassword(it, false) }
        repeatNewPasswordLayout.etPassword.onTextChanged { presenter.setPassword(it, true) }

        btnSave.setOnClickListener { presenter.onSaveClick() }
    }

    override fun enableBtnSave(enable: Boolean) { btnSave.isEnabled = enable }

    override fun onBackPressed() { presenter.onBackCommandClick() }
}