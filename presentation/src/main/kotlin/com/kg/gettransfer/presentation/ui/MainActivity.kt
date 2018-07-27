package com.kg.gettransfer.presentation.ui

import android.Manifest

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

import android.view.View

import com.arellomobile.mvp.MvpAppCompatActivity
import com.arellomobile.mvp.presenter.InjectPresenter

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener
import com.google.android.gms.maps.MapView

import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions

import com.kg.gettransfer.R
import com.kg.gettransfer.presentation.presenter.MainPresenter
import com.kg.gettransfer.presentation.view.MainView

import kotlinx.android.synthetic.main.activity_main.*

import timber.log.Timber

class MainActivity: MvpAppCompatActivity(), MainView {
	@InjectPresenter
	lateinit var presenter: MainPresenter
	
	private var permissionsGranted = true
	private var isFirst = true
	private var gmap: GoogleMap? = null
	private var centerMarker: Marker? = null

	companion object
	{
		private val PERMISSION_REQUEST = 2211
		private val MAP_VIEW_BUNDLE_KEY = "MapViewBundleKey"
		private val PERMISSIONS = arrayOf<String>(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
	}
	
	init {
		AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
	}
	
	@CallSuper
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
		{
			permissionsGranted = checkPermission()
			presenter.setPermissionsGranted(permissionsGranted)
		}
		
		setContentView(R.layout.activity_main)
		
		val tb: Toolbar = this.toolbar as Toolbar
		tb.setTitle(R.string.app_name)
		
		setSupportActionBar(tb)
		getSupportActionBar()?.setDisplayShowTitleEnabled(true)
		getSupportActionBar()?.setDisplayHomeAsUpEnabled(true)
		getSupportActionBar()?.setHomeButtonEnabled(true);
		
		val abl: AppBarLayout = this.appbar as AppBarLayout
		abl.bringToFront()
		
		var mapViewBundle: Bundle? = null
		if(savedInstanceState != null)
		{
			isFirst = false
			mapViewBundle = savedInstanceState.getBundle(MAP_VIEW_BUNDLE_KEY)
		}
		
		Timber.d("Permissions granted: ${permissionsGranted}")
		if(permissionsGranted) startGoogleMap(mapViewBundle)
		else Snackbar.make(rootView, "Permissions not granted", Snackbar.LENGTH_SHORT).show()
	}
	
	private fun startGoogleMap(mapViewBundle: Bundle?) {
		mapView.onCreate(mapViewBundle)
		mapView.getMapAsync({ gmap -> 
			this.gmap = gmap
			this.gmap!!.setMyLocationEnabled(true)
			this.gmap!!.setOnMyLocationButtonClickListener(OnMyLocationButtonClickListener {
				onClickMyLocation()
				true
			})
//			this.gmap!!.setOnCameraMoveListener(this)
			presenter.updateCurrentLocation()
		})
	}
	
	@CallSuper
	override fun onSaveInstanceState(outState: Bundle) {
		super.onSaveInstanceState(outState)
		var mapViewBundle = outState.getBundle(MAP_VIEW_BUNDLE_KEY)
		if(mapViewBundle == null) {
			mapViewBundle = Bundle()
			outState.putBundle(MAP_VIEW_BUNDLE_KEY, mapViewBundle)
		}
		if(permissionsGranted) mapView.onSaveInstanceState(mapViewBundle)
	}
	
	@CallSuper
	override fun onStart() {
		super.onStart()
		if(permissionsGranted) mapView.onStart()
	}
	
	@CallSuper
	override fun onResume() {
		super.onResume()
		if(permissionsGranted) mapView.onResume()
	}
	
	@CallSuper
	override fun onPause() {
		if(permissionsGranted) mapView.onPause()
		super.onPause()
	}
	
	@CallSuper
	override fun onStop() {
		if(permissionsGranted) mapView.onStop()
		super.onStop()
	}
		
	@CallSuper
	override fun onDestroy() {
		if(permissionsGranted) mapView.onDestroy()
		super.onDestroy()
	}
	
	@CallSuper
	override fun onLowMemory() {
		if(permissionsGranted) mapView.onLowMemory()
		super.onLowMemory()
	}
	
	/**
	 * @return true — не требуется разрешение пользователя
	 */
	private fun checkPermission(): Boolean {
		if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
		   ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
				ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_REQUEST)
				return false
		}
		return true
	}
	
	override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
		if(requestCode != PERMISSION_REQUEST) return
		if(grantResults.size == 2 && 
			grantResults[0] == PackageManager.PERMISSION_GRANTED &&
			grantResults[1] == PackageManager.PERMISSION_GRANTED) recreate()
		else finish()
	}

	/* MainView */
	override fun onClickMyLocation() {
		presenter.updateCurrentLocation()
	}
}
