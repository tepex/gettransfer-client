package com.kg.gettransfer.presentation.ui

import android.os.Bundle
import android.support.annotation.CallSuper
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

    @ProvidePresenter
    fun createLoginPresenter() = SignUpPresenter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_sign_up, container, false)

    @CallSuper
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initTextChangeListeners()
        initPhoneTextChangeListeners()
        @Suppress("MagicNumber")
        btnLogin.setThrottledClickListener(1_000L) {
            showLoading()
            presenter.registration()
        }
        licenseAgreementTv.setThrottledClickListener {
            showLoading()
            presenter.showLicenceAgreement()
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
            @Suppress("TooGenericExceptionThrown")
            throw RuntimeException(e)
        } catch (e: IllegalAccessException) {
            @Suppress("TooGenericExceptionThrown")
            throw RuntimeException(e)
        }
        super.onDetach()
    }

    override fun showValidationErrorDialog(phoneExample: String) {
        BottomSheetDialog
            .newInstance()
            .apply {
                title = this@SignUpFragment.getString(R.string.LNG_ERROR_CREDENTIALS)
                text = this@SignUpFragment.getString(R.string.LNG_ERROR_EMAIL_PHONE, phoneExample)
                onDismissCallBack = { hideLoading() }
            }
            .show(fragmentManager)
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
                    hideLoading()
                }
            }
            .show(fragmentManager)
    }

    private fun initTextChangeListeners() {
        nameLayout.fieldText.onTextChanged { text ->
            presenter.name = text
            btnLogin.isEnabled = presenter.checkFieldsIsEmpty()
        }

        phoneLayout.fieldText.onTextChanged { text ->
            presenter.phone = text
            btnLogin.isEnabled = presenter.checkFieldsIsEmpty()
        }

        emailLayout.fieldText.onTextChanged { text ->
            presenter.email = text
            btnLogin.isEnabled = presenter.checkFieldsIsEmpty()
        }

        switchAgreementTb.setOnCheckedChangeListener { _, isChecked ->
            presenter.termsAccepted = isChecked
            btnLogin.isEnabled = presenter.checkFieldsIsEmpty()
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

    override fun updateTextPhone(phone: String) {
        phoneLayout.fieldText.setText(phone)
    }

    override fun updateTextEmail(email: String) {
        emailLayout.fieldText.setText(email)
    }

    // ---- Shit from base classes ------

    override fun blockInterface(block: Boolean, useSpinner: Boolean) {
        if (block) {
            if (useSpinner) showLoading()
        } else hideLoading()
    }

    override fun showLoading() {
        if (!loadingFragment.isAdded) {
            fragmentManager?.beginTransaction()?.apply {
                replace(android.R.id.content, loadingFragment)
                commit()
            }
        }
    }

    override fun hideLoading() {
        if (loadingFragment.isAdded) {
            fragmentManager?.beginTransaction()?.apply {
                remove(loadingFragment)
                commit()
            }
        }
    }

    // TODO remove BaseView or add code.
    override fun setError(finish: Boolean, errId: Int, vararg args: String?) {}

    override fun setError(e: ApiException) {
        Timber.e("code: ${e.code}")
        Sentry.getContext().recordBreadcrumb(BreadcrumbBuilder().setMessage(e.details).build())
        Sentry.capture(e)
        var textError = e.message ?: "Error"
        when (e.code) {
            ApiException.UNPROCESSABLE -> textError = getString(R.string.LNG_UNPROCESSABLE_ERROR)
        }
        BottomSheetDialog
            .newInstance()
            .apply {
                title = textError
                onDismissCallBack = { hideLoading() }
            }
            .show(fragmentManager)
    }

    // TODO remove BaseView or add code.
    override fun setError(e: DatabaseException) {}

    // TODO remove BaseView or add code.
    override fun setTransferNotFoundError(transferId: Long) {}

    companion object {
        const val MIN_PHONE_LENGTH = 4

        fun newInstance() = SignUpFragment()
    }
}
