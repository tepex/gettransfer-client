package com.kg.gettransfer.presentation.ui

import android.os.Bundle

import android.support.v7.widget.LinearLayoutManager

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.arellomobile.mvp.MvpAppCompatFragment
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter

import com.kg.gettransfer.R
import com.kg.gettransfer.domain.CoroutineContexts
import com.kg.gettransfer.domain.interactor.ApiInteractor
import com.kg.gettransfer.domain.model.Transfer

import com.kg.gettransfer.presentation.adapter.RequestsRVAdapter
import com.kg.gettransfer.presentation.presenter.RequestsFragmentPresenter
import com.kg.gettransfer.presentation.view.RequestsFragmentView

import kotlinx.android.synthetic.main.fragment_requests.*

import org.koin.android.ext.android.inject

class RequestsFragment: MvpAppCompatFragment(), RequestsFragmentView{
    @InjectPresenter
    internal lateinit var presenter: RequestsFragmentPresenter

    private val apiInteractor: ApiInteractor by inject()
    private val coroutineContexts: CoroutineContexts by inject()

    @ProvidePresenter
    fun createRequestsFragmentPresenter(): RequestsFragmentPresenter = RequestsFragmentPresenter(coroutineContexts, apiInteractor)

    private var categoryName = ""

    companion object {
        @JvmField val CATEGORY   = "category"

        fun newInstance(categoryName: String): RequestsFragment {
            val fragment = RequestsFragment()
            val bundle = Bundle()
            bundle.putString(CATEGORY, categoryName)
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val bundle = arguments!!
        bundle.getString(CATEGORY)?.let{categoryName = it}
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_requests, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rvRequests.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        presenter.setData(categoryName)
    }

    override fun setRequests(transfers: List<Transfer>, distanceUnit: String) {
        rvRequests.adapter = RequestsRVAdapter(transfers, distanceUnit)
    }
}
