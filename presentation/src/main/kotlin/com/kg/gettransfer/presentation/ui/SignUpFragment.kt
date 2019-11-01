package com.kg.gettransfer.presentation.ui

import android.os.Bundle

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import androidx.annotation.CallSuper

import moxy.MvpAppCompatFragment
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter

import com.kg.gettransfer.R
import com.kg.gettransfer.domain.ApiException
import com.kg.gettransfer.domain.DatabaseException

import com.kg.gettransfer.extensions.hideKeyboard
import com.kg.gettransfer.extensions.setThrottledClickListener

import com.kg.gettransfer.presentation.presenter.SignUpPresenter
import com.kg.gettransfer.presentation.view.SignUpView

import com.kg.gettransfer.utilities.PhoneNumberFormatter

import io.sentry.Sentry
import io.sentry.event.BreadcrumbBuilder

import kotlinx.android.synthetic.main.fragment_sign_up.*
import kotlinx.android.synthetic.main.view_input_account_field.view.*

import timber.log.Timber

/**
 * Fragment for user registration.
 *
 * @author П. Густокашин (Diwixis)
 */
@Suppress("TooManyFunctions")
class SignUpFragment : MvpAppCompatFragment(), SignUpView {
    @InjectPresenter
    internal lateinit var presenter: SignUpPresenter
    private val loadingFragment by lazy { LoadingFragment() }

    private lateinit var textError: String
    var detailText = ""
    var goToLogin = false

    var changePage: ((String?, Boolean?) -> Unit)? = null

