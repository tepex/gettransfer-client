package com.kg.gettransfer.presentation.ui

import android.os.Bundle
import androidx.annotation.CallSuper
import androidx.core.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import com.kg.gettransfer.R
import com.kg.gettransfer.domain.ApiException
import com.kg.gettransfer.extensions.hideKeyboard
import com.kg.gettransfer.extensions.setThrottledClickListener
import com.kg.gettransfer.presentation.presenter.SmsCodePresenter
import com.kg.gettransfer.presentation.view.LogInView
import com.kg.gettransfer.presentation.view.SmsCodeView
import io.sentry.Sentry
import io.sentry.event.BreadcrumbBuilder
import kotlinx.android.synthetic.main.fragment_sms_code.*
import kotlinx.serialization.json.JSON
// import leakcanary.AppWatcher
import timber.log.Timber

@Suppress("TooManyFunctions")
class SmsCodeFragment : BaseLogInFragment(), SmsCodeView {

    @InjectPresenter
    internal lateinit var presenter: SmsCodePresenter

    @ProvidePresenter
    fun smsCodePresenterProvider() = SmsCodePresenter()

    override fun getPresenter(): SmsCodePresenter = presenter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_sms_code, container, false)

    @CallSuper
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(presenter) {
            arguments?.let { bundle ->
                params = JSON.parse(LogInView.Params.serializer(), bundle.getString(LogInView.EXTRA_PARAMS) ?: "")
                isPhone = bundle.getBoolean(SmsCodeView.EXTRA_IS_PHONE)
            }
            pinItemsCount = pinView.itemCount
        }

        smsTitle.text = "${getString(getTitleId())} ${presenter.params.emailOrPhone}"

        pinView.onTextChanged { code ->
            presenter.pinCode = code
            btnDone.isEnabled = presenter.isEnabledButtonDone
        }

        loginBackButton.setOnClickListener { v ->
            v.hideKeyboard()
            presenter.back()
        }

        btnResendCode.setOnClickListener { v ->
            v.hideKeyboard()
            pinView.setText("")
            presenter.sendVerificationCode()
        }

        btnDone.setThrottledClickListener { v ->
            v.hideKeyboard()
            presenter.onLoginClick()
        }
    }

    private fun getTitleId() =
        if (presenter.isPhone) R.string.LNG_LOGIN_SEND_SMS_CODE else R.string.LNG_LOGIN_SEND_EMAIL_CODE

    override fun startTimer() {
        btnResendCode.isEnabled = false
    }

    override fun tickTimer(millisUntilFinished: Long, interval: Long) {
        btnResendCode.text =
            getString(R.string.LNG_LOGIN_RESEND_WAIT, (millisUntilFinished / interval).toString())
                .plus(" ${getString(R.string.LNG_SEC)}")
    }

    override fun finishTimer() {
        btnResendCode.isEnabled = true
        btnResendCode.text = getText(R.string.LNG_LOGIN_RESEND_ALLOW)
    }

    override fun showCodeExpiration(codeExpiration: Int) {
        tvCodeExpiration.text = getString(R.string.LNG_CODE_EXPIRATION, codeExpiration.toString())
    }

    override fun setError(e: ApiException) {
        Timber.e("code: ${e.code}")
        Sentry.getContext().recordBreadcrumb(BreadcrumbBuilder().setMessage(e.details).build())
        Sentry.capture(e)
        if (e.code == ApiException.NO_USER) {
            context?.let { pinView.setTextColor(ContextCompat.getColor(it, R.color.color_gtr_red)) }
            return
        }
        val titleError = getTitleError(e)
        BottomSheetDialog
            .newInstance()
            .apply {
                title = titleError
            }
            .show(requireFragmentManager())
    }

    private fun getTitleError(e: ApiException): String {
        if (e.isHttpException && e.details.isNotEmpty()) return e.details
        return when (e.code) {
            ApiException.NETWORK_ERROR -> getString(R.string.LNG_NETWORK_ERROR)
            else -> e.message ?: "Error"
        }
    }

    override fun onDestroy() {
        super.onDestroy()
//        AppWatcher.objectWatcher.watch(this)
    }

    companion object {
        fun newInstance() = SmsCodeFragment()
    }
}
