package com.kg.gettransfer.presentation.presenter

import com.arellomobile.mvp.InjectViewState
import com.kg.gettransfer.domain.interactor.PaymentInteractor
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
import org.koin.standalone.inject

@InjectViewState
class LogInPresenter : BasePresenter<LogInView>(), KoinComponent {

    private val paymentInteractor: PaymentInteractor by inject()

    var emailOrPhone: String = ""
        set(value) {
            field = if (isPhone(value)) LoginHelper.formatPhone(value) else value
        }
    var password: String = ""
    internal var nextScreen: String? = null
    internal var transferId: Long = 0
    internal var offerId: Long? = null
    internal var rate: Int? = null
    val isEnabledButtonLogin
        get() = password.isNotEmpty() && emailOrPhone.isNotEmpty()
    val isEnabledRequestCodeButton
        get() = emailOrPhone.isNotEmpty()

    private fun isPhone(value: String = emailOrPhone) = LoginHelper.checkIsNumber(value)

    override fun attachView(view: LogInView) {
        super.attachView(view)
        if (emailOrPhone.isNotEmpty()) viewState.setEmail(emailOrPhone)
    }

    private fun validateInput(): Boolean {
        LoginHelper.validateInput(
            emailOrPhone,
            isPhone()
        ).also {
            when (it) {
                INVALID_EMAIL -> viewState.showValidationError(INVALID_EMAIL)
                INVALID_PHONE -> viewState.showValidationError(INVALID_PHONE)
                CREDENTIALS_VALID -> return true
            }
        }
        return false
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
                analytics.logProfile(Analytics.PASSENGER_TYPE)
            }
            Screens.OFFERS -> {
                router.newChainFromMain(Screens.Offers(transferId))
            }
            Screens.PAYMENT_OFFER -> {
                utils.launchSuspend {
                    val transferResult = fetchData(NO_CACHE_CHECK) { transferInteractor.getTransfer(transferId) }
                    val offerResult =
                        fetchData(NO_CACHE_CHECK) { offerInteractor.getOffers(transferId) }?.find { it.id == offerId }
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
                router.newRootScreen(Screens.Splash(transferId, rate, true))
            }
        }
        if (openSettingsScreen) {
            router.replaceScreen(Screens.Settings)
        }
        viewState.hideLoading()
    }

    private fun checkInputData() = emailOrPhone.isNotEmpty() && validateInput()

    private fun logLoginEvent(result: String) {
        val map = mutableMapOf<String, Any>()
        map[Analytics.STATUS] = result

        analytics.logEvent(Analytics.EVENT_LOGIN, createStringBundle(Analytics.STATUS, result), map)
    }

    override fun onBackCommandClick() {
        router.backTo(Screens.MainPassenger())
    }

    private fun checkCarrierMode() {
        if (accountManager.remoteAccount.isDriver) {
            if (accountManager.remoteAccount.isManager) analytics.logProfile(Analytics.CARRIER_TYPE)
            else analytics.logProfile(Analytics.DRIVER_TYPE)
            router.newRootScreen(Screens.Carrier(Screens.CARRIER_MODE))
            return
        }
        router.replaceScreen(Screens.Carrier(Screens.REG_CARRIER))
    }

    fun loginWithCode() {
        if (!checkInputData()) return
        viewState.hideLoading()
        val isPhone = isPhone()
        router.replaceScreen(
            Screens.SmsCode(
                emailOrPhone,
                isPhone,
                nextScreen ?: ""
            )
        )
    }

    fun onLoginClick() {
        if (password.isEmpty()) {
            viewState.showValidationError(MainLoginActivity.INVALID_PASSWORD)
            return
        }
        if (emailOrPhone.isEmpty() ||
            isPhone() && !LoginHelper.phoneIsValid(emailOrPhone) ||
            !isPhone() && !LoginHelper.emailIsValid(emailOrPhone)
        ) {
            viewState.showValidationError(MainLoginActivity.INVALID_EMAIL)
            return
        }

        if (!checkInputData()) return

        utils.launchSuspend {
            fetchResult(SHOW_ERROR, checkLoginError = false) {
                when (isPhone()) {
                    true -> accountManager.login(null, LoginHelper.formatPhone(emailOrPhone), password, false)
                    false -> accountManager.login(emailOrPhone, null, password, false)
                }
            }.also {
                it.error?.let { e ->
                    viewState.setError(e)
                    logLoginEvent(Analytics.RESULT_FAIL)
                }

                it.isSuccess()?.let {
                    openPreviousScreen()
                    logLoginEvent(Analytics.RESULT_SUCCESS)
                    registerPushToken()
                    router.exit()
                }
            }
        }
        viewState.hideLoading()
    }

    fun sendVerificationCode() {
        if (emailOrPhone.isEmpty() ||
            (!isPhone() && !LoginHelper.emailIsValid(emailOrPhone)) ||
            (isPhone() && !LoginHelper.phoneIsValid(emailOrPhone))
        ) {
            viewState.showValidationError(MainLoginActivity.INVALID_EMAIL)
            return
        }
        val isPhone = isPhone()
        utils.launchSuspend {
            fetchResult(SHOW_ERROR, checkLoginError = false) {
                when (isPhone) {
                    true -> sessionInteractor.getVerificationCode(null, LoginHelper.formatPhone(emailOrPhone))
                    false -> sessionInteractor.getVerificationCode(emailOrPhone, null)
                }
            }.also {
                if (it.error != null)
                    viewState.setError(it.error!!)
                else {
                    loginWithCode()
                }
            }
        }
    }

    fun getPhoneExample(): String = Utils.phoneUtil.internationalExample(sessionInteractor.locale)

    companion object {
        const val PHONE_ATTRIBUTE = "+"
        const val EMAIL_ATTRIBUTE = "@"
    }
}
