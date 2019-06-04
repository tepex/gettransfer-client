package com.kg.gettransfer.presentation.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.arellomobile.mvp.MvpAppCompatFragment
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.kg.gettransfer.R
import com.kg.gettransfer.domain.ApiException
import com.kg.gettransfer.domain.DatabaseException
import com.kg.gettransfer.presentation.view.SignUpView
import com.kg.gettransfer.utilities.PhoneNumberFormatter
import kotlinx.android.synthetic.main.fragment_sign_up.*

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
        get() = loginPhoneTv.text.toString().replace(" ", "")

    private val name
        get() = loginNameTv.text.toString().trim()

    private val email
        get() = loginEmailTv.text.toString().trim()

    private val termsAccepted
        get() = switchAgreementTb.isChecked

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_sign_up, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initTextChangeListeners()
        initPhoneTextChangeListeners()
        btnLogin.setOnClickListener {
            presenter.registration(name, phone, email, termsAccepted)
        }
        licenseAgreementTv.setOnClickListener { presenter.showLicenceAgreement() }
    }

    override fun showValidationErrorDialog() {
        SignUpBottomSheetError
            .newInstance()
            .show(fragmentManager, SignUpBottomSheetError.TAG)
    }

    override fun showRegisterSuccessDialog() {
        SignUpBottomSheetSuccess
            .newInstance()
            .show(fragmentManager, SignUpBottomSheetSuccess.TAG)
    }

    private fun initTextChangeListeners() {
        loginNameTv.onTextChanged {
            btnLogin.isEnabled = checkAbleEnableButton()
        }

        loginPhoneTv.onTextChanged {
            btnLogin.isEnabled = checkAbleEnableButton()
        }

        loginEmailTv.onTextChanged {
            btnLogin.isEnabled = checkAbleEnableButton()
        }

        switchAgreementTb.setOnCheckedChangeListener { _, _ ->
            btnLogin.isEnabled = checkAbleEnableButton()
        }
    }

    private fun initPhoneTextChangeListeners() {
        loginPhoneTv.onTextChanged { text ->
            if (text.isEmpty() && loginPhoneTv.isFocused) {
                loginPhoneTv.setText("+")
                loginPhoneTv.setSelection(1)
            }
        }
        loginPhoneTv.addTextChangedListener(PhoneNumberFormatter())
        loginPhoneTv.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) phone.let {
                val phoneCode = Utils.getPhoneCodeByCountryIso(context!!)
                if (it.isEmpty()) {
                    loginPhoneTv.setText(if (phoneCode > 0) "+".plus(phoneCode) else "+")
                }
            }
            else phone.let {
                if (it.length <= 4) {
                    loginPhoneTv.setText("")
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
        //TODO remove BaseView or add code.
    }

    override fun setError(e: DatabaseException) {
        //TODO remove BaseView or add code.
    }
}