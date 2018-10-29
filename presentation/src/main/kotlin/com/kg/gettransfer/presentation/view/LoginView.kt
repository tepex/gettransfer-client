package com.kg.gettransfer.presentation.view

import com.arellomobile.mvp.MvpView

interface LoginView: BaseView {
    fun enableBtnLogin(enable: Boolean)
    fun showError(show: Boolean)
}
