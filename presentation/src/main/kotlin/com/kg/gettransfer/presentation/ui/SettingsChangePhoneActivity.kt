package com.kg.gettransfer.presentation.ui

import android.os.Bundle

import androidx.annotation.CallSuper

import com.kg.gettransfer.R
import com.kg.gettransfer.presentation.model.TitleModel
import com.kg.gettransfer.presentation.presenter.SettingsChangeContactPresenter

import kotlinx.android.synthetic.main.activity_settings_change_phone.*
import kotlinx.android.synthetic.main.toolbar.*
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter

class SettingsChangePhoneActivity : SettingsChangeContactBaseActivity() {

    @InjectPresenter
    internal lateinit var presenter: SettingsChangeContactPresenter

    @ProvidePresenter
    fun createSettingsChangeContactPresenter() = SettingsChangeContactPresenter(false)

    override fun getPresenter(): SettingsChangeContactPresenter = presenter

    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings_change_phone)
        initFields(activationCodeView, btnChangePhone, scrollContent)
        initPhoneTextChangeListeners()
    }

    private fun initPhoneTextChangeListeners() {
        with(phoneLayout) {
            setOnTextChanged { presenter.newContact = it.trim().replace(" ", "") }
            setOnFocusChangeListener()
        }
    }

    override fun setToolbar(text: String?) {
        setToolbar(toolbar, TitleModel.Id(R.string.LNG_CHANGING_PHONE), text)
    }
}
