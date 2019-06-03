package com.kg.gettransfer.presentation.ui

import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.arellomobile.mvp.MvpAppCompatFragment
import com.kg.gettransfer.R
import com.kg.gettransfer.presentation.presenter.LogInPresenter
import kotlinx.android.synthetic.main.fragment_password.*

class PasswordFragment : MvpAppCompatFragment() {

    private var passwordVisible = false

    private lateinit var parent: LogInFragment
    private lateinit var mPresenter: LogInPresenter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_password, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        parent = parentFragment as LogInFragment
        mPresenter = parent.presenter

        currentUser.text = mPresenter.emailOrPhone

        etPassword.onTextChanged {
            val pas = it.trim()
            mPresenter.setPassword(pas)
            btnLogin.isEnabled = pas.isNotEmpty()
        }
        etPassword.setOnFocusChangeListener { v, hasFocus -> changePasswordToggle(hasFocus) }
        ivPasswordToggle.setOnClickListener { togglePassword() }
        btnLoginByCode.setOnClickListener { mPresenter.loginWithCode() }
        //tvForgotPassword.setOnClickListener { mPresenter.onPassForgot() }

        btnLogin.setOnClickListener { mPresenter.onLoginClick() }
    }

    private fun togglePassword() {
        if (passwordVisible) {
            passwordVisible = false
            hidePassword()
        } else {
            passwordVisible = true
            showPassword()
        }
    }

    private fun showPassword() {
        ivPasswordToggle.setImageResource(R.drawable.ic_eye)
        etPassword.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
        etPassword.text?.length?.let { etPassword.setSelection(it) }
    }

    private fun hidePassword() {
        ivPasswordToggle.setImageResource(R.drawable.ic_eye_off)
        etPassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        etPassword.text?.length?.let { etPassword.setSelection(it) }
    }

    private fun changePasswordToggle(hasFocus: Boolean) {
        when {
            hasFocus && passwordVisible -> ivPasswordToggle.setImageResource(R.drawable.ic_eye)
            !hasFocus && passwordVisible -> ivPasswordToggle.setImageResource(R.drawable.ic_eye_inactive)
            hasFocus && !passwordVisible -> ivPasswordToggle.setImageResource(R.drawable.ic_eye_off)
            else -> ivPasswordToggle.setImageResource(R.drawable.ic_eye_off_inactive)
        }
    }
}