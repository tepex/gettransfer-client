package com.kg.gettransfer.presentation.presenter

import com.kg.gettransfer.extensions.newChainFromMain

import com.kg.gettransfer.presentation.view.LogInView
import com.kg.gettransfer.presentation.view.OpenDeepLinkScreenView
import com.kg.gettransfer.presentation.view.Screens
import kotlinx.coroutines.launch

open class OpenNextScreenPresenter<BV: OpenDeepLinkScreenView> : OpenDeepLinkScreenPresenter<BV>() {

    internal lateinit var params: LogInView.Params

    fun openNextScreen() = worker.main.launch {
        if (params.nextScreen.isEmpty()) openMainScreen()
        when (params.nextScreen) {
            Screens.CLOSE_AFTER_LOGIN -> router.exit()
            // from create order screen (needed delete)
            Screens.OFFERS -> router.newChainFromMain(Screens.Offers(params.transferId))
            // from deeplinks
            Screens.DETAILS          -> openTransfer(params.transferId)
            Screens.PAYMENT_OFFER    -> openOffer(params.transferId, params.offerId, params.bookNowTransportId)
            Screens.RATE_TRANSFER    -> rateTransfer(params.transferId, params.rate)
            Screens.DOWNLOAD_VOUCHER -> openVoucher(params.transferId)
            Screens.CHAT             -> openChat(params.transferId)
        }
    }

    override fun onDialogDismissCallback() {
        router.exit()
    }
}
