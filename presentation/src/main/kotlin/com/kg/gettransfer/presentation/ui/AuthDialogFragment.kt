package com.kg.gettransfer.presentation.ui

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.arellomobile.mvp.MvpAppCompatDialogFragment
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.kg.gettransfer.R
import com.kg.gettransfer.domain.ApiException
import com.kg.gettransfer.domain.DatabaseException
import com.kg.gettransfer.extensions.getString
import com.kg.gettransfer.presentation.parent.AuthDialogParent
import com.kg.gettransfer.presentation.presenter.AuthDialogPresenter
import com.kg.gettransfer.presentation.view.AuthDialogView
import kotlinx.android.synthetic.main.fragment_login_dialog.*

class AuthDialogFragment: MvpAppCompatDialogFragment(), AuthDialogView {
    lateinit var parent: AuthDialogParent

    @InjectPresenter
    lateinit var presenter: AuthDialogPresenter

    @ProvidePresenter
    fun getAuthDialogPresenter() = AuthDialogPresenter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_login_dialog, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        parent = activity as AuthDialogParent
        btn_check_auth.setOnClickListener {
            presenter.putAccount(et_auth_email.getString(), et_auth_phone.getString())
        }
    }

    override fun redirectToLogin() {
        parent.redirectToLogin(et_auth_email.getString())
        dismiss()
    }

    override fun onAccountCreated() {
        dismiss()
    }

    override fun setEmail(email: String, withFocus: Boolean) {
        et_auth_email.setText(email)
        if (withFocus)
            et_auth_email.requestFocus()
    }

    override fun setPhone(phone: String, withFocus: Boolean) {
        et_auth_phone.setText(phone)
        if (withFocus)
            et_auth_phone.requestFocus()
    }

    override fun blockInterface(block: Boolean, useSpinner: Boolean) {
        (activity as BaseActivity)
                .blockInterface(block, useSpinner)
    }

    override fun onResume() {
        super.onResume()
        context?.let {
            dialog.window?.setLayout(Utils.dpToPxInt(it, 256F), Utils.dpToPxInt(it, 480F))
            dialog.window?.setGravity(Gravity.CENTER)
        }

    }

    override fun setError(finish: Boolean, errId: Int, vararg args: String?) {}
    override fun setError(e: ApiException) {}
    override fun setError(e: DatabaseException) {}

}