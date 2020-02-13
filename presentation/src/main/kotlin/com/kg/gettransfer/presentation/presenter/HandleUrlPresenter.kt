package com.kg.gettransfer.presentation.presenter

import android.net.Uri

import com.kg.gettransfer.R
import com.kg.gettransfer.extensions.createStartChain
import com.kg.gettransfer.presentation.model.DeeplinkScreenModel
import com.kg.gettransfer.presentation.view.OpenDeepLinkScreenView
import com.kg.gettransfer.presentation.view.Screens
import com.kg.gettransfer.utilities.DeeplinkManager

import kotlinx.coroutines.launch

import moxy.InjectViewState
import org.koin.core.inject

/** TODO: refactor to regular expressions */
@InjectViewState
class HandleUrlPresenter : OpenDeepLinkScreenPresenter<OpenDeepLinkScreenView>() {

    lateinit var url: String

    private val deeplinkManager: DeeplinkManager by inject()

    @Suppress("ComplexMethod")
    fun handleIntent(appLinkData: Uri) {
        when(val screen = deeplinkManager.getScreenForLink(appLinkData)) {
            is DeeplinkScreenModel.PaymentOffer -> openOfferLink(screen.transferId, screen.offerId, screen.bookNowOfferId)
            is DeeplinkScreenModel.Chat -> openChatLink(screen.transferId)
            is DeeplinkScreenModel.Transfer -> openTransferLink(screen.transferId)
            is DeeplinkScreenModel.RateTransfer -> openRateTransferLink(screen.transferId, screen.rate)
            is DeeplinkScreenModel.DownloadVoucher -> openVoucherLink(screen.transferId)
            is DeeplinkScreenModel.CreateOrder -> createOrder(screen.fromPlaceId, screen.toPlaceId, screen.promo)
            is DeeplinkScreenModel.NewPassword -> openNewPasswordLink(screen.authKey)
            is DeeplinkScreenModel.Main -> openMainScreen()
        }
    }

    private fun openOfferLink(transferId: Long, offerId: Long?, bookNowTransportId: String?) = worker.main.launch {
        checkInitialization()
        if (!accountManager.isLoggedIn) {
            router.createStartChain(Screens.LoginToPaymentOffer(transferId, offerId, bookNowTransportId))
        } else {
            openOffer(transferId, offerId, bookNowTransportId)
        }
    }

    private fun openChatLink(transferId: Long) = worker.main.launch {
        checkInitialization()
        if (!accountManager.isLoggedIn) {
            router.createStartChain(Screens.LoginToChat(transferId))
        } else {
            openChat(transferId)
        }
    }

    private fun openTransferLink(transferId: Long) = worker.main.launch {
        checkInitialization()
        if (!accountManager.isLoggedIn) {
            router.createStartChain(Screens.LoginToShowDetails(transferId))
        } else {
            openTransfer(transferId)
        }
    }

    private fun openRateTransferLink(transferId: Long, rate: Int?) = worker.main.launch {
        checkInitialization()
        if (!accountManager.isLoggedIn) {
            router.createStartChain(Screens.LoginToRateTransfer(transferId, rate))
        } else {
            rateTransfer(transferId, rate)
        }
    }

    private fun openVoucherLink(transferId: Long) = worker.main.launch {
        checkInitialization()
        if (!accountManager.isLoggedIn) {
            router.createStartChain(Screens.LoginToDownloadVoucher(transferId))
        } else {
            openVoucher(transferId)
        }
    }

    private fun openNewPasswordLink(authKey: String?) = worker.main.launch {
        checkInitialization()
        if (accountManager.isLoggedIn) {
            openProfileSettings()
            return@launch
        }
        authKey?.let { key ->
            fetchResultOnly { sessionInteractor.authOldToken(key) }
            fetchResultOnly { sessionInteractor.coldStart() }
            if (accountManager.isLoggedIn) {
                openProfileSettings()
            } else {
                openMainWithError(R.string.LNG_ERROR_PASSWORD_LINK)
            }
        } ?: openMainWithError(R.string.LNG_ERROR_PASSWORD_LINK)
    }

    private fun openProfileSettings() {
        router.createStartChain(Screens.ProfileSettings())
    }
}
