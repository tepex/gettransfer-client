package com.kg.gettransfer.presentation.ui

import android.os.Bundle

import android.support.annotation.CallSuper

import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView

import com.kg.gettransfer.domain.AsyncUtils

import kotlin.coroutines.experimental.suspendCoroutine

import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.launch

abstract class BaseGoogleMapActivity: BaseActivity() {
    protected lateinit var googleMap: GoogleMap
    protected lateinit var _mapView: MapView
    
    private val compositeDisposable = Job()
    private val utils = AsyncUtils(coroutineContexts, compositeDisposable)

    @CallSuper
    protected override fun onStart() {
        super.onStart()
        _mapView.onStart()
    }

    @CallSuper
    protected override fun onResume() {
        super.onResume()
        _mapView.onResume()
    }

    @CallSuper
    protected override fun onPause() {
        _mapView.onPause()
        super.onPause()
    }

    @CallSuper
    protected override fun onStop() {
        _mapView.onStop()
        super.onStop()
    }

    @CallSuper
    protected override fun onDestroy() {
        _mapView.onDestroy()
        compositeDisposable.cancel()
        super.onDestroy()
    }

    @CallSuper
    override fun onLowMemory() {
        _mapView.onLowMemory()
        super.onLowMemory()
    }

    protected fun initGoogleMap(mapViewBundle: Bundle?) {
        _mapView.onCreate(mapViewBundle)

        utils.launch {
            googleMap = getGoogleMapAsync()
            customizeGoogleMaps()
        }
    }

    private suspend fun getGoogleMapAsync(): GoogleMap = suspendCoroutine { cont ->
        _mapView.getMapAsync { cont.resume(it) }
    }

    protected abstract fun customizeGoogleMaps()
}
