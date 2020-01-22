package com.kg.gettransfer.presentation.ui

import android.os.Bundle

import androidx.annotation.CallSuper

import com.kg.gettransfer.R
import com.kg.gettransfer.presentation.model.TitleModel
import com.kg.gettransfer.presentation.presenter.SettingsChangePasswordPresenter
import com.kg.gettransfer.presentation.view.SettingsChangePasswordView

import kotlinx.android.synthetic.main.activity_settings_change_password.*
import kotlinx.android.synthetic.main.activity_settings_change_password.btnSave
import kotlinx.android.synthetic.main.toolbar.*
import kotlinx.android.synthetic.main.view_input_password.view.*

import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter

class SettingsChangePasswordActivity : BaseActivity(), SettingsChangePasswordView {

    @InjectPresenter
    internal lateinit var presenter: SettingsChangePasswordPresenter

    @ProvidePresenter
    fun createSettingsChangePasswordPresenter() = SettingsChangePasswordPresenter()

    override fun getPresenter(): SettingsChangePasswordPresenter = presenter

    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings_change_password)
        setToolbar(toolbar, TitleModel.Id(R.string.LNG_LOGIN_PASSWORD_SECTION))
        newPasswordLayout.etPassword.requestFocus()

        newPasswordLayout.etPassword.onTextChanged { presenter.setPassword(it, false) }
        repeatNewPasswordLayout.etPassword.onTextChanged { presenter.setPassword(it, true) }

        btnSave.setOnClickListener { presenter.onSaveClick() }
    }

    override fun setError(finish: Boolean, errId: Int, vararg args: String?) {
        showDialog(getString(errId))
    }

    private fun showDialog(error: String) {
        BottomSheetDialog.newInstance().apply {
            title = error
            imageId = R.drawable.ic_warning
        }.show(supportFragmentManager)
    }

    override fun enableBtnSave(enable: Boolean) { btnSave.isEnabled = enable }

    override fun onBackPressed() { presenter.onBackCommandClick() }
}
