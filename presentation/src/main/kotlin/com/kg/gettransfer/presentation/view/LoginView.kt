package com.kg.gettransfer.presentation.view

import com.arellomobile.mvp.MvpView

interface LoginView : MvpView {
    fun finish()
    fun showError()
    fun hideError()
}
