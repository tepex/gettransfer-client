package com.kg.gettransfer.presentation.presenter

import com.arellomobile.mvp.InjectViewState

import com.kg.gettransfer.presentation.view.LogsView

@InjectViewState
class LogsPresenter : BasePresenter<LogsView>() {
    override fun onFirstViewAttach() {
        viewState.setLogs(systemInteractor.logs)
    }

    fun clearLogs() {
        systemInteractor.clearLogs()
        viewState.setLogs(systemInteractor.logs)
    }

    fun onShareClicked() {
        /* TODO: use router.navigateTo(SendEmail())
        val path = FileProvider.getUriForFile(applicationContext, getString(R.string.file_provider_authority), logsFile)
        val emailIntent = Intent(Intent.ACTION_SEND)
        emailIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)*/
        //emailIntent.type = "text/*"
        /*
        emailIntent.putExtra(Intent.EXTRA_STREAM, path)
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.share_extra_subject))
        startActivity(Intent.createChooser(emailIntent, getString(R.string.share)))
        */
    }
}
