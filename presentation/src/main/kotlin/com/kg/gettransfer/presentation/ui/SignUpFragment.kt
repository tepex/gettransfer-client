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

    private val phone
        get() = phoneLayout.fieldText.text.toString().replace(" ", "")

    private val name
        get() = nameLayout.fieldText.text.toString().trim()

    private val email
        get() = emailLayout.fieldText.text.toString().trim()

    private val termsAccepted
        get() = switchAgreementTb.isChecked

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_sign_up, container, false)

    @CallSuper
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initTextChangeListeners()
        initPhoneTextChangeListeners()
        btnLogin.setThrottledClickListener(1000L) {
            presenter.registration(name, phone, email, termsAccepted)
        }
        licenseAgreementTv.setThrottledClickListener { presenter.showLicenceAgreement() }
    }

    override fun showValidationErrorDialog(phoneExample: String) {
        BottomSheetDialog
            .newInstance()
            .apply {
                title = this@SignUpFragment.getString(R.string.LNG_ERROR_CREDENTIALS)
                text = this@SignUpFragment.getString(R.string.LNG_ERROR_EMAIL_PHONE, phoneExample)
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
                onDismissCallBack = { presenter.onBackCommandClick() }
            }
            .show(fragmentManager)
    }

    private fun initTextChangeListeners() {
        nameLayout.fieldText.onTextChanged {
            btnLogin.isEnabled = checkAbleEnableButton()
        }

        phoneLayout.fieldText.onTextChanged {
            btnLogin.isEnabled = checkAbleEnableButton()
        }

        emailLayout.fieldText.onTextChanged {
            btnLogin.isEnabled = checkAbleEnableButton()
        }

        switchAgreementTb.setOnCheckedChangeListener { _, _ ->
            btnLogin.isEnabled = checkAbleEnableButton()
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
                if (hasFocus) phone.let {
                    val phoneCode = Utils.getPhoneCodeByCountryIso(context!!)
                    if (it.isEmpty()) {
                        setText(if (phoneCode > 0) "+".plus(phoneCode) else "+")
                    }
                }
                else phone.let {
                    if (it.length <= 4) {
                        setText("")
                    }
                }
            }
        }
    }

    private fun checkAbleEnableButton(): Boolean =
        presenter.checkFieldsIsEmpty(fieldValues = listOf(name, phone, email)) && termsAccepted

    companion object {
        fun newInstance() = SignUpFragment()
    }

    //---- Shit from base classes ------

    override fun blockInterface(block: Boolean, useSpinner: Boolean) {
        if (block) {
            if (useSpinner) showLoading()
        } else hideLoading()
    }

    private fun showLoading() {
        if (loadingFragment.isAdded) return
        fragmentManager?.beginTransaction()?.apply {
            replace(R.id.container, loadingFragment)
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
            .apply { title = textError }
            .show(fragmentManager)
    }

    override fun setError(e: DatabaseException) {
        //TODO remove BaseView or add code.
    }

    override fun setTransferNotFoundError(transferId: Long) {
        //TODO remove BaseView or add code.
    }
}
