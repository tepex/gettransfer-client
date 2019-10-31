package com.kg.gettransfer.presentation.view

/**
 * View for [Sign Up]
 *
 * @author П. Густокашин (Diwixis)
 */
interface SignUpView : BaseView {
    fun showRegisterSuccessDialog()
    fun showValidationErrorDialog(phoneExample: String)
    fun updateTextPhone(phone: String)
    fun updateTextEmail(email: String)
}
