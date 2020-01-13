package com.kg.gettransfer.presentation.presenter

import com.kg.gettransfer.core.presentation.WorkerManager

import com.kg.gettransfer.sys.domain.ContactEmail
import com.kg.gettransfer.presentation.view.SupportView
import com.kg.gettransfer.utilities.Analytics
import com.kg.gettransfer.utilities.CommunicationManager

import kotlinx.coroutines.launch

import moxy.InjectViewState

import org.koin.core.inject
import org.koin.core.parameter.parametersOf

@InjectViewState
class SupportPresenter : BasePresenter<SupportView>() {

    private val worker: WorkerManager by inject { parametersOf("SupportPresenter") }
    private val communicationManager: CommunicationManager by inject()

    override fun attachView(view: SupportView) {
        super.attachView(view)
        worker.main.launch {
            showEmail()
        }
    }

    private suspend fun showEmail() {
        viewState.showEmail(configsManager.getConfigs().contactEmails.first { it.id == ContactEmail.Id.INFO }.email)
    }

    fun onAboutUsClick() = worker.main.launch {
        analytics.logEvent(Analytics.EVENT_MENU, Analytics.PARAM_KEY_NAME, Analytics.ABOUT_CLICKED)
        viewState.showAboutUs()
    }

    fun sendEmail(transferId: Long?) = communicationManager.sendEmail(transferId)

    fun callPhone(phone: String) = communicationManager.callPhone(phone)
}
