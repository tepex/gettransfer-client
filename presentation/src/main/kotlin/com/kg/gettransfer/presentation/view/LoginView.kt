package com.kg.gettransfer.presentation.view

import com.arellomobile.mvp.MvpView

interface LoginView: BaseLoadingView {
    fun enableBtnLogin(enable: Boolean)
}
