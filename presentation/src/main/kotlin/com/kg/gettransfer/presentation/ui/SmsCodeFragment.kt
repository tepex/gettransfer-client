package com.kg.gettransfer.presentation.ui

import android.os.Bundle
import android.os.CountDownTimer

import android.support.annotation.CallSuper
import android.support.annotation.StringRes
import android.support.v4.content.ContextCompat

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.arellomobile.mvp.MvpAppCompatFragment
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter

import com.kg.gettransfer.R
import com.kg.gettransfer.domain.ApiException
import com.kg.gettransfer.domain.DatabaseException

import com.kg.gettransfer.extensions.isVisible
import com.kg.gettransfer.extensions.setThrottledClickListener

import com.kg.gettransfer.presentation.presenter.SmsCodePresenter

import com.kg.gettransfer.presentation.view.LogInView
import com.kg.gettransfer.presentation.view.SmsCodeView

import io.sentry.Sentry
import io.sentry.event.BreadcrumbBuilder

import kotlinx.android.synthetic.main.fragment_sms_code.*

import timber.log.Timber

class SmsCodeFragment : MvpAppCompatFragment(), SmsCodeView {

    private lateinit var timerBtnResendCode: CountDownTimer

    private val loadingFragment by lazy { LoadingFragment() }

    @InjectPresenter
    internal lateinit var presenter: SmsCodePresenter

    @ProvidePresenter
    fun smsCodePresenterProvider() = SmsCodePresenter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_sms_code, container, false)

    @CallSuper
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val smsResendDelay = presenter.smsResendDelaySec * SEC_IN_MILLIS

        val isPhone = arguments?.getBoolean(EXTERNAL_IS_PHONE) ?: false

        with(presenter) {
            arguments?.let {
                nextScreen = it.getString(LogInView.EXTRA_NEXT_SCREEN) ?: ""
                emailOrPhone = arguments?.getString(EXTERNAL_EMAIL_OR_PHONE) ?: ""
            }
        }

        smsTitle.text = when (isPhone) {
            true -> getString(R.string.LNG_LOGIN_SEND_SMS_CODE)
            false -> getString(R.string.LNG_LOGIN_SEND_EMAIL_CODE)
        }.plus(" ").plus(presenter.emailOrPhone)

        pinView.onTextChanged { code ->
            if (wrongCodeError.isVisible) {
                context?.let { pinView.setTextColor(ContextCompat.getColor(it, R.color.color_gtr_green)) }
            }
            btnDone.isEnabled = code.length == pinView.itemCount
        }

        loginBackButton.setOnClickListener { presenter.back() }

        setTimer(smsResendDelay)
        updateTimerResendCode()
        btnResendCode.setOnClickListener { sendVerificationCode(isPhone) }

        btnDone.setThrottledClickListener {
            presenter.onLoginClick(
                presenter.emailOrPhone ?: "",
                pinView.toString(),
                isPhone
            )
        }
    }

    private fun sendVerificationCode(isPhone: Boolean) {
        presenter.sendVerificationCode(presenter.emailOrPhone ?: "", isPhone)
    }

    @CallSuper
    override fun onDestroyView() {
        super.onDestroyView()
        timerBtnResendCode.cancel()
    }

    private fun setTimer(smsResendDelay: Long) {
        btnResendCode.isEnabled = false
        timerBtnResendCode = object : CountDownTimer(smsResendDelay, SEC_IN_MILLIS) {
            override fun onTick(millisUntilFinished: Long) {
                btnResendCode.text =
                    getString(R.string.LNG_LOGIN_RESEND_WAIT, (millisUntilFinished / SEC_IN_MILLIS).toString())
                        .plus(" ${getString(R.string.LNG_SEC)}")
            }

            override fun onFinish() {
                btnResendCode.isEnabled = true
                btnResendCode.text = getText(R.string.LNG_LOGIN_RESEND_ALLOW)
            }
        }
        timerBtnResendCode.start()
    }

    override fun updateTimerResendCode() {
        btnResendCode.isEnabled = false
        timerBtnResendCode.start()
    }

    override fun showErrorText(show: Boolean, text: String?) {
        pinView.setTextColor(
            ContextCompat.getColor(
                context!!,
                if (show) R.color.color_gtr_red else R.color.color_gtr_green
            )
        )
        wrongCodeError.isVisible = show
        text?.let { wrongCodeError.text = text }
    }

    override fun showValidationError(b: Boolean, invaliD_EMAIL: Int) {
        //TODO remove BaseView or add code.
    }

    //----------TODO остатки от группы Base.---------------

    override fun blockInterface(block: Boolean, useSpinner: Boolean) {
        if (block) {
            if (useSpinner) showLoading()
        } else hideLoading()
    }

    override fun setError(e: ApiException) {
        pinView.setTextColor(ContextCompat.getColor(context!!, R.color.color_gtr_red))
        Timber.e("code: ${e.code}")
        Sentry.getContext().recordBreadcrumb(BreadcrumbBuilder().setMessage(e.details).build())
        Sentry.capture(e)
        when (e.code) {
            ApiException.NOT_FOUND -> {
                BottomSheetDialog
                    .newInstance()
                    .apply {
                        title = this@SmsCodeFragment.getString(R.string.LNG_ACCOUNT_NOTFOUND, presenter.emailOrPhone)
                        buttonOkText = this@SmsCodeFragment.getString(R.string.LNG_OK)
                        onDismissCallBack = {
                            presenter.back()
                        }
                    }
                    .show(fragmentManager)
            }
        }
    }

    override fun setError(e: DatabaseException) {
        Sentry.getContext().recordBreadcrumb(BreadcrumbBuilder().setMessage("CacheError: ${e.details}").build())
        Sentry.capture(e)
    }

    override fun setError(finish: Boolean, @StringRes errId: Int, vararg args: String?) {
        Utils.showError(context!!, false, getString(errId, *args))
    }

    private fun showLoading() {
        if (loadingFragment.isAdded) return
        activity?.supportFragmentManager?.beginTransaction()?.apply {
            replace(android.R.id.content, loadingFragment)
            commit()
        }
    }

    private fun hideLoading() {
        if (!loadingFragment.isAdded) return
        activity?.supportFragmentManager?.beginTransaction()?.apply {
            remove(loadingFragment)
            commit()
        }
    }

    override fun setTransferNotFoundError(transferId: Long) {
        //TODO remove BaseView or add code.
    }

    companion object {
        const val SMS_RESEND_DELAY_MILLIS = 90_000L
        const val SEC_IN_MILLIS = 1_000L

        const val EXTERNAL_IS_PHONE = ".presentation.ui.SmsCodeFragment_IS_PHONE"
        const val EXTERNAL_EMAIL_OR_PHONE = ".presentation.ui.SmsCodeFragment_EMAIL_OR_PHONE"
        const val EXTERNAL_SMS_RESEND_DELAY_SEC = ".presentation.ui.SmsCodeFragment_EMAIL_OR_PHONE"

        fun newInstance() = SmsCodeFragment()
    }
}
