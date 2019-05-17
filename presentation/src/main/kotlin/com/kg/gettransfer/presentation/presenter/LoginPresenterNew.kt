package com.kg.gettransfer.presentation.presenter

import com.arellomobile.mvp.InjectViewState
import com.kg.gettransfer.R

import com.kg.gettransfer.domain.model.Account.Companion.GROUP_CARRIER_DRIVER
import com.kg.gettransfer.domain.model.Account.Companion.GROUP_MANAGER_VIEW_TRANSFERS
import com.kg.gettransfer.extensions.firstSign
import com.kg.gettransfer.extensions.internationalExample

import com.kg.gettransfer.presentation.ui.LoginActivityNew
import com.kg.gettransfer.presentation.ui.Utils
import com.kg.gettransfer.presentation.ui.helpers.LoginHelper
import com.kg.gettransfer.presentation.ui.helpers.LoginHelper.CREDENTIALS_VALID
import com.kg.gettransfer.presentation.ui.helpers.LoginHelper.INVALID_EMAIL
import com.kg.gettransfer.presentation.ui.helpers.LoginHelper.INVALID_PHONE

import com.kg.gettransfer.presentation.view.LoginViewNew
import com.kg.gettransfer.presentation.view.Screens

import com.kg.gettransfer.utilities.Analytics

@InjectViewState
class LoginPresenterNew : BasePresenter<LoginViewNew>() {
    internal var showingFragment: Int? = null

    internal var emailOrPhone: String? = null
    internal var nextScreen: String? = null
    internal var transferId: Long = 0
    internal var offerId: Long? = null
    internal var rate: Int? = null
    val smsResendDelaySec = sessionInteractor.mobileConfigs.smsResendDelaySec

    private var password: String? = null

    val isPhone: Boolean
        get() = LoginHelper.checkIsNumber(emailOrPhone)

    companion object{
        const val PHONE_ATTRIBUTE = "+"
        const val EMAIL_ATTRIBUTE = "@"

        const val CLOSE_FRAGMENT = 0
        const val PASSWORD_VIEW  = 1
        const val SMS_CODE_VIEW  = 2
    }

    override fun attachView(view: LoginViewNew) {
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

    fun sendVerificationCode() {
        if (!checkInputData()) return

        utils.launchSuspend {
            viewState.blockInterface(true, true)
            fetchResult(SHOW_ERROR, checkLoginError = false) {
                when(isPhone) {
                    true -> sessionInteractor.getVerificationCode(null, LoginHelper.formatPhone(emailOrPhone))
                    false -> sessionInteractor.getVerificationCode(emailOrPhone, null)
                }
            }.also {
                if (it.error != null)
                    viewState.showError(true, it.error!!)
                else {
                    if (showingFragment == SMS_CODE_VIEW) viewState.updateTimerResendCode()
                    else viewState.showPasswordFragment(true, SMS_CODE_VIEW)
                }
            }
            viewState.blockInterface(false)
        }
    }

    fun onLoginClick(withSmsCode: Boolean = false) {
        if(password == null){
            viewState.showValidationError(true, LoginActivityNew.INVALID_PASSWORD)
            return
        }
   //     viewState.showValidationError(false, 0)
        if (!checkInputData()) return

        utils.launchSuspend {
            viewState.blockInterface(true, true)
            fetchResult(SHOW_ERROR, checkLoginError = false) {
                when (isPhone) {
                    true -> sessionInteractor.accountLogin(null, LoginHelper.formatPhone(emailOrPhone), password!!)
                    false -> sessionInteractor.accountLogin(emailOrPhone, null, password!!)
                }
            }
                    .also {
                        it.error?.let { e ->
                            viewState.showError(true, e)
                            logLoginEvent(Analytics.RESULT_FAIL)
                        }

                        it.isSuccess()?.let {
                            viewState.showErrorText(false)
                            if (withSmsCode) viewState.showChangePasswordDialog()
                            else openPreviousScreen()
                            logLoginEvent(Analytics.RESULT_SUCCESS)
                            registerPushToken()
                        }
                    }
            viewState.blockInterface(false)
        }
    }

    private fun suggestCreateAccount() {
        viewState.showLoginInfo(R.string.LNG_ERROR_ACCOUNT, R.string.LNG_ERROR_ACCOUNT_CREATE_RIDE)
    }

    fun openPreviousScreen(openSettingsScreen: Boolean = false) {
        if (nextScreen.isNullOrEmpty()) return
        when (nextScreen) {
            Screens.CLOSE_AFTER_LOGIN -> router.exit()
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
            Screens.PAYMENT_OFFER -> {
                utils.launchSuspend {
                    fetchData (NO_CACHE_CHECK) { transferInteractor.getTransfer(transferId) }
                            ?.let { transfer ->
                                val transferModel = transferMapper.toView(transfer)

                                router.navigateTo(Screens.ChangeMode(Screens.PASSENGER_MODE))
                                router.navigateTo(Screens.PaymentOffer(transferId, offerId, transferModel.dateRefund,
                                        transferModel.paymentPercentages!!, null))
                            }
                }
            }
            Screens.RATE_TRANSFER -> {
                router.navigateTo(Screens.Splash(transferId, rate, true))
            }
        }
        if(openSettingsScreen) { router.replaceScreen(Screens.Settings) }
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
                viewState.setError(false,
                        R.string.LNG_ERROR_EMAIL_PHONE,
                        Utils.phoneUtil.internationalExample(sessionInteractor.account.locale)
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
        when {
            showingFragment != null -> viewState.showPasswordFragment(false, CLOSE_FRAGMENT)
            else                    -> router.exit()
        }
    }

    fun setEmailOrPhone(email: String) {
        this.emailOrPhone = if (email.isEmpty()) null else email
    }

    fun setPassword(password: String) {
        this.password = if (password.isEmpty()) null else password
    }

    fun onPassForgot() = router.navigateTo(Screens.RestorePassword)

    private fun checkCarrierMode(): String {
        val groups = sessionInteractor.account.groups
        if (groups.indexOf(GROUP_CARRIER_DRIVER) >= 0) {
            if (groups.indexOf(GROUP_MANAGER_VIEW_TRANSFERS) >= 0) analytics.logProfile(Analytics.CARRIER_TYPE)
            else analytics.logProfile(Analytics.DRIVER_TYPE)
            return Screens.CARRIER_MODE
        }
        return Screens.REG_CARRIER
    }
}
