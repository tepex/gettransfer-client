package com.kg.gettransfer.presentation.ui

import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.arellomobile.mvp.MvpAppCompatFragment
import com.kg.gettransfer.R
import com.kg.gettransfer.domain.ApiException
import com.kg.gettransfer.extensions.isVisible
import com.kg.gettransfer.presentation.presenter.LoginPresenterNew
import com.kg.gettransfer.presentation.view.PasswordView
import kotlinx.android.synthetic.main.fragment_password.*

class PasswordFragment: MvpAppCompatFragment(), PasswordView {

    private var passwordVisible = false

    private lateinit var mActivity: LoginActivityNew
    private lateinit var mPresenter: LoginPresenterNew

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_password, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mActivity = activity as LoginActivityNew
        mActivity.passwordView = this
        mPresenter = mActivity.presenter

        currentUser.text = mPresenter.emailOrPhone

        etPassword.onTextChanged {
            val pas = it.trim()
            mPresenter.setPassword(pas)
            btnLogin.isEnabled = pas.isNotEmpty()
        }
        etPassword.setOnFocusChangeListener { v, hasFocus -> changePasswordToggle(hasFocus) }
        ivPasswordToggle.setOnClickListener { togglePassword() }
        tvForgotPassword.setOnClickListener { mPresenter.onPassForgot() }

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

    override fun showValidationError(errString: Int?) {
        if(errString == null) {
            tvLoginError.isVisible = false
        } else {
            tvLoginError.isVisible = true
            tvLoginError.setText(errString)
        }
    }

    override fun showError(show: Boolean, error: ApiException) {
        tvLoginError.isVisible = show
        if (show) tvLoginError.text = when{
            error.isNotFound() -> getString(R.string.LNG_NOT_FOUND)
            else -> error.message ?: getString(R.string.LNG_BAD_CREDENTIALS_ERROR)
        }
    }



//    override fun blockInterface(block: Boolean, useSpinner: Boolean) {
//        if (block) tvLoginError.isVisible = false
//    }
//
//    override fun setError(finish: Boolean, @StringRes errId: Int, vararg args: String?) {
//        tvLoginError.isVisible = true
//    }
//
//    override fun setError(e: ApiException) {}
//    override fun setError(e: DatabaseException) {}
}