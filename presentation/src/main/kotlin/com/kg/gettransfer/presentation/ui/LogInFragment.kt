package com.kg.gettransfer.presentation.ui

import android.os.Bundle
import androidx.annotation.CallSuper
import androidx.annotation.StringRes
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
import com.kg.gettransfer.extensions.setThrottledClickListener
import com.kg.gettransfer.presentation.presenter.LogInPresenter
import com.kg.gettransfer.presentation.ui.MainLoginActivity.Companion.INVALID_EMAIL
import com.kg.gettransfer.presentation.ui.MainLoginActivity.Companion.INVALID_PASSWORD
import com.kg.gettransfer.presentation.ui.MainLoginActivity.Companion.INVALID_PHONE
import com.kg.gettransfer.presentation.view.BaseView
import com.kg.gettransfer.presentation.view.LogInView

import io.sentry.Sentry
import io.sentry.event.BreadcrumbBuilder

import kotlinx.android.synthetic.main.fragment_log_in.*
import kotlinx.android.synthetic.main.view_input_account_field.view.*
import kotlinx.android.synthetic.main.view_input_password.*
import kotlinx.serialization.json.JSON
//import leakcanary.AppWatcher

import timber.log.Timber

/**
 * Fragment for user login
 *
 * @author П. Густокашин (Diwixis)
 */
@Suppress("TooManyFunctions")
class LogInFragment : MvpAppCompatFragment(), LogInView {

    @InjectPresenter
    internal lateinit var presenter: LogInPresenter

    @ProvidePresenter
    fun createLoginPresenter() = LogInPresenter()

    var changePage: ((String, Boolean) -> Unit)? = null

    private val loadingFragment by lazy { LoadingFragment() }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_log_in, container, false)

    @CallSuper
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(presenter) {
            arguments?.let { args ->
                params = JSON.parse(LogInView.Params.serializer(), args.getString(LogInView.EXTRA_PARAMS) ?: "")
            }
        }
        initTextChangeListeners()
        initClickListeners()
    }

    override fun onResume() {
        super.onResume()
        emailLayout.fieldText.requestFocus()
    }

    private fun initClickListeners() {
        btnLogin.setThrottledClickListener { view ->
            view.hideKeyboard()
            showLoading()
            presenter.onLoginClick()
        }
        btnRequestCode.setThrottledClickListener { view ->
            view.hideKeyboard()
            showLoading()
            presenter.sendVerificationCode()
        }
    }

    private fun initTextChangeListeners() {
        emailLayout.fieldText.onTextChanged { text ->
            presenter.setEmailOrPhone(text)
            btnLogin.isEnabled = presenter.isEnabledButtonLogin
            btnRequestCode.isEnabled = presenter.isEnabledRequestCodeButton
        }
        etPassword.onTextChanged { text ->
            presenter.password = text
            btnLogin.isEnabled = presenter.isEnabledButtonLogin
        }
    }

    override fun setEmail(login: String) {
        emailLayout.fieldText.setText(login)
        etPassword.requestFocus()
    }

    override fun showValidationError(errorType: Int) {
        val errStringRes = when (errorType) {
            INVALID_EMAIL, INVALID_PHONE -> getString(R.string.LNG_ERROR_EMAIL_PHONE, presenter.getPhoneExample())
            INVALID_PASSWORD -> getString(R.string.LNG_LOGIN_PASSWORD)
            else -> getString(R.string.LNG_BAD_CREDENTIALS_ERROR)
        }
        BottomSheetDialog
            .newInstance()
            .apply {
                title = this@LogInFragment.getString(R.string.LNG_ERROR_CREDENTIALS)
                text = errStringRes
                onDismissCallBack = { hideLoading() }
            }
            .show(requireFragmentManager())
    }

    override fun onDestroy() {
        super.onDestroy()
//        AppWatcher.objectWatcher.watch(this)
    }

    companion object {
        const val LOG_IN_EMAIL = ".presentation.ui.LogInFragment"
        fun newInstance() = LogInFragment()
    }

    // --------------------------------------------

    override fun blockInterface(block: Boolean, useSpinner: Boolean) {
        if (block) {
            if (useSpinner) showLoading()
        } else hideLoading()
    }

    // TODO remove BaseView or add code.
    override fun setError(finish: Boolean, @StringRes errId: Int, vararg args: String?) {}

    // TODO remove BaseView or add code.
    override fun setTransferNotFoundError(transferId: Long, dismissCallBack: (() -> Unit)?) {
        val act = activity
        if (act is BaseView) {
            act.setTransferNotFoundError(transferId, dismissCallBack)
        }
    }

    override fun setError(e: ApiException) {
        Timber.e("code: ${e.code}")
        Sentry.getContext().recordBreadcrumb(BreadcrumbBuilder().setMessage(e.details).build())
        Sentry.capture(e)
        val titleError = getTitleError(e)
        when (e.code) {
            ApiException.NO_USER -> BottomSheetDialog
                .newInstance()
                .apply {
                    title = titleError
                    onDismissCallBack = { hideLoading() }
                }
                .show(requireFragmentManager())

            ApiException.NOT_FOUND -> BottomSheetDialog
                .newInstance()
                .apply {
                    title = titleError
                    text = this@LogInFragment.getString(R.string.LNG_ERROR_ACCOUNT_CREATE_USER)
                    buttonOkText = this@LogInFragment.getString(R.string.LNG_SIGNUP)
                    onClickOkButton = { changePage?.invoke(presenter.params.emailOrPhone, presenter.isPhone()) }
                    onDismissCallBack = { hideLoading() }
                    isShowCloseButton = true
                }
                .show(requireFragmentManager())

            else -> BottomSheetDialog
                .newInstance()
                .apply {
                    title = titleError
                    onDismissCallBack = { hideLoading() }
                }
                .show(requireFragmentManager())
        }
    }

    private fun getTitleError(e: ApiException): String {
        return when (e.code) {
            ApiException.NO_USER -> getString(R.string.LNG_BAD_CREDENTIALS_ERROR)
            ApiException.NOT_FOUND -> getString(
                R.string.LNG_ACCOUNT_NOTFOUND,
                /* TODO вот тут возможно придется просить поле из презентера.
                           Или всегда в метод передавать лишний параметр. */
                presenter.params.emailOrPhone
            )
            ApiException.NETWORK_ERROR -> getString(R.string.LNG_NETWORK_ERROR)
            else -> e.message ?: "Error"
        }
    }

    override fun setError(e: DatabaseException) {
        Sentry.getContext().recordBreadcrumb(BreadcrumbBuilder().setMessage("CacheError: ${e.details}").build())
        Sentry.capture(e)
    }

    override fun showLoading() {
        if (loadingFragment.isAdded) return
        fragmentManager?.beginTransaction()?.apply {
            replace(android.R.id.content, loadingFragment)
            commit()
        }
    }

    override fun hideLoading() {
        if (!loadingFragment.isAdded) return
        fragmentManager?.beginTransaction()?.apply {
            remove(loadingFragment)
            commit()
        }
    }
}
