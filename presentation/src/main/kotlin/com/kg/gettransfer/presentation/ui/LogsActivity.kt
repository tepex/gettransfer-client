package com.kg.gettransfer.presentation.ui

import android.content.Intent

import android.os.Bundle

import android.support.annotation.CallSuper

import android.support.v4.content.FileProvider
import android.support.v7.widget.Toolbar

import android.view.View

import android.widget.ScrollView

import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter

import com.kg.gettransfer.R
import com.kg.gettransfer.presentation.presenter.LogsPresenter
import com.kg.gettransfer.presentation.view.LogsView

import java.io.File

import kotlinx.android.synthetic.main.activity_share_logs.*
import kotlinx.android.synthetic.main.toolbar.view.*

class LogsActivity: BaseActivity(), LogsView {
    @InjectPresenter
    internal lateinit var presenter: LogsPresenter

    @ProvidePresenter
    fun createShareLogsPresenter() = LogsPresenter(router, systemInteractor)

    override fun getPresenter(): LogsPresenter = presenter

    protected override var navigator = object: BaseNavigator(this) {}

    @CallSuper
    protected override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_share_logs)

        setSupportActionBar(toolbar as Toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        (toolbar as Toolbar).setNavigationOnClickListener { presenter.onBackCommandClick() }
        (toolbar as Toolbar).layoutLogs.visibility = View.VISIBLE
        (toolbar as Toolbar).btnShare.setOnClickListener { presenter.onShareClicked() }
        (toolbar as Toolbar).btnClear.setOnClickListener { presenter.clearLogs() }
    }

    override fun setLogs(logs: String) {
        textLogs.text = logs
        scrollView.post { scrollView.fullScroll(ScrollView.FOCUS_DOWN) }
    }

    override fun share(logsFile: File) {
        val path = FileProvider.getUriForFile(applicationContext, getString(R.string.file_provider_authority), logsFile)
        val emailIntent = Intent(Intent.ACTION_SEND)
        emailIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        emailIntent.type = "text/*"
        emailIntent.putExtra(Intent.EXTRA_STREAM, path)
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.share_extra_subject))
        startActivity(Intent.createChooser(emailIntent, getString(R.string.share)))
    }
}
