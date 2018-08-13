package com.kg.gettransfer.presentation.presenter

import android.support.annotation.CallSuper

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter

import com.kg.gettransfer.domain.CoroutineContexts
import com.kg.gettransfer.domain.AsyncUtils

import com.kg.gettransfer.domain.interactor.AddressInteractor

import com.kg.gettransfer.presentation.view.SearchView

import ru.terrakok.cicerone.Router

import timber.log.Timber

@InjectViewState
class SearchPresenter(private val cc: CoroutineContexts,
	                  private val router: Router,
	                  private val addressInteractor: AddressInteractor): MvpPresenter<SearchView>() {
	
	override fun onFirstViewAttach() {
		val addr = addressInteractor.getCachedAddress()
		if(addr != null) viewState.setAddressFrom(addr.address)
	}
	
	fun onBackCommandClick() = viewState.finish()
}
