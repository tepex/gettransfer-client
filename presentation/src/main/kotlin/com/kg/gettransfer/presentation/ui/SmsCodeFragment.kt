package com.kg.gettransfer.presentation.ui

import android.os.Bundle
import android.os.CountDownTimer
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.arellomobile.mvp.MvpAppCompatFragment
import com.kg.gettransfer.R
import com.kg.gettransfer.extensions.isVisible
import com.kg.gettransfer.presentation.presenter.LoginPresenter
import com.kg.gettransfer.presentation.view.SmsCodeView
import kotlinx.android.synthetic.main.fragment_sms_code.*

class SmsCodeFragment: MvpAppCompatFragment(), SmsCodeView {

    private lateinit var mActivity: LoginActivity
    private lateinit var mPresenter: LoginPresenter

    private lateinit var timerBtnResendCode: CountDownTimer
    private var smsResendDelay: Long = SMS_RESEND_DELAY_MILLIS

    companion object {
        const val SMS_RESEND_DELAY_MILLIS = 90_000L
        const val SEC_IN_MILLIS = 1_000L
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_sms_code, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mActivity = activity as LoginActivity
        mPresenter = mActivity.presenter
        mActivity.smsCodeView = this
        smsResendDelay = mPresenter.smsResendDelaySec * SEC_IN_MILLIS

        smsTitle.text = when(mPresenter.isPhone) {
            true -> getString(R.string.LNG_LOGIN_SEND_SMS_CODE)
            false -> getString(R.string.LNG_LOGIN_SEND_EMAIL_CODE)
        }.plus(" ").plus(mPresenter.emailOrPhone)

        pinView.onTextChanged {
            if (wrongCodeError.isVisible) pinView.setTextColor(ContextCompat.getColor(mActivity, R.color.color_gtr_green))
            mPresenter.setPassword(it)
            btnLogin.isEnabled = it.length == pinView.itemCount
        }

        setTimer()
        btnResendCode.setOnClickListener { mPresenter.sendVerificationCode() }

        btnLogin.setOnClickListener { mPresenter.onLoginClick(true) }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        timerBtnResendCode.cancel()
    }

    private fun setTimer() {
        setStateBtnResendMessage(false)
        timerBtnResendCode = object: CountDownTimer(smsResendDelay, SEC_IN_MILLIS) {
            override fun onTick(millisUntilFinished: Long) {
                btnResendCode.text = getString(R.string.LNG_LOGIN_RESEND_WAIT, (millisUntilFinished / SEC_IN_MILLIS).toString())
                        .plus(" ${getString(R.string.LNG_SEC)}")
            }

            override fun onFinish() {
                setStateBtnResendMessage(true)
                btnResendCode.text = getText(R.string.LNG_LOGIN_RESEND_ALLOW)
            }
        }
        timerBtnResendCode.start()
    }

    override fun updateTimerResendCode() {
        setStateBtnResendMessage(false)
        timerBtnResendCode.start()
    }

    override fun showErrorText(show: Boolean, text: String?) {
        pinView.setTextColor(ContextCompat.getColor(mActivity, if (show) R.color.color_gtr_red else R.color.color_gtr_green))
        wrongCodeError.isVisible = show
        text?.let { wrongCodeError.text = text }
    }

    private fun setStateBtnResendMessage(enable: Boolean) {
        btnResendCode.apply {
            //isAllCaps = enable
            isEnabled = enable
        }
    }
}