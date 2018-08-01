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

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener
import com.google.android.gms.maps.MapView

import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions

import com.kg.gettransfer.R
import com.kg.gettransfer.presentation.presenter.StartPresenter
import com.kg.gettransfer.presentation.view.StartView

import org.koin.android.ext.android.inject
import org.koin.standalone.KoinComponent

import kotlinx.android.synthetic.main.fragment_start.*

import timber.log.Timber

const val MY_LOCATION_BUTTON_INDEX = 2
const val COMPASS_BUTTON_INDEX = 5

const val MAP_VIEW_BUNDLE_KEY = "MapViewBundleKey"

class StartFragment: MvpAppCompatFragment(), StartView, BackButtonListener {
	private var isFirst = true
	private var gmap: GoogleMap? = null
	private var centerMarker: Marker? = null
	@InjectPresenter
	lateinit var presenter: StartPresenter
	
	private val focusListener = View.OnFocusChangeListener {_, hasFocus ->
		if(hasFocus) {
			Timber.d("start transition")
			(activity as MainActivity).startSearchScreen()
		}
	}

	companion object {
		@Suppress("UNUSED_PARAMETER")
		fun getNewInstance(data: Any?): StartFragment {
			return StartFragment()
		}
	}
	
	@ProvidePresenter
	fun createStartPresenter(): StartPresenter = StartPresenter((activity as MainActivity).router)

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
		                      savedInstanceState: Bundle?): View? {
		return inflater.inflate(R.layout.fragment_start, container, false)
    }
    
	@CallSuper
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		var mapViewBundle: Bundle? = null
		if(savedInstanceState != null)
		{
			isFirst = false
			mapViewBundle = savedInstanceState.getBundle(MAP_VIEW_BUNDLE_KEY)
		}
		mapView?.onCreate(mapViewBundle)
		mapView?.getMapAsync({ gmap -> 
			this.gmap = gmap
			/*
			this.gmap!!.setOnMyLocationButtonClickListener(OnMyLocationButtonClickListener {
				onClickMyLocation()
				true
			})
			this.gmap!!.setOnCameraMoveListener(this)
			presenter.updateCurrentLocation()
			*/
			customizeGoogleMaps()
			presenter.updateCurrentLocation()
		})
		searchFrom.address.setOnFocusChangeListener(focusListener)
		searchTo.address.setOnFocusChangeListener(focusListener)
	}
	
	@CallSuper
	override fun onSaveInstanceState(outState: Bundle) {
		super.onSaveInstanceState(outState)
		var mapViewBundle = outState.getBundle(MAP_VIEW_BUNDLE_KEY)
		if(mapViewBundle == null) {
			mapViewBundle = Bundle()
			outState.putBundle(MAP_VIEW_BUNDLE_KEY, mapViewBundle)
		}
		if((activity as MainActivity).permissionsGranted) mapView?.onSaveInstanceState(mapViewBundle)
	}

	@CallSuper
	override fun onStart() {
		super.onStart()
		if((activity as MainActivity).permissionsGranted) mapView?.onStart()
	}
	
	@CallSuper
	override fun onResume() {
		super.onResume()
		if((activity as MainActivity).permissionsGranted) mapView?.onResume()
	}
	
	@CallSuper
	override fun onPause() {
		if((activity as MainActivity).permissionsGranted) mapView?.onPause()
		super.onPause()
	}
	
	@CallSuper
	override fun onStop() {
		if((activity as MainActivity).permissionsGranted) mapView?.onStop()
		super.onStop()
	}
	
	@CallSuper
	override fun onDestroy() {
		if((activity as MainActivity).permissionsGranted) mapView?.onDestroy()
		super.onDestroy()
	}

	@CallSuper
	override fun onLowMemory() {
		if((activity as MainActivity).permissionsGranted) mapView?.onLowMemory()
		super.onLowMemory()
	}
	
	override fun onBackPressed(): Boolean {
		presenter.onBackCommandClick()
		return true
	}

	/**
	 * Грязный хак — меняем положение нативной кнопки 'MyLocation'
	 * https://stackoverflow.com/questions/36785542/how-to-change-the-position-of-my-location-button-in-google-maps-using-android-st
	 */
	 
	private fun customizeGoogleMaps() {
		gmap!!.setMyLocationEnabled(true)
		val parent = (mapView?.findViewById(1) as View).parent as View
		val myLocationBtn = parent.findViewById(MY_LOCATION_BUTTON_INDEX) as View
		val rlp = myLocationBtn.getLayoutParams() as RelativeLayout.LayoutParams 
		// position on right bottom
		rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0)
		rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE)
		
		rlp.setMargins(0, 0, 
			resources.getDimension(R.dimen.location_button_margin_end).toInt(),
			resources.getDimension(R.dimen.location_button_margin_bottom).toInt())
		
		val compassBtn = parent.findViewById(COMPASS_BUTTON_INDEX) as View
		val rlp1 = compassBtn.getLayoutParams() as RelativeLayout.LayoutParams 
		// position on right bottom
		rlp1.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0)
		rlp1.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE)
		
		rlp1.setMargins(resources.getDimension(R.dimen.compass_button_margin_start).toInt(), 0, 
			0, resources.getDimension(R.dimen.compass_button_margin_bottom).toInt())
	}
}
