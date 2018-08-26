package com.kg.gettransfer.presentation.ui

import android.os.Bundle

import android.support.v7.widget.Toolbar

import android.text.TextUtils

import android.util.Patterns

import android.view.View

import com.arellomobile.mvp.MvpAppCompatActivity
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter

import com.kg.gettransfer.R

import com.kg.gettransfer.domain.CoroutineContexts
import com.kg.gettransfer.domain.interactor.ApiInteractor

import com.kg.gettransfer.presentation.presenter.LoginPresenter
import com.kg.gettransfer.presentation.view.LoginView

import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.toolbar.view.*

import org.koin.android.ext.android.inject

class LoginActivity : MvpAppCompatActivity(), LoginView {

    @InjectPresenter
    lateinit var loginPresenter: LoginPresenter

    private val apiInteractor: ApiInteractor by inject()
    private val coroutineContexts: CoroutineContexts by inject()

    private var emptyEmail = true
    private var emptyPassword = true
    private var correctEmail = false

    @ProvidePresenter
    fun createLoginPresenter(): LoginPresenter = LoginPresenter(coroutineContexts, apiInteractor)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        setupToolbar()

        btnLogin.isEnabled = false

        checkFields()
        setOnClickListeners()
    }

    private fun setupToolbar() {
        setSupportActionBar(toolbar as Toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        (toolbar as Toolbar).toolbar_title.setText(R.string.login)
        (toolbar as Toolbar).setNavigationOnClickListener { loginPresenter.onBackCommandClick() }
    }

    private fun setOnClickListeners() {
        btnLogin.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()
            loginPresenter.onLoginClick(email, password)
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

    override fun showError() {
        tvLoginError.visibility = View.VISIBLE
    }

    override fun hideError() {
        tvLoginError.visibility = View.INVISIBLE
    }
}
