package com.kg.gettransfer.presentation.presenter

import com.kg.gettransfer.core.presentation.WorkerManager
import com.kg.gettransfer.domain.model.OfferItem
import com.kg.gettransfer.domain.model.Transfer
import com.kg.gettransfer.extensions.createStartChain
import com.kg.gettransfer.extensions.newChainFromMain

import com.kg.gettransfer.presentation.view.BaseView
import com.kg.gettransfer.presentation.view.LogInView
import com.kg.gettransfer.presentation.view.Screens
import kotlinx.coroutines.launch
import org.koin.core.inject
import org.koin.core.parameter.parametersOf

open class OpenNextScreenPresenter<BV : BaseView> : BasePresenter<BV>() {

    val worker: WorkerManager by inject { parametersOf("OpenNextScreenPresenter") }

    internal lateinit var params: LogInView.Params

    fun openNextScreen() {
        if (params.nextScreen.isEmpty()) return
        when (params.nextScreen) {
            Screens.CLOSE_AFTER_LOGIN -> router.exit()
            // from create order screen (needed delete)
            Screens.OFFERS -> router.newChainFromMain(Screens.Offers(params.transferId))
            // from deeplinks
            Screens.DETAILS       -> openTransfer()
            Screens.PAYMENT_OFFER -> openOffer()
            Screens.RATE_TRANSFER -> rateTransfer()
        }
    }

    private fun openOffer() = worker.main.launch {
        checkTransfer(params.transferId).isSuccess()?.let { transfer ->
            if (transfer.checkStatusCategory() == Transfer.STATUS_CATEGORY_ACTIVE) {
                val offerItem: OfferItem? = when {
                    params.offerId != 0L            ->
                        fetchData(NO_CACHE_CHECK) { offerInteractor.getOffers(transfer.id) }?.find { it.id == params.offerId }
                    params.bookNowTransportId != "" ->
                        transfer.bookNowOffers.find { it.transportType.id.name == params.bookNowTransportId }
                    else                       -> null
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

    private fun openChat() = worker.main.launch {
        // needed realization
    }

    private fun openTransfer() = worker.main.launch {
        checkTransfer(params.transferId).isSuccess()?.let { transfer ->
            if (transfer.checkStatusCategory() == Transfer.STATUS_CATEGORY_ACTIVE) {
                router.createStartChain(Screens.Offers(transfer.id))
            } else {
                router.createStartChain(Screens.Details(transfer.id))
            }
        }
    }

    private fun rateTransfer() = worker.main.launch {
        checkTransfer(params.transferId).isSuccess()?.let { transfer ->
            router.replaceScreen(Screens.MainPassengerToRateTransfer(transfer.id, params.rate))
        }
    }

    private suspend fun checkTransfer(transferId: Long) =
        fetchResult(SHOW_ERROR) { transferInteractor.getTransfer(transferId) }.also { result ->
            result.error?.let { e ->
                if (e.isNotFound()) {
                    viewState.setTransferNotFoundError(transferId) { router.exit() }
                }
            }
        }
}
