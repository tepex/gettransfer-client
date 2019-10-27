package com.kg.gettransfer.presentation.presenter

import com.kg.gettransfer.domain.model.OfferItem
import com.kg.gettransfer.domain.model.Transfer
import com.kg.gettransfer.extensions.createStartChain
import com.kg.gettransfer.presentation.view.OpenDeepLinkScreenView
import com.kg.gettransfer.presentation.view.Screens

open class OpenDeepLinkScreenPresenter<BV: OpenDeepLinkScreenView>: BaseHandleUrlPresenter<BV>() {

    var transferId: Long? = null

    suspend fun openOffer(transferId: Long, offerId: Long?, bookNowTransportId: String?) {
        checkTransfer(transferId).isSuccess()?.let { transfer ->
            if (transfer.checkStatusCategory() == Transfer.STATUS_CATEGORY_ACTIVE) {
                val offerItem: OfferItem? = when {
                    offerId != null && offerId != DEFAULT_ID ->
                        fetchData(NO_CACHE_CHECK) { offerInteractor.getOffers(transfer.id) }?.find { it.id == offerId }
                    !bookNowTransportId.isNullOrEmpty()      ->
                        transfer.bookNowOffers.find { it.transportType.id.toString() == bookNowTransportId }
                    else                                     -> null
                }
                if (offerItem != null) {
                    with(paymentInteractor) {
                        selectedTransfer = transfer
                        selectedOffer = offerItem
                    }
                    router.createStartChain(Screens.PaymentOffer())
                } else {
                    router.createStartChain(Screens.Offers(transfer.id))
                }
            } else {
                router.createStartChain(Screens.Details(transfer.id))
            }
        }
    }

    suspend fun openChat(transferId: Long) {
        checkTransfer(transferId).isSuccess()?.let { transfer ->
            if (transfer.showOfferInfo) {
                router.createStartChain(Screens.Chat(transfer.id))
            } else {
                viewState.setChatIsNoLongerAvailableError { onDialogDismissCallback() }
            }
        }
    }

    suspend fun openTransfer(transferId: Long) {
        checkTransfer(transferId).isSuccess()?.let { transfer ->
            if (transfer.checkStatusCategory() == Transfer.STATUS_CATEGORY_ACTIVE) {
                router.createStartChain(Screens.Offers(transfer.id))
            } else {
                router.createStartChain(Screens.Details(transfer.id))
            }
        }
    }

    suspend fun rateTransfer(transferId: Long, rate: Int) {
        checkTransfer(transferId).isSuccess()?.let { transfer ->
            router.newRootScreen(Screens.MainPassengerToRateTransfer(transfer.id, rate))
        }
    }

    suspend fun openVoucher(transferId: Long) {
        if (checkTransfer(transferId).isSuccess() != null) {
            viewState.downloadVoucher()
        }
    }

    fun downloadVoucher() {
        transferId?.let { downloadManager.downloadVoucher(it) }
        openMainScreen()
    }

    private suspend fun checkTransfer(transferId: Long) =
        fetchResult(SHOW_ERROR) { transferInteractor.getTransfer(transferId) }.also { result ->
            result.error?.let { e ->
                if (e.isNotFound()) {
                    viewState.setTransferNotFoundError(transferId) { onDialogDismissCallback() }
                } else {
                    openMainScreen()
                }
            }
        }

    open fun onDialogDismissCallback() {
        openMainScreen()
    }

    fun openMainScreen() = router.replaceScreen(Screens.MainPassenger())

    companion object {
        const val DEFAULT_ID = 0L
    }
}