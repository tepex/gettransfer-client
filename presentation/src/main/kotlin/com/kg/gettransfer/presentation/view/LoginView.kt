package com.kg.gettransfer.presentation.view

interface LoginView: BaseView {
    fun enableBtnLogin(enable: Boolean)
    fun showError(show: Boolean)
}
