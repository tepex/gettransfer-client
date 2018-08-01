package com.kg.gettransfer.presentation.presenter

import android.support.annotation.CallSuper

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.arellomobile.mvp.MvpView

import com.kg.gettransfer.presentation.view.MainView

//import javax.inject.Inject

import timber.log.Timber

@InjectViewState
class MainPresenter: MvpPresenter<MainView>()
{
	@CallSuper
	override fun onDestroy()
	{
		super.onDestroy()
	}
	
	override fun onFirstViewAttach()
	{
		Timber.d("MainPresenter.onFirstViewAttach()")
	}
}
