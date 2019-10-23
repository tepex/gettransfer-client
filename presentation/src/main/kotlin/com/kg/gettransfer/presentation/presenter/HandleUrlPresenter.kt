package com.kg.gettransfer.presentation.presenter

import moxy.InjectViewState

import com.kg.gettransfer.domain.model.OfferItem
import com.kg.gettransfer.domain.model.Transfer
import com.kg.gettransfer.extensions.createStartChain
import com.kg.gettransfer.extensions.newChainFromMain
import com.kg.gettransfer.presentation.view.HandleUrlView
import com.kg.gettransfer.presentation.view.Screens

@InjectViewState
class HandleUrlPresenter : BasePresenter<HandleUrlView>() {

    @Suppress("ComplexMethod")
    fun openOffer(transferId: Long, offerId: Long?, bookNowTransportId: String?) = utils.launchSuspend {
        checkInitialization()
        if (!accountManager.isLoggedIn) {
            router.newChainFromMain(Screens.LoginToPaymentOffer(transferId, offerId))
        } else {
            val result = fetchResult(SHOW_ERROR) { transferInteractor.getTransfer(transferId) }
            result.error?.let { e ->
                if (e.isNotFound()) {
                    viewState.setTransferNotFoundError(transferId)
                }
                router.replaceScreen(Screens.MainPassenger())
            }
            result.isSuccess()?.let { transfer ->
                val offerItem: OfferItem? = when {
                    offerId != null            ->
                        fetchData(NO_CACHE_CHECK) { offerInteractor.getOffers(transferId) }?.find { it.id == offerId }
                    bookNowTransportId != null ->
                        transfer.bookNowOffers.find { it.transportType.id.name == bookNowTransportId }
                    else                       -> null
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

    @Suppress("UNUSED_PARAMETER", "EmptyFunctionBlock")
    fun openChat(chatId: String) {}

    fun openTransfer(transferId: Long) = utils.launchSuspend {
        checkInitialization()
        if (!accountManager.isLoggedIn) {
            router.createStartChain(Screens.LoginToShowDetails(transferId))
        } else {
            val result = fetchResult(SHOW_ERROR) { transferInteractor.getTransfer(transferId) }
            result.error?.let { e ->
                if (e.isNotFound()) {
                    viewState.setTransferNotFoundError(transferId)
                }
            }
            result.isSuccess()?.let { transfer ->
                if (transfer.checkStatusCategory() == Transfer.STATUS_CATEGORY_CONFIRMED) {
                    router.createStartChain(Screens.Details(transferId))
                } else {
                    router.createStartChain(Screens.Offers(transferId))
                }
            }
        }
    }

    fun rateTransfer(transferId: Long, rate: Int) = utils.launchSuspend {
        checkInitialization()
        if (!accountManager.isLoggedIn) {
            router.replaceScreen(Screens.LoginToRateTransfer(transferId, rate))
        } else {
            router.newRootScreen(Screens.MainPassengerToRateTransfer(transferId, rate))
        }
    }

    fun createOrder(fromPlaceId: String?, toPlaceId: String?, promo: String?) = utils.launchSuspend {
        checkInitialization()
        with(orderInteractor) {
            fromPlaceId?.let { fetchResult(SHOW_ERROR) { updatePoint(false, it) } }
            toPlaceId?.let   { fetchResult(SHOW_ERROR) { updatePoint(true, it) } }
            promo?.let { promoCode = it }
            if (isCanCreateOrder()) {
                router.createStartChain(Screens.CreateOrder)
            } else {
                router.newRootScreen(Screens.MainPassenger())
            }
        }
    }

    private suspend fun checkInitialization() {
        if (!sessionInteractor.isInitialized) {
            fetchResult(SHOW_ERROR) { sessionInteractor.coldStart() }
        }
    }

    fun openMainScreen() = router.replaceScreen(Screens.MainPassenger())
}
