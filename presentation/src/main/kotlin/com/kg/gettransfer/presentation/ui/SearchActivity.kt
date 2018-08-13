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
	private val addressInteractor: AddressInteractor by inject()
	private val coroutineContexts: CoroutineContexts by inject()
	
	private val FADE_DURATION  = 500L
	private val SLIDE_DURATION = 500L
	
	companion object {
		const val EXTRA_ADDRESS_PREDICTION = "address_prediction"
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
		
		setContentView(R.layout.activity_search)
		
		setSupportActionBar(toolbar as Toolbar)
		supportActionBar?.setDisplayShowTitleEnabled(false)
		supportActionBar?.setDisplayHomeAsUpEnabled(true)
		supportActionBar?.setDisplayShowHomeEnabled(true)
		(toolbar as Toolbar).setNavigationOnClickListener { presenter.onBackCommandClick() }
		
		searchTo.requestFocus()
		searchTo.address.addTextChangedListener(object: TextWatcher {
			override fun afterTextChanged(s: Editable?) {
				Timber.d("after text changed: %s", s)
			}
			override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
			override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
		})
		
		val prediction = intent?.getStringExtra(EXTRA_ADDRESS_PREDICTION)
		searchTo.address.setText(prediction)
		Timber.d("prediction: %s", prediction)
		
		addressList.layoutManager = LinearLayoutManager(this)
		addressList.adapter = AddressAdapter(this)
		
		val fade = Fade()
		fade.setDuration(FADE_DURATION)
		getWindow().setEnterTransition(fade)
		
		val slide = Slide()
		slide.setDuration(SLIDE_DURATION)
		getWindow().setReturnTransition(slide)
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
	
	override fun setAddressFrom(addressFrom: String) {
		searchFrom.address.setText(addressFrom)
	}
	
	override fun setAddressTo(addressTo: String) {
		searchTo.address.setText(addressTo)
	}
	
	override fun setError(@StringRes errId: Int, finish: Boolean) {
		Utils.showError(this, errId, finish)
	}
}