    @ProvidePresenter
    fun createLoginPresenter() = SignUpPresenter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_sign_up, container, false)

    @Suppress("NestedBlockDepth")
    @CallSuper
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initTextChangeListeners()
        initPhoneTextChangeListeners()
        initClickListeners()
    }

    private fun initTextChangeListeners() {
        nameLayout.fieldText.onTextChanged { text ->
            presenter.name = text
            btnSignUp.isEnabled = presenter.isEnabledSignUpButton
        }

        phoneLayout.fieldText.onTextChanged { text ->
            presenter.phone = text
            btnSignUp.isEnabled = presenter.isEnabledSignUpButton
        }

        emailLayout.fieldText.onTextChanged { text ->
            presenter.email = text
            btnSignUp.isEnabled = presenter.isEnabledSignUpButton
        }

        switchAgreementTb.setOnCheckedChangeListener { _, isChecked ->
            presenter.termsAccepted = isChecked
            btnSignUp.isEnabled = presenter.isEnabledSignUpButton
        }
    }

    @Suppress("ComplexMethod")
    private fun initPhoneTextChangeListeners() {
        with(phoneLayout.fieldText) {
            onTextChanged { text ->
                if (text.isEmpty() && isFocused) {
                    setText("+")
                    setSelection(1)
                }
            }
            addTextChangedListener(PhoneNumberFormatter())
            setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus) {
                    phoneLayout.fieldText.text.toString().let { phone ->
                        context?.let { context ->
                            val phoneCode = Utils.getPhoneCodeByCountryIso(context)
                            if (phone.isEmpty()) setText(if (phoneCode > 0) "+$phoneCode" else "+")
                        }
                    }
                } else if (phoneLayout.fieldText.text.toString().length <= MIN_PHONE_LENGTH) setText("")
            }
        }
    }

    private fun initClickListeners() {
        btnSignUp.setThrottledClickListener(THROTTLED_DELAY) { v ->
            v.hideKeyboard()
            presenter.registration()
        }
        licenseAgreementTv.setThrottledClickListener {
            presenter.showLicenceAgreement()
        }
    }

    override fun showRegisterSuccessDialog() {
        BottomSheetDialog
            .newInstance()
            .apply {
                imageId = R.drawable.logo
                title = this@SignUpFragment.getString(R.string.LNG_REGISTERED)
                text = this@SignUpFragment.getString(R.string.LNG_TRANSFER_CREATE_HINT)
                buttonOkText = this@SignUpFragment.getString(R.string.LNG_MENU_SUBTITLE_NEW)
                onDismissCallBack = {
                    presenter.onBackCommandClick()
                }
                onClickOkButton = { presenter.onCreateTransferClick() }
            }
            .show(requireFragmentManager())
    }

    override fun updateTextPhone(phone: String) {
        phoneLayout.fieldText.setText(phone)
    }

    override fun updateTextEmail(email: String) {
        emailLayout.fieldText.setText(email)
    }

    // loading fragment

    override fun blockInterface(block: Boolean, useSpinner: Boolean) {
        if (block) {
            if (useSpinner) showLoading()
        } else hideLoading()
    }

    private fun showLoading() {
        if (!loadingFragment.isAdded) {
            fragmentManager?.beginTransaction()?.apply {
                replace(android.R.id.content, loadingFragment)
                commit()
            }
        }
    }

    private fun hideLoading() {
        if (loadingFragment.isAdded) {
            fragmentManager?.beginTransaction()?.apply {
                remove(loadingFragment)
                commit()
            }
        }
    }

    // errors

    override fun showValidationErrorDialog(phoneExample: String) {
        BottomSheetDialog
            .newInstance()
            .apply {
                title = this@SignUpFragment.getString(R.string.LNG_ERROR_CREDENTIALS)
                text = this@SignUpFragment.getString(R.string.LNG_ERROR_EMAIL_PHONE, phoneExample)
            }
            .show(requireFragmentManager())
    }

    override fun setError(e: ApiException) {
        Timber.e("code: ${e.code}")
        Sentry.getContext().recordBreadcrumb(BreadcrumbBuilder().setMessage(e.details).build())
        Sentry.capture(e)
        textError = e.message ?: getString(R.string.LNG_ERROR)

        getErrorType(e)
        getErrorCode(e)
        showDialog()
    }

    private fun showDialog() {
        BottomSheetDialog
            .newInstance()
            .apply {
                title = textError
                text = detailText
                if (goToLogin) {
                    onClickOkButton = { changePage?.invoke(null, null) }
                    buttonOkText = this@SignUpFragment.getString(R.string.LNG_LOGIN_BUTTON)
                }
                isShowCloseButton = true
            }
            .show(requireFragmentManager())
    }

    private fun getErrorCode(e: ApiException) {
        when (e.code) {
            ApiException.TOO_MANY_REQUESTS -> textError = getString(R.string.LNG_ERROR_RATE_LIMIT)
            ApiException.NETWORK_ERROR -> textError = getString(R.string.LNG_NETWORK_ERROR)
        }
    }

    private fun getErrorType(e: ApiException) {
        when (e.type) {
            ApiException.TYPE_EMAIL_TAKEN -> setTextIfAccountExist()
            ApiException.TYPE_PHONE_TAKEN -> setTextIfAccountExist()
            ApiException.TYPE_EMAIL_INVALID -> textError = getString(R.string.LNG_ERROR_EMAIL)
            ApiException.TYPE_PHONE_INVALID -> textError = getString(R.string.LNG_ERROR_PHONE)
            ApiException.TYPE_PHONE_UNPROCESSABLE -> textError = getString(R.string.LNG_UNPROCESSABLE_ERROR)
        }
    }

    private fun setTextIfAccountExist() {
        goToLogin = true
        textError = getString(R.string.LNG_ACCOUNT_EXISTS_ERROR)
        detailText = getString(R.string.LNG_LOGIN_REQUIRED)
    }

    override fun setError(finish: Boolean, errId: Int, vararg args: String?) {}

    override fun setError(e: DatabaseException) {}

    override fun setTransferNotFoundError(transferId: Long, dismissCallBack: (() -> Unit)?) {}

    companion object {
        const val MIN_PHONE_LENGTH = 4
        const val THROTTLED_DELAY = 1_000L

        fun newInstance() = SignUpFragment()
    }

    override fun onDestroy() {
        super.onDestroy()
//        AppWatcher.objectWatcher.watch(this)
    }
}
