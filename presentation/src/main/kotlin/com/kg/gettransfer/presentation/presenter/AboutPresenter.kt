package com.kg.gettransfer.presentation.presenter

import android.os.Bundle
import com.arellomobile.mvp.InjectViewState

import com.kg.gettransfer.presentation.view.AboutView
import com.kg.gettransfer.presentation.view.Screens
import com.kg.gettransfer.utilities.Analytics

@InjectViewState
class AboutPresenter: BasePresenter<AboutView>() {
	internal var openMain: Boolean = false

	fun closeAboutActivity() {
		if(openMain) router.navigateTo(Screens.Main)
		else router.exit()
	}

	fun logEvent(value: Int) {
		val map = mutableMapOf<String, Any>()
		map[Analytics.EXIT_STEP] = value

		val bundle = Bundle()
		bundle.putInt(Analytics.EXIT_STEP, value)

		analytics.logEvent(Analytics.EVENT_ONBOARDING, bundle, map)
	}
}
