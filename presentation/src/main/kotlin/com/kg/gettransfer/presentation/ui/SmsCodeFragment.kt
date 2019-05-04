package com.kg.gettransfer.presentation.ui

import android.os.Bundle
import android.os.CountDownTimer
import android.support.v4.content.res.ResourcesCompat
import android.text.Spannable
import android.text.SpannableString
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.arellomobile.mvp.MvpAppCompatFragment
import com.kg.gettransfer.R
import com.kg.gettransfer.presentation.presenter.LoginPresenterNew
import com.kg.gettransfer.presentation.view.SmsCodeView
import kotlinx.android.synthetic.main.fragment_sms_code.*

class SmsCodeFragment: MvpAppCompatFragment(), SmsCodeView {

    private lateinit var mActivity: LoginActivityNew
    private lateinit var mPresenter: LoginPresenterNew

    private lateinit var timerBtnResendCode: CountDownTimer

    companion object {
        const val RESEND_CODE_TIME_MILLIS = 90_000L
        const val SEC_IN_MILLIS = 1_000L
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_sms_code, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mActivity = activity as LoginActivityNew
        mPresenter = mActivity.presenter
        mActivity.smsCodeView = this

        smsTitle.text = getString(R.string.LNG_LOGIN_SEND_SMS_CODE).plus(" ").plus(mPresenter.emailOrPhone)

        pinView.onTextChanged {
            mPresenter.setPassword(it)
            btnLogin.isEnabled = it.length == pinView.itemCount
        }

        setTimer()
        btnResendCode.setOnClickListener { mPresenter.sendVerificationCode() }

        btnLogin.setOnClickListener { mPresenter.onLoginClick() }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        timerBtnResendCode.cancel()
    }

    private fun setTimer() {
        setStateBtnResendMessage(false)

        val regularFont = ResourcesCompat.getFont(mActivity, R.font.sf_pro_text_regular)!!.style
        val boldFont = ResourcesCompat.getFont(mActivity, R.font.sf_pro_text_semibold)!!.style

        val strResendCode = getString(R.string.LNG_LOGIN_RESEND_CODE)
        val strIn = getString(R.string.LNG_DATE_IN_HOURS).toLowerCase()
        val strS = getString(R.string.LNG_SEC)

        timerBtnResendCode = object: CountDownTimer(RESEND_CODE_TIME_MILLIS, SEC_IN_MILLIS) {
            override fun onTick(millisUntilFinished: Long) {
                val boldStr = strIn.plus(" ${millisUntilFinished / SEC_IN_MILLIS} ").plus(strS)
                val spannableString = SpannableString(strResendCode.plus("\n").plus(boldStr))
                val boldStrIndex = spannableString.indexOf(boldStr)

                spannableString.setSpan(StyleSpan(regularFont), 0, boldStrIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                spannableString.setSpan(StyleSpan(boldFont), boldStrIndex, spannableString.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

                btnResendCode.setText(spannableString, TextView.BufferType.SPANNABLE)
            }

            override fun onFinish() {
                setStateBtnResendMessage(true)
                btnResendCode.text = getString(R.string.LNG_LOGIN_RESEND_CODE)
            }
        }

        timerBtnResendCode.start()
    }

    override fun updateTimerResendCode() {
        setStateBtnResendMessage(false)
        timerBtnResendCode.start()
    }

    private fun setStateBtnResendMessage(enable: Boolean) {
        btnResendCode.apply {
            isAllCaps = enable
            isEnabled = enable
        }
    }
}