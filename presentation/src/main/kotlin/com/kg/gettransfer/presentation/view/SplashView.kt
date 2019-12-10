package com.kg.gettransfer.presentation.view

import moxy.MvpView

import java.util.Locale

interface SplashView: MvpView {

    fun checkLaunchType()

    fun onNeedAppUpdateInfo()

    fun dispatchAppState(locale: Locale)

    fun showAbout()
}
