package com.kg.gettransfer.presentation.presenter

import com.arellomobile.mvp.InjectViewState
import com.kg.gettransfer.domain.interactor.PaymentInteractor
import com.kg.gettransfer.domain.model.OfferItem
import com.kg.gettransfer.domain.model.Transfer
import com.kg.gettransfer.extensions.createStartChain
import com.kg.gettransfer.extensions.newChainFromMain
import com.kg.gettransfer.presentation.view.HandleUrlView
import com.kg.gettransfer.presentation.view.Screens
import org.koin.core.inject

@InjectViewState
class HandleUrlPresenter : BasePresenter<HandleUrlView>() {
    private val paymentInteractor: PaymentInteractor by inject()

    fun openOffer(transferId: Long, offerId: Long?, bookNowTransportId: String?) {
        utils.launchSuspend {
            if (!sessionInteractor.isInitialized) {
                fetchResult(SHOW_ERROR) { sessionInteractor.coldStart() }
            }
            if (!accountManager.isLoggedIn)
                router.newChainFromMain(Screens.LoginToPaymentOffer(transferId, offerId))
            else {
                fetchResult(SHOW_ERROR) { transferInteractor.getTransfer(transferId) }
                        .also {
                            it.error?.let { e ->
                                if (e.isNotFound()) viewState.setTransferNotFoundError(transferId)
                                router.replaceScreen(Screens.MainPassenger())
                            }

                            it.isSuccess()?.let { transfer ->

                                val offerItem: OfferItem? = when {
                                    offerId != null -> {
                                        fetchData(NO_CACHE_CHECK) {
                                            offerInteractor.getOffers(transferId)
                                        }?.find { offer -> offer.id == offerId }
                                    }
                                    bookNowTransportId != null -> transfer.bookNowOffers.find { offer -> offer.transportType.id.name == bookNowTransportId }
                                    else -> null
                                }

                                offerItem?.let { offer ->
                                    with(paymentInteractor) {
                                        selectedTransfer = transfer
                                        selectedOffer = offer
                                    }
                                    router.createStartChain(Screens.PaymentOffer())
                                }
                            }
                        }
            }
        }
    }

    @Suppress("UNUSED_PARAMETER")
    fun openChat(chatId: String) {

    }

    fun openTransfer(transferId: Long) {
        utils.launchSuspend {
            if (!sessionInteractor.isInitialized) {
                fetchResult(SHOW_ERROR) { sessionInteractor.coldStart() }
            }
            if (!accountManager.isLoggedIn)
                router.createStartChain(Screens.LoginToShowDetails(transferId))
            else {
                fetchResult(SHOW_ERROR) { transferInteractor.getTransfer(transferId) }
                        .also {
                            it.error?.let { e ->
                                if (e.isNotFound()) viewState.setTransferNotFoundError(transferId)
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
        utils.launchSuspend {
            if (!sessionInteractor.isInitialized) {
                fetchResult(SHOW_ERROR) { sessionInteractor.coldStart() }
            }
            if (!accountManager.isLoggedIn) router.replaceScreen(Screens.LoginToRateTransfer(transferId, rate))
            else router.newRootScreen(Screens.MainPassengerToRateTransfer(transferId, rate))
        }
    }

    fun openMainScreen() = router.replaceScreen(Screens.MainPassenger())
}