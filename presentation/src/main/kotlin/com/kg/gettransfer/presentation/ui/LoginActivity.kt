package com.kg.gettransfer.presentation.ui

import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import com.arellomobile.mvp.MvpAppCompatActivity
import com.arellomobile.mvp.presenter.InjectPresenter
import com.kg.gettransfer.R
import com.kg.gettransfer.presentation.presenter.LoginPresenter
import com.kg.gettransfer.presentation.view.LoginView
import kotlinx.android.synthetic.main.activity_login.*


class LoginActivity : MvpAppCompatActivity(), LoginView {

    @InjectPresenter
    lateinit var loginPresenter: LoginPresenter
    private var emptyEmail = true
    private var emptyPassword = true
    private var correctEmail = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        btnLogin.isEnabled = false
        checkFields()
        btnLogin.setOnClickListener {
            loginPresenter.onLoginClick()
        }
    }

    private fun checkFields() {
        etEmail.onTextChanged {
            correctEmail = Patterns.EMAIL_ADDRESS.matcher(it).matches()
            emptyEmail = TextUtils.isEmpty(it)
            enableBtnLogin()
        }
        etPassword.onTextChanged {
            emptyPassword = TextUtils.isEmpty(it)
            enableBtnLogin()
        }
    }

    private fun enableBtnLogin() {
        btnLogin.isEnabled = !emptyEmail && !emptyPassword && correctEmail
    }
}
