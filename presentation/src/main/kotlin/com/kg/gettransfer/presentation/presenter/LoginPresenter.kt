package com.kg.gettransfer.presentation.presenter

import android.util.Patterns

import com.arellomobile.mvp.InjectViewState

import com.kg.gettransfer.R

import com.kg.gettransfer.domain.model.Account
import com.kg.gettransfer.domain.model.Account.Companion.GROUP_CARRIER_DRIVER
import com.kg.gettransfer.domain.model.Account.Companion.GROUP_MANAGER_VIEW_TRANSFERS
import com.kg.gettransfer.presentation.mapper.TransferMapper

import com.kg.gettransfer.presentation.ui.LoginActivity

import com.kg.gettransfer.presentation.view.LoginView
import com.kg.gettransfer.presentation.view.Screens

import com.kg.gettransfer.utilities.Analytics

import org.koin.standalone.inject

@InjectViewState
class LoginPresenter : BasePresenter<LoginView>() {
    private val transferMapper: TransferMapper by inject()

    private var password: String? = null

    internal var email: String? = null
    internal var screenForReturn: String? = null
    internal var transferId: Long = 0
    internal var offerId: Long? = null
    internal var rate: Int? = null

    fun onLoginClick() {
        if (!checkFields()) return

        utils.launchSuspend {
            viewState.blockInterface(true, true)
            val result = utils.asyncAwait { systemInteractor.login(email!!, password!!) }
            if (result.error == null) {
                if (!screenForReturn.isNullOrEmpty()) {
                    when (screenForReturn) {
                        Screens.CARRIER_MODE   -> {
                            router.navigateTo(Screens.ChangeMode(checkCarrierMode()))
                        }
                        Screens.PASSENGER_MODE -> {
                            router.navigateTo(Screens.ChangeMode(Screens.PASSENGER_MODE))
                            analytics.logProfile(Analytics.PASSENGER_TYPE)
                        }
                        Screens.OFFERS         -> {
                            router.navigateTo(Screens.ChangeMode(Screens.PASSENGER_MODE))
                            router.navigateTo(Screens.Offers(transferId))
                        }
                        Screens.CLOSE_AFTER_LOGIN -> router.exit()
                        Screens.PAYMENT_OFFER -> {
                            utils.launchSuspend {
                                val transferResult = utils.asyncAwait { transferInteractor.getTransfer(transferId) }
                                if (transferResult.error != null) viewState.setError(transferResult.error!!)
                                else {
                                    val transfer = transferResult.model
                                    val transferModel = transferMapper.toView(transfer)

                                    router.navigateTo(Screens.ChangeMode(Screens.PASSENGER_MODE))
                                    router.navigateTo(Screens.PaymentOffer(transferId, offerId, transferModel.dateRefund,
                                            transferModel.paymentPercentages, null))
                                }
                            }
                        }
                        Screens.RATE_TRANSFER -> router.navigateTo(Screens.Splash(transferId, rate, true))
                    }
                }
                logLoginEvent(Analytics.RESULT_SUCCESS)
                registerPushToken()
            } else {
                if(result.error!!.isNoUser()) viewState.showError(true, result.error!!.message)
                logLoginEvent(Analytics.RESULT_FAIL)
            }
            viewState.blockInterface(false)
        }
    }

    private fun logLoginEvent(result: String) {
        val map = mutableMapOf<String, Any>()
        map[Analytics.STATUS] = result

        analytics.logEvent(Analytics.EVENT_LOGIN, createStringBundle(Analytics.STATUS, result), map)
    }

    fun onHomeClick() = router.exit()

    fun setEmail(email: String) { this.email = if (email.isEmpty()) null else email }
    fun setPassword(password: String) { this.password = if (password.isEmpty()) null else password }

    private fun checkFields(): Boolean {
        val checkEmail = email != null && Patterns.EMAIL_ADDRESS.matcher(email!!).matches()
        val checkPassword = password != null
        var fieldsValid = true
        var errorType = 0
        if (!checkEmail)         { fieldsValid = false; errorType = LoginActivity.INVALID_EMAIL }
        else if (!checkPassword) { fieldsValid = false; errorType = LoginActivity.INVALID_PASSWORD }
        viewState.showValidationError(!fieldsValid, errorType)
        return fieldsValid
    }

    private fun checkCarrierMode(): String {
        val groups = systemInteractor.account.groups
        if (groups.indexOf(GROUP_CARRIER_DRIVER) >= 0) {
            if (groups.indexOf(GROUP_MANAGER_VIEW_TRANSFERS) >= 0) analytics.logProfile(Analytics.CARRIER_TYPE)
            else analytics.logProfile(Analytics.DRIVER_TYPE)
            return Screens.CARRIER_MODE
        }
        return Screens.REG_CARRIER
    }

    fun onPassForgot() = router.navigateTo(Screens.RestorePassword)
}
