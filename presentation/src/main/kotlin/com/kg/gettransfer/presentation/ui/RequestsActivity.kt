package com.kg.gettransfer.presentation.ui

import android.os.Bundle
import android.support.annotation.CallSuper
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.Toolbar
import com.arellomobile.mvp.MvpAppCompatActivity
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.kg.gettransfer.R
import com.kg.gettransfer.domain.CoroutineContexts
import com.kg.gettransfer.domain.interactor.ApiInteractor
import com.kg.gettransfer.domain.model.Transfer
import com.kg.gettransfer.presentation.presenter.RequestsPresenter
import com.kg.gettransfer.presentation.view.RequestsView
import kotlinx.android.synthetic.main.activity_requests.*
import kotlinx.android.synthetic.main.toolbar.view.*
import org.koin.android.ext.android.inject

class RequestsActivity: MvpAppCompatActivity(), RequestsView {
    @InjectPresenter
    internal lateinit var presenter: RequestsPresenter

    private val apiInteractor: ApiInteractor by inject()
    private val coroutineContexts: CoroutineContexts by inject()

    @ProvidePresenter
    fun createRequestsPresenter(): RequestsPresenter = RequestsPresenter(coroutineContexts, apiInteractor)

    @CallSuper
    protected override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_requests)

        setSupportActionBar(toolbar as Toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        (toolbar as Toolbar).toolbar_title.setText(R.string.text_nav_requests_title)
        (toolbar as Toolbar).setNavigationOnClickListener { presenter.onBackCommandClick() }

        rvRequests.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
    }

    override fun setRequests(transfers: List<Transfer>, distanceUnit: String) {
        rvRequests.adapter = RequestsRVAdapter(transfers, distanceUnit)
    }
}