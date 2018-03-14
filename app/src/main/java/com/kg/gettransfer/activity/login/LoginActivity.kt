package com.kg.gettransfer.activity.login


import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.text.method.LinkMovementMethod
import android.util.Patterns.EMAIL_ADDRESS
import android.view.View
import android.view.View.*
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import com.kg.gettransfer.R
import org.koin.android.ext.android.inject


/**
 * Created by denisvakulenko on 01/02/2018.
 */


class LoginActivity : AppCompatActivity(), LoginContract.View {
    override val presenter: LoginContract.Presenter by inject()

    private val pb by lazy { findViewById<ProgressBar>(R.id.progressBar) }

    private val etEmail by lazy { findViewById<EditText>(R.id.etEmail) }
    private val etPass by lazy { findViewById<EditText>(R.id.etPass) }

    private val tvError by lazy { findViewById<TextView>(R.id.tvError) }

    private val tvRestorePass by lazy { findViewById<TextView>(R.id.tvRestorePass) }

    private val fab by lazy { findViewById<FloatingActionButton>(R.id.fab) }


    // --


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        installTextWatcher()

        val tvRestorePass = findViewById<TextView>(R.id.tvRestorePass)
        tvRestorePass.movementMethod = LinkMovementMethod.getInstance()

        presenter.view = this
        presenter.start()
    }


    // --


    override fun loginSuccess() {
        val returnIntent = Intent()
        setResult(Activity.RESULT_OK, returnIntent)
        finish()
    }


    override fun showError(message: String?) {
        tvError.visibility = if (message == null) GONE else VISIBLE
        tvError.text = message
    }


    override fun busyChanged(busy: Boolean) {
        etEmail.isEnabled = !busy
        etPass.isEnabled = !busy

        tvRestorePass.visibility = if (!busy) VISIBLE else INVISIBLE

        pb.visibility = if (busy) VISIBLE else INVISIBLE

        updateFabVisibility()
    }


    // --


    fun updateFabVisibility() {
        fab.visibility = if (!presenter.busy && validateFields()) VISIBLE else INVISIBLE
    }


    fun login(v: View) {
        login()
    }

    fun login() {
        presenter.login(etEmail.text.toString(), etPass.text.toString())
    }


    fun back(v: View) {
        val returnIntent = Intent()
        setResult(Activity.RESULT_CANCELED, returnIntent)
        finish()
    }


    // --


    private fun installTextWatcher() {
        val textWatcher: TextWatcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                updateFabVisibility()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        }

        etEmail.addTextChangedListener(textWatcher)
        etPass.addTextChangedListener(textWatcher)

        etPass.setOnEditorActionListener { _, id, _ ->
            if (id == EditorInfo.IME_ACTION_DONE) {
                login()
                return@setOnEditorActionListener true
            }
            false
        }
    }


    private fun validateFields(): Boolean =
            EMAIL_ADDRESS.matcher(etEmail.text).matches() && etPass.text.length >= 8


    override fun onDestroy() {
        super.onDestroy()
        presenter.stop()
    }
}