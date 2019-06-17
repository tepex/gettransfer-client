package com.kg.gettransfer.presentation.ui

import android.os.Bundle
import android.support.annotation.CallSuper
import android.support.annotation.StringRes

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.arellomobile.mvp.MvpAppCompatFragment
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter

import com.kg.gettransfer.R
import com.kg.gettransfer.domain.ApiException
import com.kg.gettransfer.domain.DatabaseException

import com.kg.gettransfer.extensions.setThrottledClickListener

import com.kg.gettransfer.presentation.presenter.LogInPresenter

import com.kg.gettransfer.presentation.ui.MainLoginActivity.Companion.INVALID_EMAIL
import com.kg.gettransfer.presentation.ui.MainLoginActivity.Companion.INVALID_PASSWORD
import com.kg.gettransfer.presentation.ui.MainLoginActivity.Companion.INVALID_PHONE

import com.kg.gettransfer.presentation.view.LogInView
import com.kg.gettransfer.presentation.view.LogInView.Companion.EXTRA_EMAIL_TO_LOGIN

import io.sentry.Sentry
import io.sentry.event.BreadcrumbBuilder

import kotlinx.android.synthetic.main.fragment_log_in.*
import kotlinx.android.synthetic.main.view_input_password.*

import timber.log.Timber

/**
 * Fragment for user login
 *
 * @author П. Густокашин (Diwixis)
 */
class LogInFragment : MvpAppCompatFragment(), LogInView {

    @InjectPresenter
    internal lateinit var presenter: LogInPresenter

    @ProvidePresenter
    fun createLoginPresenter() = LogInPresenter()

    var changePage: (() -> Unit)? = null

    private val loadingFragment by lazy { LoadingFragment() }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_log_in, container, false)

    @CallSuper
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(presenter) {
            arguments?.let {
                saveProfile(it.getString(EXTRA_EMAIL_TO_LOGIN, ""))
                nextScreen = it.getString(LogInView.EXTRA_NEXT_SCREEN) ?: ""
                transferId = it.getLong(LogInView.EXTRA_TRANSFER_ID, 0L)
                offerId = it.getLong(LogInView.EXTRA_OFFER_ID, 0L)
                rate = it.getInt(LogInView.EXTRA_RATE, 0)
            }
        }
        initTextChangeListeners()
        initClickListeners()
    }

    @CallSuper
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        val emailOrPhone = loginEmailTv.text.toString()
        outState.putString(LOG_IN_EMAIL, emailOrPhone)
        presenter.saveProfile(emailOrPhone)
    }

    private fun initClickListeners() {
        btnLogin.setThrottledClickListener {
            presenter.onLoginClick(loginEmailTv.text.toString(), etPassword.text.toString())
        }
        btnRequestCode.setThrottledClickListener { presenter.loginWithCode(loginEmailTv.text.toString(), changePage) }
    }

    private fun initTextChangeListeners() {
        loginEmailTv.onTextChanged {
            val emailPhone = it.trim()
            btnLogin.isEnabled = emailPhone.isNotEmpty() && etPassword.text?.isNotEmpty() ?: false
            btnRequestCode.isEnabled = emailPhone.isNotEmpty()
        }
        etPassword.onTextChanged {
            btnLogin.isEnabled = loginEmailTv.text?.isNotEmpty() ?: false && it.isNotEmpty()
        }
    }

    override fun setEmail(login: String) {
        loginEmailTv.setText(login)
        etPassword.requestFocus()
    }

    override fun showLoginInfo(title: Int, info: Int) {
        Utils.showLoginDialog(context!!, message = getString(info), title = getString(title))
    }

    override fun showValidationError(errorType: Int) {
        val errStringRes = when (errorType) {
            INVALID_EMAIL -> R.string.LNG_ERROR_EMAIL
            INVALID_PHONE -> R.string.LNG_ERROR_PHONE
            INVALID_PASSWORD -> R.string.LNG_LOGIN_PASSWORD
            else -> R.string.LNG_BAD_CREDENTIALS_ERROR
        }
        BottomSheetDialog
            .newInstance()
            .apply {
                title = this@LogInFragment.getString(errStringRes)
            }
            .show(fragmentManager)
    }

    companion object {
        const val LOG_IN_EMAIL = ".presentation.ui.LogInFragment"
        fun newInstance() = LogInFragment()
    }

    //--------------------------------------------

    override fun blockInterface(block: Boolean, useSpinner: Boolean) {
        if (block) {
            if (useSpinner) showLoading()
        } else hideLoading()
    }

    override fun setError(finish: Boolean, @StringRes errId: Int, vararg args: String?) {
        //TODO remove BaseView or add code.
    }

    override fun setTransferNotFoundError(transferId: Long) {
        //TODO remove BaseView or add code.
    }

    override fun setError(e: ApiException) {
        Timber.e("code: ${e.code}")
        Sentry.getContext().recordBreadcrumb(BreadcrumbBuilder().setMessage(e.details).build())
        Sentry.capture(e)
        when (e.code) {
            ApiException.NO_USER -> {
                BottomSheetDialog
                    .newInstance()
                    .apply { title = this@LogInFragment.getString(R.string.LNG_BAD_CREDENTIALS_ERROR) }
                    .show(fragmentManager)
            }
            ApiException.NOT_FOUND -> {
                BottomSheetDialog
                    .newInstance()
                    .apply {
                        title =
                            this@LogInFragment.getString(R.string.LNG_ACCOUNT_NOTFOUND, presenter.emailOrPhoneProfile)
                        text = this@LogInFragment.getString(R.string.LNG_ERROR_ACCOUNT_CREATE_USER)
                        buttonOkText = this@LogInFragment.getString(R.string.LNG_SIGNUP)
                        onClickOkButton = { changePage?.invoke() }
                    }
                    .show(fragmentManager)
            }
            else -> {
                BottomSheetDialog
                    .newInstance()
                    .apply { title = e.message ?: "Error" }
                    .show(fragmentManager)
            }
        }
    }

    override fun setError(e: DatabaseException) {
        Sentry.getContext().recordBreadcrumb(BreadcrumbBuilder().setMessage("CacheError: ${e.details}").build())
        Sentry.capture(e)
    }

    private fun showLoading() {
        if (loadingFragment.isAdded) return
        activity?.supportFragmentManager?.beginTransaction()?.apply {
            replace(R.id.container, loadingFragment)
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
}
