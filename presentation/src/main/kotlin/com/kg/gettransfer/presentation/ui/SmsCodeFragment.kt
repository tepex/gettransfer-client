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

        with(presenter) {
            arguments?.let {
                nextScreen = it.getString(LogInView.EXTRA_NEXT_SCREEN) ?: ""
                emailOrPhone = it.getString(SmsCodeView.EXTERNAL_EMAIL_OR_PHONE) ?: ""
                isPhone = it.getBoolean(SmsCodeView.EXTERNAL_IS_PHONE)
            }
        }

        smsTitle.text = when (presenter.isPhone ?: false) {
            true -> getString(R.string.LNG_LOGIN_SEND_SMS_CODE)
            false -> getString(R.string.LNG_LOGIN_SEND_EMAIL_CODE)
        }.plus(" ").plus(presenter.emailOrPhone)

        pinView.onTextChanged { code ->
            if (wrongCodeError.isVisible) {
                context?.let { pinView.setTextColor(ContextCompat.getColor(it, R.color.color_gtr_green)) }
            }
            presenter.setCode(code)
            btnDone.isEnabled = code.length == pinView.itemCount
        }

        loginBackButton.setOnClickListener { presenter.back() }
        btnResendCode.setOnClickListener { presenter.sendVerificationCode() }
        btnDone.setThrottledClickListener { presenter.onLoginClick() }

        setTimer()
    }

    @CallSuper
    override fun onDestroyView() {
        super.onDestroyView()
        timerBtnResendCode.cancel()
    }


    /* Timer */

    private fun setTimer() {
        timerBtnResendCode = object : CountDownTimer(presenter.smsResendDelaySec * SEC_IN_MILLIS, SEC_IN_MILLIS) {
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
    }

    override fun updateTimerResendCode() {
        btnResendCode.isEnabled = false
        timerBtnResendCode.start()
    }


    /* Errors */

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

    override fun setTransferNotFoundError(transferId: Long) {
        //TODO remove BaseView or add code.
    }


    /* Loading fragment */

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

    companion object {
        const val SEC_IN_MILLIS = 1_000L

        fun newInstance() = SmsCodeFragment()
    }
}
