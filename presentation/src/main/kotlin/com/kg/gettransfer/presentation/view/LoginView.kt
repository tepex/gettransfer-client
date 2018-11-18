package com.kg.gettransfer.presentation.view

interface LoginView: BaseView {
    companion object {
        val EXTRA_SCREEN_FOR_RETURN = "${LoginView::class.java.name}.previous_screen"
        val EXTRA_EMAIL_TO_LOGIN    = "${LoginView::class.java.name}.email_to_login"
    }

    fun enableBtnLogin(enable: Boolean)
    fun showError(show: Boolean)
}
