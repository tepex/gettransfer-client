package com.kg.gettransfer.presentation.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.arellomobile.mvp.MvpAppCompatFragment
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.kg.gettransfer.R
import com.kg.gettransfer.domain.ApiException
import com.kg.gettransfer.domain.DatabaseException
import com.kg.gettransfer.presentation.presenter.ChangePasswordPresenter
import com.kg.gettransfer.presentation.presenter.SettingsPresenter
import com.kg.gettransfer.presentation.view.ChangePasswordView
import kotlinx.android.synthetic.main.fragment_change_password.*
import kotlinx.android.synthetic.main.view_input_password.view.*

class ChangePasswordFragment: MvpAppCompatFragment(), ChangePasswordView {

    @InjectPresenter
    internal lateinit var presenter: ChangePasswordPresenter

    @ProvidePresenter
    fun createChangePasswordPresenter() = ChangePasswordPresenter()

    private lateinit var mActivity: SettingsActivity
    private lateinit var mPresenter: SettingsPresenter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_change_password, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        root.setOnClickListener {}
        mActivity = (activity as SettingsActivity)
        mPresenter = mActivity.presenter

        newPasswordLayout.etPassword.onTextChanged { presenter.setPassword(it, false) }
        repeatNewPasswordLayout.etPassword.onTextChanged { presenter.setPassword(it, true) }

        btnSave.setOnClickListener { presenter.onSaveClick() }
    }

    override fun enableBtnSave(enable: Boolean) { btnSave.isEnabled = enable }

    override fun passwordChanged() {
        mPresenter.passwordChanged()
    }

    override fun blockInterface(block: Boolean, useSpinner: Boolean) {}
    override fun setError(e: ApiException) { mActivity.setError(e) }
    override fun setError(e: DatabaseException) { mActivity.setError(e) }
    override fun setError(finish: Boolean, errId: Int, vararg args: String?) { mActivity.setError(finish, errId, *args) }
}