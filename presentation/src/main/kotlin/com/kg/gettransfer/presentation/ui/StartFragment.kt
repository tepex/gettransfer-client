package com.kg.gettransfer.presentation.ui

import android.content.pm.PackageManager

import android.os.Bundle

import android.support.annotation.CallSuper

import android.support.v4.app.Fragment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.kg.gettransfer.R
//import com.kg.gettransfer.presentation.presenter.StartPresenter
import com.kg.gettransfer.presentation.view.StartView

import kotlinx.android.synthetic.main.fragment_start.*

import timber.log.Timber

class StartFragment: Fragment(), StartView {
	companion object {
		fun getNewInstance(data: Any?): StartFragment {
			return StartFragment()
		}
	}

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
		                      savedInstanceState: Bundle?): View? {
		return inflater.inflate(R.layout.fragment_start, container, false)
    }
    
	@CallSuper
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		Timber.d("Start fragment created")
	}
}
