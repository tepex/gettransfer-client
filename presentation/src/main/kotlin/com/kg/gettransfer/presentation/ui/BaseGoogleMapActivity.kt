package com.kg.gettransfer.presentation.ui

import android.content.res.Configuration

import android.graphics.Bitmap
import android.graphics.Canvas

import android.os.Build
import android.os.Bundle
import android.os.LocaleList

import android.support.annotation.CallSuper
import android.support.v4.content.ContextCompat

import android.view.View

import android.widget.RelativeLayout

import com.google.android.gms.maps.CameraUpdate
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions

import com.kg.gettransfer.R
import com.kg.gettransfer.domain.AsyncUtils
import com.kg.gettransfer.domain.CoroutineContexts

import com.kg.gettransfer.presentation.model.PolylineModel
import com.kg.gettransfer.presentation.model.RouteModel

import kotlinx.android.synthetic.main.view_maps_pin.view.*

import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

import org.koin.android.ext.android.get

import timber.log.Timber

//import java.util.*

import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

abstract class BaseGoogleMapActivity: BaseActivity() {
    private lateinit var googleMapJob: Job
    protected lateinit var _mapView: MapView
    protected lateinit var googleMap: GoogleMap

    private val compositeDisposable = Job()
    private val utils = AsyncUtils(get<CoroutineContexts>(), compositeDisposable)

    companion object {
        @JvmField val MAP_VIEW_BUNDLE_KEY = "MapViewBundleKey"
    }

    @CallSuper
    protected override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        val locale = systemInteractor.locale
//        Log.i("findLocale", locale.language)
//        Locale.setDefault(locale)
//
//        var res = resources
//        var config = Configuration(res.configuration)
//        @Suppress("DEPRECATION")
//        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.N) config.locale = locale
//        else config.setLocales(LocaleList(locale))
//        createConfigurationContext(config)
    }

    @CallSuper
    protected override fun onStart() {
        super.onStart()
        _mapView.onStart()
        initMap()
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

    protected open fun initMap() {}

    @CallSuper
    protected override fun onStop() {
        _mapView.onStop()
        super.onStop()
    }

    @CallSuper
    protected override fun onDestroy() {
        _mapView.onDestroy()
        compositeDisposable.cancel()
        googleMapJob.cancel()
        super.onDestroy()
    }

    @CallSuper
    override fun onLowMemory() {
        _mapView.onLowMemory()
        super.onLowMemory()
    }

    protected fun initMapView(savedInstanceState: Bundle?) {
        val mapViewBundle = savedInstanceState?.getBundle(MAP_VIEW_BUNDLE_KEY)
        _mapView.onCreate(mapViewBundle)
        googleMapJob = utils.launch {
            googleMap = suspendCoroutine { cont -> _mapView.getMapAsync { cont.resume(it) } }
            customizeGoogleMaps()
        }
    }

    protected suspend open fun customizeGoogleMaps() {
        googleMap.uiSettings.setRotateGesturesEnabled(false)
        googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.style_json))
    }
    
    protected fun processGoogleMap(block: () -> Unit) {
        utils.launch {
            if(!googleMapJob.isCompleted) googleMapJob.join()
            block()
        }
    }
    
    protected fun setPolyline(polyline: PolylineModel, routeModel: RouteModel) {
        if(polyline.startPoint == null || polyline.finishPoint == null) {
            Timber.w("Polyline model is empty for route: $routeModel")
            return
        }
        val bmPinA = getPinBitmap(routeModel.from, routeModel.dateTime, R.drawable.ic_map_label_a)
        val bmPinB = getPinBitmap(routeModel.to, Utils.formatDistance(this, routeModel.distance, routeModel.distanceUnit), R.drawable.ic_map_label_b)

        val startMakerOptions = MarkerOptions()
                .position(polyline.startPoint)
                .icon(BitmapDescriptorFactory.fromBitmap(bmPinA))
        val endMakerOptions = MarkerOptions()
                .position(polyline.finishPoint)
                .icon(BitmapDescriptorFactory.fromBitmap(bmPinB))

        processGoogleMap {
            if(polyline.line != null) {
                polyline.line.width(10f).color(ContextCompat.getColor(this@BaseGoogleMapActivity, R.color.colorPolyline))
                googleMap.addPolyline(polyline.line)
            }

            googleMap.addMarker(startMakerOptions)
            googleMap.addMarker(endMakerOptions)

            try {
                polyline.track?.let { googleMap.moveCamera(it) }
                //showTrack(polyline.track)
            }
            catch(e: Exception) { Timber.e(e) }
        }
    }

    protected fun setPinForHourlyTransfer(placeName: String, info: String, point: LatLng) {
        val bmPinA = getPinBitmap(placeName, info, R.drawable.ic_map_label_a)
        val startMakerOptions = MarkerOptions()
                .position(point)
                .icon(BitmapDescriptorFactory.fromBitmap(bmPinA))
        googleMap.addMarker(startMakerOptions)
        val zoom = resources.getInteger(R.integer.map_min_zoom).toFloat()
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(point, zoom))
    }

    private fun getPinBitmap(placeName: String, info: String, drawable: Int): Bitmap{
        val pinLayout = layoutInflater.inflate(R.layout.view_maps_pin, null)
        pinLayout.tvPlace.text = placeName
        pinLayout.tvInfo.text = info
        pinLayout.tvPlaceMirror.text = placeName
        pinLayout.tvInfoMirror.text = info
        pinLayout.imgPin.setImageResource(drawable)
        return createBitmapFromView(pinLayout)
    }

    protected fun showTrack (track: CameraUpdate){
        googleMap.animateCamera(track)
    }

    private fun createBitmapFromView(v: View): Bitmap {
        v.layoutParams = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT)
        v.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED))
        v.layout(0, 0, v.measuredWidth, v.measuredHeight)
        val bitmap = Bitmap.createBitmap(v.measuredWidth,
                v.measuredHeight,
                Bitmap.Config.ARGB_8888)

        v.layout(v.left, v.top, v.right, v.bottom)
        v.draw(Canvas(bitmap))
        return bitmap
    }

    protected fun clearMarkersAndPolylines(){ googleMap.clear() }
}
