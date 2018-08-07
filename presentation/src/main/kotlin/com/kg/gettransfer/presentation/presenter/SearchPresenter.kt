package com.kg.gettransfer.presentation.presenter

import android.support.annotation.CallSuper

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter

import com.kg.gettransfer.presentation.view.SearchView

import timber.log.Timber

@InjectViewState
class SearchPresenter: MvpPresenter<SearchView>() {
	fun onBackCommandClick() {
		viewState.finish()
	}
}
