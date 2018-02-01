package com.kg.gettransfer.login

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.EditText
import android.widget.TextView
import com.kg.gettransfer.R


/**
 * Created by denisvakulenko on 01/02/2018.
 */


class LoginActivity : AppCompatActivity(), LoginContract.View {
    override val presenter: LoginContract.Presenter = LoginPresenter()


    private val etEmail by lazy { findViewById<EditText>(R.id.etEmail) }
    private val etPass by lazy { findViewById<EditText>(R.id.etPass) }

    private val tvError by lazy { findViewById<TextView>(R.id.tvError) }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        presenter.view = this // TODO: DI later
    }

    override fun showError(message: String) {
        tvError.text = message
    }

    override fun updateLoadingIndicator(visible: Boolean) {

    }

    fun login(v: View) {
        presenter.login(etEmail.text.toString(), etPass.text.toString())
    }
}