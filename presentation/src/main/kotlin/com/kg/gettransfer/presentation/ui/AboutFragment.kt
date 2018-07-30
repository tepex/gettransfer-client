package com.kg.gettransfer.presentation.ui

import android.content.pm.PackageManager

import android.os.Bundle

import android.support.annotation.CallSuper

import android.support.v4.app.Fragment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.kg.gettransfer.R
//import com.kg.gettransfer.presentation.presenter.AboutPresenter
import com.kg.gettransfer.presentation.view.AboutView

import kotlinx.android.synthetic.main.fragment_about.*

import timber.log.Timber

class AboutFragment: Fragment(), AboutView {
	var versionName: String = ""
	
	companion object {
		fun getNewInstance(data: Any): AboutFragment {
			return AboutFragment()
		}
	}

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
		                      savedInstanceState: Bundle?): View? {
		return inflater.inflate(R.layout.fragment_about, container, false)
    }
    
	@CallSuper
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		try {
			versionName = activity!!.packageManager.getPackageInfo(activity!!.packageName, 0).versionName
			Timber.d("Version name: $versionName")
		}
		catch(e: PackageManager.NameNotFoundException) {
			Timber.e(e)
		}
	}
}
