package com.kg.gettransfer.presentation.ui

import android.os.Bundle
import android.os.Parcel
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.arellomobile.mvp.MvpAppCompatFragment
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.kg.gettransfer.R
import com.kg.gettransfer.domain.model.Transfer
import com.kg.gettransfer.presentation.presenter.RequestsFragmentPresenter
import com.kg.gettransfer.presentation.view.RequestsFragmentView
import kotlinx.android.synthetic.main.fragment_requests.*

class RequestsFragment: MvpAppCompatFragment(), RequestsFragmentView{
    @InjectPresenter
    internal lateinit var presenter: RequestsFragmentPresenter

    @ProvidePresenter
    fun createRequestsFragmentPresenter(): RequestsFragmentPresenter = RequestsFragmentPresenter()

    private var bundleKey = ""
    private var distanceUnit = ""
    private var transfers = arrayListOf<Transfer>()

    companion object {
        @JvmField val BUNDLE_KEY    = "BundleKey"
        @JvmField val DISTANCE_UNIT = "DistanceUnit"
        @JvmField val TRANSFERS     = "Transfers"

        fun newInstance(transfers: ArrayList<Transfer>, distanceUnit: String, bundleKey: String): RequestsFragment {
            val fragment = RequestsFragment()
            val bundle = Bundle()
            bundle.putString(BUNDLE_KEY, bundleKey)
            bundle.putString(bundleKey + DISTANCE_UNIT, distanceUnit)
            bundle.putSerializable(bundleKey + TRANSFERS, transfers)
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val bundle = arguments!!
        bundle.getString(BUNDLE_KEY)?.let{bundleKey = it}
        bundle.getString(bundleKey + DISTANCE_UNIT)?.let{distanceUnit = it}
        bundle.getSerializable(bundleKey + TRANSFERS)?.let { transfers.addAll(it as ArrayList<Transfer>) }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_requests, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rvRequests.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        presenter.setData(transfers, distanceUnit)
    }

    override fun setRequests(transfers: List<Transfer>, distanceUnit: String) {
        rvRequests.adapter = RequestsRVAdapter(transfers, distanceUnit)
    }
}