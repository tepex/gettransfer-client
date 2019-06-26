package com.kg.gettransfer.presentation.view

import com.arellomobile.mvp.MvpView
import com.kg.gettransfer.presentation.presenter.SplashPresenter

interface SplashView: MvpView {
    fun initBuildConfigs(logsProvider: SplashPresenter.LateAccessLogs)
    fun checkLaunchType()
    fun onNeedAppUpdateInfo()
    fun dispatchAppState()
}