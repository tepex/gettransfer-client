package com.kg.gettransfer.presentation.presenter

import com.kg.gettransfer.domain.interactor.PaymentInteractor

import com.kg.gettransfer.extensions.newChainFromMain

import com.kg.gettransfer.presentation.view.BaseView
import com.kg.gettransfer.presentation.view.LogInView
import com.kg.gettransfer.presentation.view.Screens

import com.kg.gettransfer.utilities.Analytics

import org.koin.core.inject

open class OpenNextScreenPresenter<BV : BaseView> : BasePresenter<BV>() {

    private val paymentInteractor: PaymentInteractor by inject()

    internal lateinit var params: LogInView.Params

    fun openNextScreen() {
        if (params.nextScreen.isEmpty()) return
        when (params.nextScreen) {
            Screens.CLOSE_AFTER_LOGIN -> router.exit()
            Screens.CARRIER_MODE -> router.replaceScreen(Screens.Carrier(Screens.REG_CARRIER))
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
}
