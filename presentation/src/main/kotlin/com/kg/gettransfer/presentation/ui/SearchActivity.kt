package com.kg.gettransfer.presentation.ui

import android.Manifest

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.content.pm.PackageManager

import android.os.Build
import android.os.Bundle

import android.support.annotation.CallSuper
import android.support.annotation.StringRes

import android.support.design.widget.AppBarLayout
import android.support.design.widget.Snackbar

import android.support.v4.app.ActivityCompat

import android.support.v4.content.ContextCompat

import android.support.v7.app.AppCompatDelegate

import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.Toolbar

import android.text.Editable
import android.text.TextWatcher

import android.transition.Explode
import android.transition.Fade
import android.transition.Slide

import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager

import com.arellomobile.mvp.MvpAppCompatActivity
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter

import com.kg.gettransfer.R

import com.kg.gettransfer.domain.CoroutineContexts

import com.kg.gettransfer.domain.model.GTAddress

import com.kg.gettransfer.domain.interactor.AddressInteractor

import com.kg.gettransfer.presentation.Screens
import com.kg.gettransfer.presentation.model.AddressPair
import com.kg.gettransfer.presentation.presenter.SearchPresenter
import com.kg.gettransfer.presentation.view.SearchView

import kotlinx.android.synthetic.main.activity_search.*
import kotlinx.android.synthetic.main.search_form.*
import kotlinx.android.synthetic.main.toolbar.*

import org.koin.android.ext.android.inject

import ru.terrakok.cicerone.Router

import timber.log.Timber

class SearchActivity: MvpAppCompatActivity(), SearchView {
	@InjectPresenter
	internal lateinit var presenter: SearchPresenter
	
	private val router: Router by inject()
	private val addressInteractor: AddressInteractor by inject()
	private val coroutineContexts: CoroutineContexts by inject()
	
	private val FADE_DURATION  = 500L
	private val SLIDE_DURATION = 500L
	
	companion object {
		const val EXTRA_ADDRESSES = "addresses"
	}
	
	@ProvidePresenter
	fun createSearchPresenter(): SearchPresenter = SearchPresenter(coroutineContexts,
		                                                     router,
		                                                     addressInteractor)
	init {
		AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
	}
	
	@CallSuper
	protected override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		/* Анимация */
		val fade = Fade()
		fade.setDuration(FADE_DURATION)
		getWindow().setEnterTransition(fade)
		
		val slide = Slide()
		slide.setDuration(SLIDE_DURATION)
		getWindow().setReturnTransition(slide)
		
		/* init UI */
		setContentView(R.layout.activity_search)
		
		setSupportActionBar(toolbar as Toolbar)
		supportActionBar?.setDisplayShowTitleEnabled(false)
		supportActionBar?.setDisplayHomeAsUpEnabled(true)
		supportActionBar?.setDisplayShowHomeEnabled(true)
		(toolbar as Toolbar).setNavigationOnClickListener { presenter.onBackCommandClick() }
		
		addressList.layoutManager = LinearLayoutManager(this)
		
		val addressPair: AddressPair = intent.getParcelableExtra(EXTRA_ADDRESSES)
		searchFrom.initWidget(addressList, addressPair.from)
		searchTo.initWidget(addressList, addressPair.to)
		searchTo.requestFocus()
		
		presenter.requestAddressListByPrediction(addressPair.to)
		
		
		/* по 3-м символам */
		
		searchFrom.onTextChanged { presenter.requestAddressListByPrediction(it) } 
		searchTo.onTextChanged { presenter.requestAddressListByPrediction(it) }
	}

	@CallSuper
	protected override fun onDestroy() {
		
		super.onDestroy();
	}
	
	override fun onBackPressed() {
		presenter.onBackCommandClick() 
	}
	
	/* SearchView */
	override fun blockInterface(block: Boolean) {}
	
	override fun setAddressFrom(address: String) {
		searchFrom.text = address
	}
	
	override fun setAddressTo(address: String) {
		searchTo.text = address
	}
	
	override fun setAddressList(list: List<GTAddress>) {
		addressList.adapter = AddressAdapter(presenter, list)
	}
	
	override fun setError(@StringRes errId: Int, finish: Boolean) {
		Utils.showError(this, errId, finish)
	}
}
