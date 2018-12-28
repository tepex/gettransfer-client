package com.kg.gettransfer.presentation.ui

import android.content.Intent

import android.os.Bundle

import android.support.annotation.CallSuper

import android.support.v7.widget.Toolbar

import android.view.View

import android.widget.ScrollView

import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter

import com.kg.gettransfer.R
import com.kg.gettransfer.domain.model.Offer
import com.kg.gettransfer.extensions.*
import com.kg.gettransfer.presentation.model.OfferModel
import com.kg.gettransfer.presentation.presenter.LogsPresenter
import com.kg.gettransfer.presentation.view.LogsView

import kotlinx.android.synthetic.main.activity_share_logs.*
import kotlinx.android.synthetic.main.toolbar.view.*

class LogsActivity : BaseActivity(), LogsView {
    @InjectPresenter
    internal lateinit var presenter: LogsPresenter

    @ProvidePresenter
    fun createShareLogsPresenter() = LogsPresenter()

    override fun getPresenter(): LogsPresenter = presenter

    @CallSuper
    protected override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_share_logs)

        setToolbar(toolbar as Toolbar)
        with(toolbar as Toolbar) {
            layoutLogs.isVisible = true
            btnShare.setOnClickListener { presenter.onShareClicked() }
            btnClear.setOnClickListener { presenter.clearLogs() }
        }
    }

    override fun setLogs(logs: String) {
        textLogs.text = logs
        scrollView.post { scrollView.fullScroll(ScrollView.FOCUS_DOWN) }
    }
}
