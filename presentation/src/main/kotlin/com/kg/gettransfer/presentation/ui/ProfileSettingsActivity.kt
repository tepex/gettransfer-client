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
import com.kg.gettransfer.utilities.PhoneNumberFormatter
import kotlinx.android.synthetic.main.activity_profile_settings.*
import kotlinx.android.synthetic.main.activity_profile_settings.toolbar
import kotlinx.android.synthetic.main.view_settings_editable_field.view.*

class ProfileSettingsActivity : BaseActivity(), ProfileSettingsView {

    @InjectPresenter
    internal lateinit var presenter: ProfileSettingsPresenter

    @ProvidePresenter
    fun profileSettingsPresenterProvider() = ProfileSettingsPresenter()

    override fun getPresenter(): ProfileSettingsPresenter = presenter

    private val phone
        get() = phoneField.field_input.text.toString().replace(" ", "")

    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_settings)
        setToolbar(toolbar as Toolbar, R.string.LNG_PROFILE)

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
            if (!email.isNullOrEmpty()) {
                emailField.field_input.setText(email)
            }

            phoneField.field_input.addTextChangedListener(PhoneNumberFormatter())
            if (!phone.isNullOrEmpty()) {
                setEnabledPhoneField(false)
                phoneField.field_input.setText(phone)
            } else {
                setEnabledPhoneField(true)
                initPhoneTextChangeListeners()
                with(phoneField.field_input) {
                    onTextChanged { presenter.setPhone(it) }
                }
            }
        }
    }

    private fun initPhoneTextChangeListeners() {
        with(phoneField.field_input) {
            onTextChanged { text ->
                if (text.isEmpty() && isFocused) {
                    setText("+")
                    setSelection(1)
                }
            }
            setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus) phone.let {
                    val phoneCode = Utils.getPhoneCodeByCountryIso(context!!)
                    if (it.isEmpty()) {
                        setText(if (phoneCode > 0) "+".plus(phoneCode) else "+")
                    }
                }
                else phone.let {
                    if (it.length <= 4) {
                        setText("")
                    }
                }
            }
        }
    }

    override fun setEnabledBtnSave(enabled: Boolean) {
        btnSave.isEnabled = enabled
    }

    override fun setEnabledPhoneField(enabled: Boolean) {
        phoneField.field_input.isFocusable = enabled
    }

    override fun onBackPressed() {
        presenter.onBackCommandClick()
    }
}