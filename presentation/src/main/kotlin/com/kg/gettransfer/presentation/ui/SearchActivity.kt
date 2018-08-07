package com.kg.gettransfer.presentation.ui

import android.Manifest

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.content.pm.PackageManager

import android.os.Build
import android.os.Bundle

import android.support.annotation.CallSuper

import android.support.design.widget.AppBarLayout
import android.support.design.widget.Snackbar

import android.support.v4.app.ActivityCompat

import android.support.v4.content.ContextCompat

import android.support.v7.app.AppCompatDelegate

import android.support.v7.widget.Toolbar

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
import com.kg.gettransfer.presentation.Screens
import com.kg.gettransfer.presentation.presenter.SearchPresenter
import com.kg.gettransfer.presentation.view.SearchView

import kotlinx.android.synthetic.main.activity_search.*
import kotlinx.android.synthetic.main.search_form.*
import kotlinx.android.synthetic.main.toolbar.*

import org.koin.android.ext.android.inject

import timber.log.Timber

class SearchActivity: MvpAppCompatActivity(), SearchView {
	@InjectPresenter
	internal lateinit var presenter: SearchPresenter
	
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
		
		val fade = Fade()
		fade.setDuration(500)
		getWindow().setEnterTransition(fade)
		
		val slide = Slide()
		slide.setDuration(500)
		getWindow().setReturnTransition(slide)
	}

	override fun onBackPressed() {
		presenter.onBackCommandClick() 
	}
}
