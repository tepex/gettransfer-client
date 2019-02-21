package com.kg.gettransfer.presentation.presenter

import com.arellomobile.mvp.InjectViewState
import com.kg.gettransfer.domain.ApiException
import com.kg.gettransfer.prefs.PreferencesImpl
import com.kg.gettransfer.presentation.mapper.TransferMapper
import com.kg.gettransfer.presentation.view.HandleUrlView
import com.kg.gettransfer.presentation.view.Screens
import org.koin.standalone.inject

@InjectViewState
class HandleUrlPresenter : BasePresenter<HandleUrlView>() {
    private val transferMapper: TransferMapper by inject()

    fun openOffer(transferId: Long, offerId: Long) {
        if (!isLoggedIn())
            router.replaceScreen(Screens.LoginToPaymentOffer(transferId, offerId))
        else {
            utils.launchSuspend {
                val transferResult = utils.asyncAwait { transferInteractor.getTransfer(transferId) }
                if (transferResult.error != null) {
                    val err = transferResult.error!!
                    if (err.isNotFound()) {
                        viewState.setError(ApiException(ApiException.NOT_FOUND, "Offer $offerId not found!"))
                    }
                    router.replaceScreen(Screens.ChangeMode(Screens.PASSENGER_MODE))
                }
                else {
                    val transfer = transferResult.model
                    val transferModel = transferMapper.toView(transfer)

                    router.replaceScreen(Screens.ChangeMode(Screens.PASSENGER_MODE))
                    router.navigateTo(Screens.PaymentOffer(
                            transferId, offerId, transferModel.dateRefund,
                            transferModel.paymentPercentages, null))
                }
            }
        }
    }

    fun openChat(chatId: String) {

    }

    fun openTransfer(transferId: Long) {
        if (!isLoggedIn())
            router.replaceScreen(Screens.LoginToGetOffers(transferId, ""))
        else {
            router.replaceScreen(Screens.ChangeMode(Screens.PASSENGER_MODE))
            router.navigateTo(Screens.Offers(transferId))
        }
    }

    fun rateTransfer(transferId: Long, rate: Int) {
        if (!isLoggedIn()) {
            router.replaceScreen(Screens.LoginToRateTransfer(transferId, rate))
        } else router.replaceScreen(Screens.Splash(transferId, rate, true))
    }

    private fun isLoggedIn() = systemInteractor.userEmail != PreferencesImpl.INVALID_EMAIL
}