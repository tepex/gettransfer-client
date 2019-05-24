package com.kg.gettransfer.presentation.presenter

import android.os.Bundle

import com.arellomobile.mvp.InjectViewState

import com.kg.gettransfer.presentation.view.AboutView
import com.kg.gettransfer.presentation.view.Screens
import com.kg.gettransfer.utilities.Analytics

@InjectViewState
class AboutPresenter : BasePresenter<AboutView>() {
    internal var openMain = false

    fun closeAboutActivity() {
        if (openMain) router.replaceScreen(Screens.MainPassenger(true)) else router.exit()
    }

    fun logEvent(value: Int) {
        if (systemInteractor.isFirstLaunch) {
            systemInteractor.isFirstLaunch = false
            logExitStep(value)
        }
    }

    private fun logExitStep(value: Int) {
        analytics.logEvent(
            Analytics.EVENT_ONBOARDING,
            Bundle().apply { putInt(Analytics.EXIT_STEP, value) },
            mutableMapOf<String, Any>().apply { put(Analytics.EXIT_STEP, value) }
        )
    }
}
