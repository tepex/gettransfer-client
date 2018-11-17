package com.kg.gettransfer.presentation.ui

import android.os.Bundle

import android.support.annotation.CallSuper
import android.support.annotation.StringRes

import android.support.v7.widget.LinearLayoutManager

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.arellomobile.mvp.MvpAppCompatFragment

import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter

import com.kg.gettransfer.R

import com.kg.gettransfer.domain.ApiException

import com.kg.gettransfer.presentation.adapter.RequestsRVAdapter
import com.kg.gettransfer.presentation.model.TransferModel
import com.kg.gettransfer.presentation.presenter.RequestsFragmentPresenter

import com.kg.gettransfer.presentation.view.BaseView
import com.kg.gettransfer.presentation.view.RequestsFragmentView

import kotlinx.android.synthetic.main.fragment_requests.*

import timber.log.Timber

/**
 * @TODO: Выделить BaseFragment
 */
class RequestsFragment: MvpAppCompatFragment(), RequestsFragmentView {
    @InjectPresenter
    internal lateinit var presenter: RequestsFragmentPresenter

    private lateinit var categoryName: String

    @ProvidePresenter
    fun createRequestsFragmentPresenter() = RequestsFragmentPresenter()

    companion object {
        @JvmField val CATEGORY = "category"

        fun newInstance(categoryName: String) = RequestsFragment().apply {
            arguments = Bundle().apply { putString(CATEGORY, categoryName) }
        }
    }

    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {
        arguments!!.getString(CATEGORY)?.let { categoryName = it }
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
        inflater.inflate(R.layout.fragment_requests, container, false)

    @CallSuper
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rvRequests.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
    }
    
    override fun setRequests(transfers: List<TransferModel>) {
        rvRequests.adapter = RequestsRVAdapter(transfers) { transfer -> presenter.openTransferDetails(transfer.id, transfer.status) }
    }

    override fun blockInterface(block: Boolean, useSpinner: Boolean) { (activity as BaseView).blockInterface(block, useSpinner) }

    override fun setError(finish: Boolean, @StringRes errId: Int, vararg args: String?) {
        (activity as BaseView).setError(finish, errId, *args)
    }
    
    override fun setError(e: ApiException) {
        Timber.e("code: ${e.code}", e)
        Utils.showError(context!!, false, getString(R.string.err_server, e.message))
    }
}
