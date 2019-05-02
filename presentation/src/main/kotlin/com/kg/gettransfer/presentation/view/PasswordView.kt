package com.kg.gettransfer.presentation.view

import com.kg.gettransfer.domain.ApiException

interface PasswordView {
    fun showValidationError(errString: Int?)
    fun showError(show: Boolean, error: ApiException)
}