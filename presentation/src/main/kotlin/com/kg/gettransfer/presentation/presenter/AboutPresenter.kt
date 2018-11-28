package com.kg.gettransfer.presentation.presenter

import com.arellomobile.mvp.InjectViewState

import com.kg.gettransfer.presentation.view.AboutView
import com.kg.gettransfer.presentation.view.Screens

@InjectViewState
class AboutPresenter: BasePresenter<AboutView>() {
	internal var openMain: Boolean = false

	fun closeAboutActivity() {
		if(openMain) router.navigateTo(Screens.Main)
		else router.exit()
	}
}
