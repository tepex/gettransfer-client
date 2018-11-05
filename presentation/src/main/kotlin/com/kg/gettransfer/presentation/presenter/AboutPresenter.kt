package com.kg.gettransfer.presentation.presenter

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.kg.gettransfer.presentation.view.AboutView

@InjectViewState
class AboutPresenter: MvpPresenter<AboutView>() {
	fun onBackCommandClick() {
		viewState.finish()
	}
}
