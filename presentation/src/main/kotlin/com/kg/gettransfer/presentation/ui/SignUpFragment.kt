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
        get() = loginPhoneTv.text.toString()

    private val name
        get() = loginNameTv.text.toString()

    private val email
        get() = loginEmailTv.text.toString()

    private val termsAccepted
        get() = switchAgreementTb.isChecked

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_sign_up, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initTextChangeListeners()
        btnLogin.setOnClickListener {
            if (checkFieldsIsValid()) presenter.registration(name, phone, email, termsAccepted)
            else showValidationErrorDialog()
        }
    }

    private fun showValidationErrorDialog() {
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

    private fun checkFieldsIsValid(): Boolean = presenter.checkFieldsIsValid(phone = phone, email = email)

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
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setError(e: ApiException) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setError(e: DatabaseException) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}