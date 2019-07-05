package com.kg.gettransfer.presentation.ui

import android.os.Bundle
import android.support.annotation.CallSuper
import android.support.annotation.StringRes
import android.support.v4.app.Fragment
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
import com.kg.gettransfer.extensions.hideKeyboard
import com.kg.gettransfer.extensions.isVisible
import com.kg.gettransfer.extensions.setThrottledClickListener
import com.kg.gettransfer.presentation.presenter.SmsCodePresenter
import com.kg.gettransfer.presentation.view.LogInView
import com.kg.gettransfer.presentation.view.SmsCodeView
import io.sentry.Sentry
import io.sentry.event.BreadcrumbBuilder
import kotlinx.android.synthetic.main.fragment_sms_code.*
import kotlinx.serialization.json.JSON
import timber.log.Timber

class SmsCodeFragment : MvpAppCompatFragment(), SmsCodeView {

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
                params = JSON.parse(LogInView.Params.serializer(), it.getString(LogInView.EXTRA_PARAMS) ?: "")
                isPhone = it.getBoolean(EXTERNAL_IS_PHONE)
            }
            pinItemsCount = pinView.itemCount
        }

        smsTitle.text = "${getString(presenter.getTitleId())} ${presenter.params.emailOrPhone}"

        pinView.onTextChanged { code ->
            presenter.pinCode = code
            presenter.checkAfterPinChanged()
        }

        loginBackButton.setOnClickListener {
            it.hideKeyboard()
            presenter.back()
        }

        btnResendCode.setOnClickListener {
            it.hideKeyboard()
            presenter.sendVerificationCode()
            presenter.setTimer()
        }

        btnDone.setThrottledClickListener {
            it.hideKeyboard()
            presenter.onLoginClick()
        }
    }

    @CallSuper
    override fun onDetach() {
        /* dirty hack https://stackoverflow.com/a/15656428 */
        try {
            val childFragmentManager = Fragment::class.java.getDeclaredField("mChildFragmentManager")
            childFragmentManager.isAccessible = true
            childFragmentManager.set(this, null)

        } catch (e: NoSuchFieldException) {
            throw RuntimeException(e)
        } catch (e: IllegalAccessException) {
            throw RuntimeException(e)
        }
        super.onDetach()
    }

    override fun setBtnDoneIsEnabled(isEnabled: Boolean) {
        btnDone.isEnabled = isEnabled
    }

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
                        title = this@SmsCodeFragment.getString(R.string.LNG_ACCOUNT_NOTFOUND, presenter.params.emailOrPhone)
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
        fragmentManager?.beginTransaction()?.apply {
            replace(android.R.id.content, loadingFragment)
            commit()
        }
    }

    private fun hideLoading() {
        if (!loadingFragment.isAdded) return
        fragmentManager?.beginTransaction()?.apply {
            remove(loadingFragment)
            commit()
        }
    }

    override fun setTransferNotFoundError(transferId: Long) {
        //TODO remove BaseView or add code.
    }

    companion object {
        const val SMS_RESEND_DELAY_MILLIS = 90_000L

        const val EXTERNAL_IS_PHONE = ".presentation.ui.SmsCodeFragment_IS_PHONE"
        const val EXTERNAL_EMAIL_OR_PHONE = ".presentation.ui.SmsCodeFragment_EMAIL_OR_PHONE"
        const val EXTERNAL_SMS_RESEND_DELAY_SEC = ".presentation.ui.SmsCodeFragment_EMAIL_OR_PHONE"

        fun newInstance() = SmsCodeFragment()
    }
}
