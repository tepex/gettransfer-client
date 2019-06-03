package com.kg.gettransfer.presentation.presenter

import com.arellomobile.mvp.InjectViewState
import com.kg.gettransfer.R
import com.kg.gettransfer.extensions.firstSign
import com.kg.gettransfer.extensions.internationalExample
import com.kg.gettransfer.extensions.newChainFromMain
import com.kg.gettransfer.presentation.ui.MainLoginActivity
import com.kg.gettransfer.presentation.ui.Utils
import com.kg.gettransfer.presentation.ui.helpers.LoginHelper
import com.kg.gettransfer.presentation.ui.helpers.LoginHelper.CREDENTIALS_VALID
import com.kg.gettransfer.presentation.ui.helpers.LoginHelper.INVALID_EMAIL
import com.kg.gettransfer.presentation.ui.helpers.LoginHelper.INVALID_PHONE
import com.kg.gettransfer.presentation.view.LogInView
import com.kg.gettransfer.presentation.view.Screens
import com.kg.gettransfer.utilities.Analytics
import org.koin.standalone.KoinComponent

@InjectViewState
class LogInPresenter : BasePresenter<LogInView>(), KoinComponent {

    internal var emailOrPhone: String? = null
    internal var nextScreen: String? = null
    internal var transferId: Long = 0
    internal var offerId: Long? = null
    internal var rate: Int? = null

    private var password: String? = null

    val isPhone: Boolean
        get() = LoginHelper.checkIsNumber(emailOrPhone)

    companion object {
        const val PHONE_ATTRIBUTE = "+"
        const val EMAIL_ATTRIBUTE = "@"
    }

    override fun attachView(view: LogInView) {
        super.attachView(view)
        emailOrPhone?.let {
            viewState.setEmail(it)
        }
    }

    private fun validateInput(): Boolean {
        LoginHelper.validateInput(emailOrPhone!!, isPhone)    //force unwrap because null-check is already done
            .also {
                when (it) {
                    INVALID_EMAIL -> viewState.showValidationError(true, INVALID_EMAIL)
                    INVALID_PHONE -> viewState.showValidationError(true, INVALID_PHONE)
                    CREDENTIALS_VALID -> return true
                }
            }
        return false
    }

    private fun suggestCreateAccount() {
        viewState.showLoginInfo(R.string.LNG_ERROR_ACCOUNT, R.string.LNG_ERROR_ACCOUNT_CREATE_RIDE)
    }

    fun openPreviousScreen(openSettingsScreen: Boolean = false) {
        if (nextScreen.isNullOrEmpty()) return
        when (nextScreen) {
            Screens.CLOSE_AFTER_LOGIN -> router.exit()
            Screens.CARRIER_MODE -> {
                checkCarrierMode()
            }
            Screens.PASSENGER_MODE -> {
                router.exit()
//                analytics.logProfile(Analytics.PASSENGER_TYPE) //TODO add analitics
            }
            Screens.OFFERS -> {
                router.newChainFromMain(Screens.Offers(transferId))
            }
            Screens.PAYMENT_OFFER -> {
                utils.launchSuspend {
                    fetchData(NO_CACHE_CHECK) { transferInteractor.getTransfer(transferId) }
                        ?.let { transfer ->
                            val transferModel = transferMapper.toView(transfer)
                            router.newChainFromMain(
                                Screens.PaymentOffer(
                                    transferId,
                                    offerId,
                                    transferModel.dateRefund,
                                    transferModel.paymentPercentages!!,
                                    null
                                )
                            )
                        }
                }
            }
            Screens.RATE_TRANSFER -> {
                router.newRootScreen(Screens.Splash(transferId, rate, true))
            }
        }
        if (openSettingsScreen) {
            router.replaceScreen(Screens.Settings)
        }
    }

    private fun checkInputData() =
        emailOrPhone != null
                && checkIfEmailOrPhone()
                && validateInput()

    private fun checkIfEmailOrPhone() =
        emailOrPhone!!.run {
            if (firstSign() == PHONE_ATTRIBUTE || any { it.toString() == EMAIL_ATTRIBUTE }) {
                true
            } else {
                viewState.setError(
                    false,
                    R.string.LNG_ERROR_EMAIL_PHONE,
                    Utils.phoneUtil.internationalExample(sessionInteractor.locale)
                )
                false
            }
        }

    /*private fun identifyLoginType(input: String) =
            input.contains("@")*/

    private fun logLoginEvent(result: String) {
        val map = mutableMapOf<String, Any>()
        map[Analytics.STATUS] = result

        analytics.logEvent(Analytics.EVENT_LOGIN, createStringBundle(Analytics.STATUS, result), map)
    }

    override fun onBackCommandClick() {
        router.backTo(Screens.MainPassenger())
    }

    fun setEmailOrPhone(email: String) {
        this.emailOrPhone = if (email.isEmpty()) null else email
    }

    fun setPassword(password: String) {
        this.password = if (password.isEmpty()) null else password
    }

    fun onPassForgot() = router.navigateTo(Screens.RestorePassword)

    private fun checkCarrierMode() {
        if (accountManager.remoteAccount.isDriver) {
            if (accountManager.remoteAccount.isManager) analytics.logProfile(Analytics.CARRIER_TYPE)
            else analytics.logProfile(Analytics.DRIVER_TYPE)
            router.newRootScreen(Screens.Carrier(Screens.CARRIER_MODE))
            return
        }
        router.replaceScreen(Screens.Carrier(Screens.REG_CARRIER))
    }

    fun showNoAccountError() {
        viewState.setError(
            false,
            when (isPhone) {
                true -> R.string.LNG_ERROR_PHONE_NOTFOUND
                false -> R.string.LNG_ERROR_EMAIL_NOTFOUND
            }
        )
    }

    fun loginWithCode() {
        router.replaceScreen(Screens.SmsCode(isPhone, emailOrPhone))
    }

    fun onLoginClick() {
        if (password == null) {
            viewState.showValidationError(true, MainLoginActivity.INVALID_PASSWORD)
            return
        }
        if (!checkInputData()) return

        utils.launchSuspend {
            viewState.blockInterface(true, true)
            fetchResult(SHOW_ERROR, checkLoginError = false) {
                when (isPhone) {
                    true -> accountManager.login(null, LoginHelper.formatPhone(emailOrPhone), password!!, false)
                    false -> accountManager.login(emailOrPhone, null, password!!, false)
                }
            }
                .also {
                    it.error?.let { e ->
                        viewState.showError(true, e)
                        logLoginEvent(Analytics.RESULT_FAIL)
                    }

                    it.isSuccess()?.let {
                        //                        viewState.showErrorText(false, null)
                        openPreviousScreen()
                        logLoginEvent(Analytics.RESULT_SUCCESS)
                        registerPushToken()
                        router.exit()
                    }
                }
            viewState.blockInterface(false)
        }
    }
}
