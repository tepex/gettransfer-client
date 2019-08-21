package com.kg.gettransfer.presentation.ui

import android.os.Bundle

import androidx.annotation.CallSuper

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.arellomobile.mvp.MvpAppCompatFragment
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter

import com.kg.gettransfer.R
import com.kg.gettransfer.domain.ApiException
import com.kg.gettransfer.domain.DatabaseException

import com.kg.gettransfer.presentation.presenter.SignUpCarrierPresenter
import com.kg.gettransfer.presentation.view.SignUpCarrierView
import kotlinx.android.synthetic.main.fragment_carrier_sign_up.*
//import leakcanary.AppWatcher

/**
 * Fragment for carrier registration.
 *
 * @author П. Густокашин (Diwixis)
 */
class SignUpCarrierFragment : MvpAppCompatFragment(), SignUpCarrierView {

    @InjectPresenter
    internal lateinit var presenter: SignUpCarrierPresenter

    @ProvidePresenter
    fun createSignUpCarrierPresenter() = SignUpCarrierPresenter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_carrier_sign_up, container, false)

    @CallSuper
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnLogin.setOnClickListener {
            presenter.registration()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
//        AppWatcher.objectWatcher.watch(this)
    }

    companion object {
        fun newInstance() = SignUpCarrierFragment()
    }

    // -----Plz, delete "Base" classes------
    override fun blockInterface(block: Boolean, useSpinner: Boolean) {}
    override fun setError(finish: Boolean, errId: Int, vararg args: String?) {}
    override fun setError(e: ApiException) {}
    override fun setError(e: DatabaseException) {}

    // To change body of created functions use File | Settings | File Templates.
    override fun setTransferNotFoundError(transferId: Long) {}
}
