package com.kg.gettransfer.presentation.ui

import android.content.Context
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
import com.kg.gettransfer.domain.DatabaseException
import com.kg.gettransfer.extensions.isVisible

import com.kg.gettransfer.presentation.adapter.RequestsRVAdapter
import com.kg.gettransfer.presentation.model.TransferModel
import com.kg.gettransfer.presentation.presenter.RequestsFragmentPresenter

import com.kg.gettransfer.presentation.view.BaseView
import com.kg.gettransfer.presentation.view.RequestsFragmentView
import com.kg.gettransfer.presentation.view.RequestsView

import kotlinx.android.synthetic.main.fragment_requests.*

import timber.log.Timber
import android.support.v7.widget.RecyclerView



/**
 * @TODO: Выделить BaseFragment
 */
class RequestsFragment: MvpAppCompatFragment(), RequestsFragmentView {
    @InjectPresenter
    internal lateinit var presenter: RequestsFragmentPresenter

    @ProvidePresenter
    fun createRequestsFragmentPresenter() = RequestsFragmentPresenter(arguments!!.getInt(TRANSFER_TYPE_ARG))

    private var rvAdapter: RequestsRVAdapter? = null

    var networkAvailable:Boolean? = null

    companion object {
        @JvmField val TRANSFER_TYPE_ARG = "TRANSFER_TYPE_ARG"

        fun newInstance(@RequestsView.TransferTypeAnnotation categoryName: Int) = RequestsFragment().apply {
            arguments = Bundle().apply { putInt(TRANSFER_TYPE_ARG, categoryName) }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
        inflater.inflate(R.layout.fragment_requests, container, false)

    @CallSuper
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val transferName = when(presenter.transferType) {
            RequestsView.TransferTypeAnnotation.TRANSFER_ACTIVE -> getString(R.string.LNG_TRIPS_EMPTY_ACTIVE)
            RequestsView.TransferTypeAnnotation.TRANSFER_ARCHIVE -> getString(R.string.LNG_TRIPS_EMPTY_COMPLETED)
            else -> throw UnsupportedOperationException()
        }

        noTransfersText.text = transferName
        rvRequests.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        val layout = when(presenter.transferType) {
            RequestsView.TransferTypeAnnotation.TRANSFER_ACTIVE -> R.layout.view_transfer_request_info_enabled
            RequestsView.TransferTypeAnnotation.TRANSFER_ARCHIVE -> R.layout.view_transfer_request_info_disabled
            else -> throw UnsupportedOperationException()
        }

        rvAdapter = RequestsRVAdapter(layout) { presenter.openTransferDetails(it.id, it.status, it.paidPercentage) }
        rvRequests.adapter = rvAdapter
    }

    override fun setScrollListener() {
        rvRequests.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if(!rvRequests.canScrollVertically(1)){
                    presenter.updateTransfersSuspend()
                }
            }
        })
    }

    override fun updateTransfers(transfers: List<TransferModel>) {
        noTransfersText.isVisible = transfers.isEmpty()
        rvAdapter?.updateTransfers(transfers)
    }

    override fun blockInterface(block: Boolean, useSpinner: Boolean) =
        (activity as BaseView).blockInterface(block, useSpinner)

    override fun setError(finish: Boolean, @StringRes errId: Int, vararg args: String?) =
        (activity as BaseView).setError(finish, errId, *args)

    override fun setError(e: ApiException) {
        Timber.e("code: ${e.code}", e)
        if(e.code != ApiException.NETWORK_ERROR) Utils.showError(context!!, false, getString(R.string.err_server, e.message))
    }

    override fun setError(e: DatabaseException) =
            (activity as BaseView).setError(e)

    override fun notifyData() {
        activity?.runOnUiThread { rvAdapter?.notifyDataSetChanged() }
    }

    override fun showTransfers(){
        if(::presenter.isInitialized){
            presenter.getTransfers(networkAvailable)
        }
    }

    fun setNetworkAvailability(available: Boolean){
        if(networkAvailable != available) {
            networkAvailable = available
            showTransfers()
        }
    }
}
