package com.kg.gettransfer.utilities

import com.kg.gettransfer.core.presentation.WorkerManager
import com.kg.gettransfer.domain.interactor.TransferInteractor
import com.kg.gettransfer.domain.model.Transfer
import com.kg.gettransfer.presentation.delegate.AccountManager
import com.kg.gettransfer.presentation.view.Screens

import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

import org.koin.core.KoinComponent
import org.koin.core.inject
import org.koin.core.parameter.parametersOf

import ru.terrakok.cicerone.Router

class CommunicationManager : KoinComponent {

    private val worker: WorkerManager by inject { parametersOf(CommunicationManager::class.simpleName) }

    private val transferInteractor: TransferInteractor by inject()

    private val accountManager: AccountManager by inject()

    private val router: Router by inject()

    fun sendEmail(transferId: Long?, emailCarrier: String? = null) {
        worker.main.launch {
            var transferID: Long? = null
            if (transferId == null || transferId == -1L) {
                val result = withContext(worker.bg) {
                    transferInteractor.getAllTransfers(getUserRole())
                }
                if (result.error == null && result.model.first.isNotEmpty()) {
                    transferID = result.model.first.first().id
                }
            } else {
                transferID = transferId
            }
            router.navigateTo(
                Screens.SendEmail(emailCarrier, transferID, accountManager.remoteProfile.email)
            )
        }
    }

    private fun getUserRole(): String =
        if (isBusinessAccount()) Transfer.Role.PARTNER.toString() else Transfer.Role.PASSENGER.toString()

    private fun isBusinessAccount(): Boolean = accountManager.remoteAccount.isBusinessAccount

    fun callPhone(phone: String) {
        router.navigateTo(Screens.CallPhone(phone))
    }
}
