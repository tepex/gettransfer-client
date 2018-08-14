package com.kg.gettransfer.presentation.presenter

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter

import com.kg.gettransfer.presentation.view.AddressView

import timber.log.Timber

@InjectViewState
class SearchAddressPresenter():  MvpPresenter<AddressView> {

	val 
	fun onTextChanged(text: String) {
		text.trim()
	}
}