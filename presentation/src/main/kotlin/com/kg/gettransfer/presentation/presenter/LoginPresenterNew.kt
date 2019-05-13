package com.kg.gettransfer.presentation.presenter

import com.arellomobile.mvp.InjectViewState
import com.kg.gettransfer.R

import com.kg.gettransfer.domain.model.Account.Companion.GROUP_CARRIER_DRIVER
import com.kg.gettransfer.domain.model.Account.Companion.GROUP_MANAGER_VIEW_TRANSFERS
import com.kg.gettransfer.extensions.firstSign
import com.kg.gettransfer.extensions.internationalExample

import com.kg.gettransfer.presentation.ui.LoginActivityNew
import com.kg.gettransfer.presentation.ui.Utils

import com.kg.gettransfer.presentation.view.LoginViewNew
import com.kg.gettransfer.presentation.view.Screens

import com.kg.gettransfer.utilities.Analytics
import java.lang.IllegalArgumentException

@InjectViewState
class LoginPresenterNew : BasePresenter<LoginViewNew>() {
    internal var showingFragment: Int? = null

    internal var emailOrPhone: String? = null
    internal var screenForReturn: String? = null
    internal var transferId: Long = 0
    internal var offerId: Long? = null
    internal var rate: Int? = null
    val smsResendDelaySec = systemInteractor.mobileConfigs.smsResendDelaySec

    private var password: String? = null

    var isPhone = false

    companion object{
        const val PHONE_ATTRIBUTE = "+"
        const val EMAIL_ATTRIBUTE = "@"

        const val CLOSE_FRAGMENT = 0
        const val PASSWORD_VIEW  = 1
        const val SMS_CODE_VIEW  = 2
    }

    fun onContinueClick() {
        if (emailOrPhone == null
                || !checkIfEmailOrPhone()
                || !validateInput()) return

        //TODO проверка наличия аккаунта на бэке
        /*val accountExist = false  //ответ от сервера
        if (accountExist) {
            //переходим на фрагмент пароля
        } else {
            suggestCreateAccount()
        }*/
        viewState.showPasswordFragment(true, PASSWORD_VIEW)
    }

    private fun validateInput(): Boolean {
        isPhone = checkIsNumber()
        if(!isPhone) {
            if(!Utils.checkEmail(emailOrPhone)) {
                viewState.showValidationError(true, LoginActivityNew.INVALID_EMAIL)
                return false
            }
        } else {
            if (!Utils.checkPhone(formatPhone())) {
                viewState.showValidationError(true, LoginActivityNew.INVALID_PHONE)
                return false
            }
        }
        return true
    }

    fun sendVerificationCode() {
        /*utils.launchSuspend {
            viewState.blockInterface(true, true)
            fetchResult(SHOW_ERROR, checkLoginError = false) {
                when(isPhone) {
                    true -> systemInteractor.getVerificationCode(null, formatPhone())
                    false -> systemInteractor.getVerificationCode(emailOrPhone, null)
                }
            }.also {
                if(it.error != null) {
                    viewState.showError(true, it.error!!)
                } else {
                    when (passwordFragmentIsShowing) {
                        true -> viewState.updateTimerResendCode()
                        false -> viewState.showPasswordFragment(true, isPhone)
                    }
                }
            }
            viewState.blockInterface(false)
        }*/
        if (!checkInputData()) return

        utils.launchSuspend {
            viewState.blockInterface(true, true)
            fetchResult(SHOW_ERROR, checkLoginError = false) {
                when(isPhone) {
                    true -> systemInteractor.getVerificationCode(null, formatPhone())
                    false -> systemInteractor.getVerificationCode(emailOrPhone, null)
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
                    true -> systemInteractor.accountLogin(null, formatPhone(), password!!)
                    false -> systemInteractor.accountLogin(emailOrPhone, null, password!!)
                }
            }
                    .also {
                        it.error?.let { e ->
                            if (e.isNoUser())
                                viewState.showError(true, e)
                            logLoginEvent(Analytics.RESULT_FAIL)
                        }

                        it.isSuccess()?.let {
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
        if (screenForReturn.isNullOrEmpty()) return
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
                    fetchData (NO_CACHE_CHECK) { transferInteractor.getTransfer(transferId) }
                            ?.let { transfer ->
                                val transferModel = transferMapper.toView(transfer)

                                router.navigateTo(Screens.ChangeMode(Screens.PASSENGER_MODE))
                                router.navigateTo(Screens.PaymentOffer(transferId, offerId, transferModel.dateRefund,
                                        transferModel.paymentPercentages!!, null))
                            }
                }
            }
            Screens.RATE_TRANSFER -> router.navigateTo(Screens.Splash(transferId, rate, true))
        }
        if(openSettingsScreen) router.navigateTo(Screens.Settings)
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
                        Utils.phoneUtil.internationalExample(systemInteractor.locale.country)
                )
                false
            }
        }



    private fun checkIsNumber(): Boolean {
        if (emailOrPhone?.firstSign() == "+") return true
        return try {
            emailOrPhone?.toLong()
            true
        } catch (e: IllegalArgumentException) {
            false
        }
    }

    /*private fun identifyLoginType(input: String) =
            input.contains("@")*/

    private fun formatPhone() =
            emailOrPhone?.let {
                when {
                    it.firstSign() == "8" -> "+7${it.substring(1)}"
                    it.firstSign() == "7" -> "+$it"
                    else                  -> it
            }
        }

    private fun logLoginEvent(result: String) {
        val map = mutableMapOf<String, Any>()
        map[Analytics.STATUS] = result

        analytics.logEvent(Analytics.EVENT_LOGIN, createStringBundle(Analytics.STATUS, result), map)
    }

    override fun onBackCommandClick() {
        if (showingFragment != null) {
            viewState.showPasswordFragment(false, CLOSE_FRAGMENT)
        } else {
            router.exit()
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
        val groups = systemInteractor.account.groups
        if (groups.indexOf(GROUP_CARRIER_DRIVER) >= 0) {
            if (groups.indexOf(GROUP_MANAGER_VIEW_TRANSFERS) >= 0) analytics.logProfile(Analytics.CARRIER_TYPE)
            else analytics.logProfile(Analytics.DRIVER_TYPE)
            return Screens.CARRIER_MODE
        }
        return Screens.REG_CARRIER
    }
}
