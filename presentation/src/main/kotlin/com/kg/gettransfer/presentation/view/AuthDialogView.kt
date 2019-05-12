package com.kg.gettransfer.presentation.view

interface AuthDialogView: BaseView {
    fun setEmail(email: String, withFocus: Boolean)
    fun setPhone(phone: String, withFocus: Boolean)
    fun redirectToLogin()
    fun onAccountCreated()
}