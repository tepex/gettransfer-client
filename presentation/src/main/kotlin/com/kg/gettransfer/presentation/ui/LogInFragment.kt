package com.kg.gettransfer.presentation.ui

import android.os.Bundle
import android.support.annotation.CallSuper
import android.support.annotation.StringRes
import android.support.v4.app.Fragment
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

import io.sentry.Sentry
import io.sentry.event.BreadcrumbBuilder

import kotlinx.android.synthetic.main.fragment_log_in.*
import kotlinx.android.synthetic.main.view_input_account_field.view.*
import kotlinx.android.synthetic.main.view_input_password.*
import kotlinx.serialization.json.JSON

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

    private fun initClickListeners() {
        btnLogin.setThrottledClickListener {
            showLoading()
            presenter.onLoginClick()
        }
        btnRequestCode.setThrottledClickListener {
            showLoading()
            presenter.sendVerificationCode()
        }
    }

    private fun initTextChangeListeners() {
        emailLayout.fieldText.onTextChanged {
            presenter.setEmailOrPhone(it)
            btnLogin.isEnabled = presenter.isEnabledButtonLogin
            btnRequestCode.isEnabled = presenter.isEnabledRequestCodeButton
        }
        etPassword.onTextChanged {
            presenter.password = it
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
                    .apply {
                        title = this@LogInFragment.getString(R.string.LNG_BAD_CREDENTIALS_ERROR)
                        onDismissCallBack = { hideLoading() }
                    }
                    .show(fragmentManager)
            }
            ApiException.NOT_FOUND -> {
                BottomSheetDialog
                    .newInstance()
                    .apply {
                        title =
                            this@LogInFragment.getString(
                                R.string.LNG_ACCOUNT_NOTFOUND,
                                presenter.params.emailOrPhone //TODO вот тут возможно придется просить поле из презентера. Или всегда в метожд передавать лишний параметр.
                            )
                        text = this@LogInFragment.getString(R.string.LNG_ERROR_ACCOUNT_CREATE_USER)
                        buttonOkText = this@LogInFragment.getString(R.string.LNG_SIGNUP)
                        onClickOkButton = { changePage?.invoke(presenter.params.emailOrPhone, presenter.isPhone()) }
                        onDismissCallBack = { hideLoading() }
                        isShowCloseButton = true
                    }
                    .show(fragmentManager)
            }
            else -> {
                BottomSheetDialog
                    .newInstance()
                    .apply {
                        title = e.message ?: "Error"
                        onDismissCallBack = { hideLoading() }
                    }
                    .show(fragmentManager)
            }
        }
    }

    override fun setError(e: DatabaseException) {
        Sentry.getContext().recordBreadcrumb(BreadcrumbBuilder().setMessage("CacheError: ${e.details}").build())
        Sentry.capture(e)
    }

    override fun showLoading() {
        if (loadingFragment.isAdded) return
        fragmentManager?.beginTransaction()?.apply {
            replace(R.id.container, loadingFragment)
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
