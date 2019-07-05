package com.kg.gettransfer.presentation.view

import com.arellomobile.mvp.MvpView
import com.kg.gettransfer.presentation.presenter.SplashPresenter

import java.util.Locale

interface SplashView: MvpView {

    fun initBuildConfigs(logsProvider: SplashPresenter.LateAccessLogs)

    fun checkLaunchType()

    fun onNeedAppUpdateInfo()

    fun dispatchAppState(locale: Locale)
}
