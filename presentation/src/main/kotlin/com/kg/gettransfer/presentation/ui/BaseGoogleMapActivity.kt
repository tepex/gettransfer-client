package com.kg.gettransfer.presentation.ui

import android.content.res.Configuration

import android.os.Build
import android.os.Bundle
import android.os.LocaleList

import android.support.annotation.CallSuper

import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView

import com.google.android.gms.maps.model.MapStyleOptions

import com.kg.gettransfer.R
import com.kg.gettransfer.domain.AsyncUtils

import java.util.Locale

import kotlin.coroutines.experimental.suspendCoroutine

import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.launch

import timber.log.Timber

abstract class BaseGoogleMapActivity: BaseActivity() {
    protected lateinit var googleMap: GoogleMap
    protected lateinit var _mapView: MapView

    private val compositeDisposable = Job()
    private val utils = AsyncUtils(coroutineContexts, compositeDisposable)

    companion object {
        @JvmField val MAP_VIEW_BUNDLE_KEY = "MapViewBundleKey"
    }

    @CallSuper
    protected override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val locale = systemInteractor.locale
        Locale.setDefault(locale)
        var config = Configuration()
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.N) config.locale = locale
        else config.setLocales(LocaleList(locale))
        createConfigurationContext(config) 
    }

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

    protected fun initGoogleMap(savedInstanceState: Bundle?) {
        val mapViewBundle = savedInstanceState?.getBundle(MAP_VIEW_BUNDLE_KEY)
        _mapView.onCreate(mapViewBundle)

        utils.launch {
            googleMap = getGoogleMapAsync()
            customizeGoogleMaps()
        }
    }

    private suspend fun getGoogleMapAsync(): GoogleMap = suspendCoroutine { cont ->
        _mapView.getMapAsync { cont.resume(it) }
    }

    protected open fun customizeGoogleMaps() {
        googleMap.uiSettings.setRotateGesturesEnabled(false)
        googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.style_json))
    }
}
