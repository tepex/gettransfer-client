package com.kg.gettransfer.presentation.presenter

import com.kg.gettransfer.domain.interactor.PaymentInteractor

import com.kg.gettransfer.extensions.newChainFromMain

import com.kg.gettransfer.presentation.view.BaseView
import com.kg.gettransfer.presentation.view.LogInView
import com.kg.gettransfer.presentation.view.Screens

import com.kg.gettransfer.utilities.Analytics

import org.koin.core.inject

open class OpenNextScreenPresenter<BV : BaseView> : BasePresenter<BV>() {

    internal lateinit var params: LogInView.Params

    fun openNextScreen() {
        if (params.nextScreen.isEmpty()) return
        when (params.nextScreen) {
            Screens.CLOSE_AFTER_LOGIN -> router.exit()
            Screens.CARRIER_MODE -> checkCarrierMode()
            Screens.PASSENGER_MODE -> {
                router.exit()
                analytics.logProfile(Analytics.PASSENGER_TYPE)
            }
            Screens.OFFERS -> router.newChainFromMain(Screens.Offers(params.transferId))
            Screens.DETAILS -> router.newChainFromMain(Screens.Details(params.transferId))
            Screens.PAYMENT_OFFER -> {
                utils.launchSuspend {
                    val transferResult = fetchData(NO_CACHE_CHECK) { transferInteractor.getTransfer(params.transferId) }
                    val offerResult =
                        fetchData(NO_CACHE_CHECK) { offerInteractor.getOffers(params.transferId) }?.find { it.id == params.offerId }
                    transferResult?.let { transfer ->
                        offerResult?.let { offer ->
                            with(paymentInteractor) {
                                selectedTransfer = transfer
                                selectedOffer = offer
                            }
                            router.newChainFromMain(Screens.PaymentOffer())
                        }
                    }
                }
            }
            Screens.RATE_TRANSFER -> {
                router.newRootScreen(Screens.MainPassengerToRateTransfer(params.transferId, params.rate))
            }
        }
    }

    private fun checkCarrierMode() {
        if (accountManager.remoteAccount.isDriver) {
            if (accountManager.remoteAccount.isManager) {
                analytics.logProfile(Analytics.CARRIER_TYPE)
            } else {
                analytics.logProfile(Analytics.DRIVER_TYPE)
            }
            router.newRootScreen(Screens.CarrierMode)
        } else {
            router.replaceScreen(Screens.CarrierRegister)
        }
    }
}
