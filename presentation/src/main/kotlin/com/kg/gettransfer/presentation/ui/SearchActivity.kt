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
	val addressInteractor: AddressInteractor by inject()
	val coroutineContexts: CoroutineContexts by inject()
	
	private lateinit var current: SearchAddress  
	
	@ProvidePresenter
	fun createSearchPresenter(): SearchPresenter = SearchPresenter(coroutineContexts,
		                                                     router,
		                                                     addressInteractor)
	companion object {
		@JvmField val FADE_DURATION  = 500L
		@JvmField val SLIDE_DURATION = 500L
		@JvmField val EXTRA_ADDRESS_FROM = "address_from"
		@JvmField val EXTRA_ADDRESS_TO = "address_to"
	}

	init {
		AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
	}
	
	@CallSuper
	protected override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		/* Animation */
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
				
		searchFrom.initWidget(this, intent.getStringExtra(EXTRA_ADDRESS_FROM))
		searchTo.initWidget(this, intent.getStringExtra(EXTRA_ADDRESS_TO), true)
		searchTo.requestFocus()
	}
	
	fun onFocusChanged(current: SearchAddress) {
		this.current = current 
		current.requestAddresses()
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
	override fun setAddressFrom(address: GTAddress) { searchFrom.address = address }
	override fun setAddressList(list: List<GTAddress>) { addressList.adapter = AddressAdapter(presenter, list) }
	
	override fun setAddress(address: GTAddress) { 
		current.address = address
		// set current position
	}
	
	override fun setError(@StringRes errId: Int, finish: Boolean) { Utils.showError(this, errId, finish) }
}
