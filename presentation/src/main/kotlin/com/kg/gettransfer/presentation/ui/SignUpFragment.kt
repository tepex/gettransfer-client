package com.kg.gettransfer.presentation.ui

import android.os.Bundle
import android.support.annotation.CallSuper

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
        btnLogin.setThrottledClickListener(1000L) {
            showLoading()
            presenter.registration()
        }
        licenseAgreementTv.setThrottledClickListener {
            showLoading()
            presenter.showLicenceAgreement()
        }
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
        nameLayout.fieldText.onTextChanged {
            presenter.name = it
            btnLogin.isEnabled = presenter.checkFieldsIsEmpty()
        }

        phoneLayout.fieldText.onTextChanged {
            presenter.phone = it
            btnLogin.isEnabled = presenter.checkFieldsIsEmpty()
        }

        emailLayout.fieldText.onTextChanged {
            presenter.email = it
            btnLogin.isEnabled = presenter.checkFieldsIsEmpty()
        }

        switchAgreementTb.setOnCheckedChangeListener { _, isChecked ->
            presenter.termsAccepted = isChecked
            btnLogin.isEnabled = presenter.checkFieldsIsEmpty()
        }
    }

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
                if (hasFocus) phoneLayout.fieldText.text.toString().let {
                    val phoneCode = Utils.getPhoneCodeByCountryIso(context!!)
                    if (it.isEmpty()) {
                        setText(if (phoneCode > 0) "+".plus(phoneCode) else "+")
                    }
                }
                else phoneLayout.fieldText.text.toString().let {
                    if (it.length <= 4) {
                        setText("")
                    }
                }
            }
        }
    }

    override fun updateTextPhone(phone: String) {
        phoneLayout.fieldText.setText(phone)
    }

    override fun updateTextEmail(email: String) {
        emailLayout.fieldText.setText(email)
    }

    companion object {
        fun newInstance() = SignUpFragment()
    }

    //---- Shit from base classes ------

    override fun blockInterface(block: Boolean, useSpinner: Boolean) {
        if (block) {
            if (useSpinner) showLoading()
        } else hideLoading()
    }

    override fun showLoading() {
        if (loadingFragment.isAdded) return
        fragmentManager?.beginTransaction()?.apply {
            add(R.id.container, loadingFragment)
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

    override fun setError(finish: Boolean, errId: Int, vararg args: String?) {
        //TODO remove BaseView or add code.
    }

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

    override fun setError(e: DatabaseException) {
        //TODO remove BaseView or add code.
    }

    override fun setTransferNotFoundError(transferId: Long) {
        //TODO remove BaseView or add code.
    }
}
