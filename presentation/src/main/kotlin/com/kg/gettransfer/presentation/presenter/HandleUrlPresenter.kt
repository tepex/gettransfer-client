package com.kg.gettransfer.presentation.presenter

import com.arellomobile.mvp.InjectViewState
import com.kg.gettransfer.domain.ApiException
import com.kg.gettransfer.domain.model.Transfer
import com.kg.gettransfer.extensions.createStartChain
import com.kg.gettransfer.extensions.newChainFromMain
import com.kg.gettransfer.prefs.PreferencesImpl
import com.kg.gettransfer.presentation.view.HandleUrlView
import com.kg.gettransfer.presentation.view.Screens

@InjectViewState
class HandleUrlPresenter : BasePresenter<HandleUrlView>() {

    fun openOffer(transferId: Long, offerId: Long?, bookNowTransportId: String?) {
        if (!accountManager.isLoggedIn)
            router.newChainFromMain(Screens.LoginToPaymentOffer(transferId, offerId))
        else {
            utils.launchSuspend {

                fetchResult(SHOW_ERROR) { transferInteractor.getTransfer(transferId) }
                        .also {
                            it.error?.let { e ->
                                if (e.isNotFound())
                                    viewState.setError(ApiException(ApiException.NOT_FOUND, "Offer $offerId not found!"))
                                router.replaceScreen(Screens.MainPassenger()) }

                            it.isSuccess()?.let { transfer ->
                                val transferModel = transferMapper.toView(transfer)
                                router.createStartChain(
                                        Screens.PaymentOffer(
                                                transferId, offerId, transferModel.dateRefund,
                                                transferModel.paymentPercentages!!, bookNowTransportId
                                        )
                                )
                            }
                        }
            }
        }
    }

    @Suppress("UNUSED_PARAMETER")
    fun openChat(chatId: String) {

    }

    fun openTransfer(transferId: Long) {
        if (!accountManager.isLoggedIn)
            router.createStartChain(Screens.LoginToGetOffers(transferId, ""))
        else {
            utils.launchSuspend {
                fetchResult(SHOW_ERROR) { transferInteractor.getTransfer(transferId) }
                        .also {
                            it.error?.let { e ->
                                if (e.isNotFound())
                                    viewState.setError(ApiException(ApiException.NOT_FOUND, "Transfer $transferId not found!"))
                            }

                            it.isSuccess()?.let { t ->
                                if (t.checkStatusCategory() == Transfer.STATUS_CATEGORY_CONFIRMED)
                                    router.createStartChain(Screens.Details(transferId))
                                else router.createStartChain(Screens.Offers(transferId))
                            }
                        }
            }
        }
    }

    fun rateTransfer(transferId: Long, rate: Int) {
        if (!accountManager.isLoggedIn) router.replaceScreen(Screens.LoginToRateTransfer(transferId, rate))
        else router.newRootScreen(Screens.Splash(transferId, rate, true))
    }

    fun openMainScreen() = router.replaceScreen(Screens.MainPassenger())
}