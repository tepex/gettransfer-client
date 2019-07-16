package com.kg.gettransfer.presentation.presenter

import com.arellomobile.mvp.InjectViewState

import com.kg.gettransfer.presentation.view.AboutView
import com.kg.gettransfer.presentation.view.Screens
import com.kg.gettransfer.utilities.Analytics

@InjectViewState
class AboutPresenter : BasePresenter<AboutView>() {
    internal var openMain = false

    fun closeAboutActivity() {
        if (!openMain) router.newRootScreen(Screens.MainPassenger(true)) else router.exit()
    }

    fun logExitStep(value: Int) {
        if (systemInteractor.isFirstLaunch) {
            systemInteractor.isFirstLaunch = false
            analytics.logEvent(Analytics.EVENT_ONBOARDING, Analytics.EXIT_STEP, value)
        }
    }
}
