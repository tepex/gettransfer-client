package com.kg.gettransfer.presentation.presenter

import android.support.annotation.CallSuper

import com.arellomobile.mvp.InjectViewState

import com.kg.gettransfer.presentation.view.MainView

import ru.terrakok.cicerone.Router

import timber.log.Timber

@InjectViewState
class MainPresenter(router: Router): BasePresenter<MainView>(router) {
	override fun onFirstViewAttach()
	{
		Timber.d("MainPresenter.onFirstViewAttach()")
	}
}
