package com.kg.gettransfer.presentation.ui

import android.content.pm.PackageManager

import android.os.Bundle

import android.support.annotation.CallSuper

import android.support.v4.app.Fragment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import android.widget.RelativeLayout

import com.arellomobile.mvp.MvpAppCompatFragment
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter

import com.kg.gettransfer.R

import com.kg.gettransfer.presentation.presenter.StartSearchPresenter
import com.kg.gettransfer.presentation.view.StartSearchView
/*
import org.koin.android.ext.android.inject
import org.koin.standalone.KoinComponent
*/

import kotlinx.android.synthetic.main.fragment_start_search.*

import timber.log.Timber

class StartSearchFragment: MvpAppCompatFragment(), StartSearchView, BackButtonListener {
	@InjectPresenter
	lateinit var presenter: StartSearchPresenter
	
	private val focusListener = View.OnFocusChangeListener {_, hasFocus ->
		if(hasFocus) Timber.d("start transition")
	}

	companion object {
		@Suppress("UNUSED_PARAMETER")
		fun getNewInstance(data: Any?): StartSearchFragment {
			return StartSearchFragment()
		}
	}

	@ProvidePresenter
	fun createStartSearchPresenter(): StartSearchPresenter = StartSearchPresenter((activity as MainActivity).router)
	
	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
		                      savedInstanceState: Bundle?): View? {
		return inflater.inflate(R.layout.fragment_start_search, container, false)
    }
    
	@CallSuper
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
	}
	
	override fun onBackPressed(): Boolean {
		presenter.onBackCommandClick()
		return true
	}
}
