package com.kg.gettransfer.presentation.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.arellomobile.mvp.MvpAppCompatFragment
import com.kg.gettransfer.R
import com.kg.gettransfer.presentation.presenter.LoginPresenterNew
import kotlinx.android.synthetic.main.fragment_sms_code.*

class SmsCodeFragment: MvpAppCompatFragment() {

    private lateinit var mActivity: LoginActivityNew
    private lateinit var mPresenter: LoginPresenterNew

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_sms_code, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mActivity = activity as LoginActivityNew
        mPresenter = mActivity.presenter

        smsTitle.text = getString(R.string.LNG_LOGIN_SEND_SMS_CODE).plus(" ").plus(mPresenter.emailOrPhone)

        pinView.onTextChanged {
            mPresenter.setPassword(it)
            btnLogin.isEnabled = it.length == pinView.itemCount
        }

        btnLogin.setOnClickListener { mPresenter.onLoginClick() }
    }
}