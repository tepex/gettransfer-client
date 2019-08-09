package com.kg.gettransfer.presentation.ui

import android.os.Bundle
import androidx.annotation.CallSuper
import androidx.appcompat.widget.Toolbar
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
        @Suppress("UnsafeCast")
        setToolbar(toolbar as Toolbar, R.string.LNG_PROFILE)

        nameField.field_input.onTextChanged { presenter.setName(it) }

        View.OnClickListener { presenter.onChangeEmailClicked() }.also { listener ->
            with(emailField) {
                setOnClickListener(listener)
                field_input.setOnClickListener(listener)
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

    @Suppress("ComplexMethod")
    private fun initPhoneTextChangeListeners() {
        with(phoneField.field_input) {
            onTextChanged { text ->
                if (text.isEmpty() && isFocused) {
                    setText("+")
                    setSelection(1)
                }
            }
            setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus) {
                    if (phone.isEmpty()) {
                        context?.let { c ->
                            val phoneCode = Utils.getPhoneCodeByCountryIso(c)
                            setText(if (phoneCode > 0) "+$phoneCode" else "+")
                        }
                    }
                } else if (phone.length < MIN_PHONE_LENGTH) {
                    setText("")
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

    companion object {
        const val MIN_PHONE_LENGTH = 5
    }
}
