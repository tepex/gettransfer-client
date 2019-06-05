package com.kg.gettransfer.presentation.view

import com.kg.gettransfer.domain.ApiException

interface SmsCodeView : BaseView {
    fun updateTimerResendCode()
    fun showErrorText(show: Boolean, text: String? = null)
    fun showValidationError(b: Boolean, invaliD_EMAIL: Int)
    fun showChangePasswordDialog()
}