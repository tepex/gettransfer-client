package com.kg.gettransfer.presentation.ui

import android.os.Bundle

import androidx.annotation.CallSuper
import androidx.annotation.StringRes

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.arellomobile.mvp.MvpAppCompatFragment

import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter

import com.kg.gettransfer.R

import com.kg.gettransfer.domain.ApiException
import com.kg.gettransfer.domain.DatabaseException
import com.kg.gettransfer.extensions.isVisible
import com.kg.gettransfer.presentation.adapter.BtnCallClickListener
import com.kg.gettransfer.presentation.adapter.BtnChatClickListener
import com.kg.gettransfer.presentation.adapter.ItemClickListener

import com.kg.gettransfer.presentation.adapter.RequestsRVAdapter
import com.kg.gettransfer.presentation.model.TransferModel
import com.kg.gettransfer.presentation.presenter.RequestsCategoryPresenter

import com.kg.gettransfer.presentation.view.BaseView
import com.kg.gettransfer.presentation.view.RequestsFragmentView
import com.kg.gettransfer.presentation.view.RequestsView
import com.kg.gettransfer.utilities.EndlessRecyclerViewScrollListener

import kotlinx.android.synthetic.main.fragment_requests.*

import timber.log.Timber
import kotlinx.android.synthetic.main.view_shimmer_loader.view.*
//import leakcanary.AppWatcher

/**
 * @TODO: Выделить BaseFragment
 */
class RequestsFragment: MvpAppCompatFragment(), RequestsFragmentView {
    @InjectPresenter
    internal lateinit var presenter: RequestsCategoryPresenter

    @ProvidePresenter
    fun createRequestsCategoryPresenter() = RequestsCategoryPresenter(arguments!!.getInt(TRANSFER_TYPE_ARG))

    private val rvAdapter: RequestsRVAdapter
    get() = rvRequests.adapter as RequestsRVAdapter

    private lateinit var scrollListener: EndlessRecyclerViewScrollListener

    companion object {
        @JvmField val TRANSFER_TYPE_ARG = "TRANSFER_TYPE_ARG"

        fun newInstance(@RequestsView.TransferTypeAnnotation categoryName: Int) = RequestsFragment().apply {
            arguments = Bundle().apply { putInt(TRANSFER_TYPE_ARG, categoryName) }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        inflater.inflate(R.layout.fragment_requests, container, false)

    @CallSuper
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setTitleFragmentEmptyRequestsList()
        rvRequests.adapter = RequestsRVAdapter(presenter.transferType, onItemClickListener, onCallClickListener, onChatClickListener)
        initClickListeners()
        initScrollListener()
    }

    private val onItemClickListener: ItemClickListener = { presenter.openTransferDetails(it.id, it.status, it.paidPercentage, it.pendingPaymentId) }

    private val onCallClickListener: BtnCallClickListener = { presenter.callPhone(it) }

    private val onChatClickListener: BtnChatClickListener = { presenter.onChatClick(it) }

    private fun setTitleFragmentEmptyRequestsList() {
        noTransfersText.text = when(presenter.transferType) {
            RequestsView.TransferTypeAnnotation.TRANSFER_ACTIVE -> getString(R.string.LNG_TRIPS_EMPTY_ACTIVE)
            RequestsView.TransferTypeAnnotation.TRANSFER_ARCHIVE -> getString(R.string.LNG_TRIPS_EMPTY_COMPLETED)
            else -> throw UnsupportedOperationException()
        }
    }

    private fun initClickListeners() {
        swipe_container.setOnRefreshListener {
            scrollListener.resetState()
            presenter.getTransfers()
        }

        btn_forward_main.setOnClickListener {
            presenter.onGetBookClicked()
        }
    }

    private fun initScrollListener() {
        val layoutManager = rvRequests.layoutManager as LinearLayoutManager

        scrollListener = object : EndlessRecyclerViewScrollListener(layoutManager) {

            override fun onLoadMore(page: Int) {
                presenter.getTransfers(page)
            }
        }
        rvRequests.addOnScrollListener(scrollListener)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        rvRequests.removeOnScrollListener(scrollListener)
    }

    override fun onDestroy() {
        super.onDestroy()
//        AppWatcher.objectWatcher.watch(this)
    }

    override fun updateTransfers(transfers: List<TransferModel>, pagesCount: Int?) {
        swipe_container.isRefreshing = false

        switchBackGroundData(false)
        rvAdapter.updateTransfers(transfers, false)
        scrollListener.setLoaded()
        pagesCount?.let { scrollListener.pages = it }
    }

    override fun updateCardWithDriverCoordinates(transferId: Long) {
        activity?.runOnUiThread { rvAdapter.updateDriverCoordinates(transferId) }
    }

    override fun updateEvents(eventsCount: Map<Long, Int>) {
        rvAdapter.updateEvents(eventsCount)
    }

    override fun onEmptyList() {
        swipe_container.isRefreshing = false

        switchBackGroundData(true)
    }

    private fun switchBackGroundData(isEmpty: Boolean) {
        rvRequests.isVisible       = !isEmpty
        iv_no_transfers.isVisible  = isEmpty
        noTransfersText.isVisible  = isEmpty
        btn_forward_main.isVisible = isEmpty
    }

    override fun blockInterface(block: Boolean, useSpinner: Boolean) {
        transfers_loader.isVisible = block
        if (block)
            transfers_loader.shimmer_loader.startShimmer()
        else transfers_loader.shimmer_loader.stopShimmer()

    }

    override fun setError(finish: Boolean, @StringRes errId: Int, vararg args: String?) =
        (activity as BaseView).setError(finish, errId, *args)

    override fun setError(e: ApiException) {
        Timber.e("code: ${e.code}")
        if(e.code != ApiException.NETWORK_ERROR) Utils.showError(context!!, false, "${getString(R.string.LNG_ERROR)}: ${e.message}")
    }

    override fun setError(e: DatabaseException) =
        (activity as BaseView).setError(e)

    override fun setTransferNotFoundError(transferId: Long) {}
}
