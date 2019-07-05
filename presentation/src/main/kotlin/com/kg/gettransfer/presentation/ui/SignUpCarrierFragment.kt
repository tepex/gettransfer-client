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

import com.kg.gettransfer.presentation.presenter.SignUpCarrierPresenter
import com.kg.gettransfer.presentation.view.SignUpCarrierView

import kotlinx.android.synthetic.main.fragment_sign_up.*

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

    @CallSuper
    override fun onDetach() {
        super.onDetach()

        try {
            val childFragmentManager = Fragment::class.java.getDeclaredField("mChildFragmentManager")
            childFragmentManager.isAccessible = true
            childFragmentManager.set(this, null)

        } catch (e: NoSuchFieldException) {
            throw RuntimeException(e)
        } catch (e: IllegalAccessException) {
            throw RuntimeException(e)
        }
    }

    companion object {
        fun newInstance() = SignUpCarrierFragment()
    }

    //-----Plz, delete "Base" classes------
    override fun blockInterface(block: Boolean, useSpinner: Boolean) {
        //TODO remove BaseView or add code.
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

    override fun setTransferNotFoundError(transferId: Long) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
