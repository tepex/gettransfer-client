package com.kg.gettransfer.presentation.ui

import android.graphics.Bitmap

import android.os.Bundle

import android.support.annotation.CallSuper

import android.view.View
import android.widget.ImageView

import com.google.android.gms.maps.CameraUpdate
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.*

import com.kg.gettransfer.R
import com.kg.gettransfer.domain.AsyncUtils
import com.kg.gettransfer.domain.CoroutineContexts
import com.kg.gettransfer.extensions.isVisible

import com.kg.gettransfer.presentation.model.PolylineModel
import com.kg.gettransfer.presentation.model.RouteModel
import com.kg.gettransfer.presentation.ui.helpers.MapHelper

import kotlinx.android.synthetic.main.view_maps_pin.view.*
import kotlinx.android.synthetic.main.view_car_pin.view.*

import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

import org.koin.android.ext.android.get

import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

abstract class BaseGoogleMapActivity : BaseActivity() {
    private lateinit var googleMapJob: Job
    protected lateinit var _mapView: MapView
    private lateinit var googleMap: GoogleMap
    protected lateinit var _btnCenter: ImageView
    protected var isMapMovingByUser = false

    private val compositeDisposable = Job()
    private val utils = AsyncUtils(get<CoroutineContexts>(), compositeDisposable)

    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    @CallSuper
    override fun onStart() {
        super.onStart()
        _mapView.onStart()
        initMap()
    }

    @CallSuper
    override fun onResume() {
        super.onResume()
        _mapView.onResume()
    }

    @CallSuper
    override fun onPause() {
        _mapView.onPause()
        super.onPause()
    }

    protected open fun initMap() {}

    @CallSuper
    override fun onStop() {
        _mapView.onStop()
        super.onStop()
    }

    @CallSuper
    override fun onDestroy() {
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
            customizeGoogleMaps(googleMap)
        }
    }

    protected open suspend fun customizeGoogleMaps(gm: GoogleMap) {
        gm.uiSettings.isRotateGesturesEnabled = false
        gm.uiSettings.isTiltGesturesEnabled = false
        gm.uiSettings.isMyLocationButtonEnabled = false
        gm.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.style_json))
        gm.setPadding(0, 0, 0, LABEL_VERTICAL_POSITION)
        gm.setOnCameraMoveStartedListener {
            if (it == GoogleMap.OnCameraMoveStartedListener.REASON_GESTURE) {
                _btnCenter.isVisible = true
                isMapMovingByUser = true
                enablePinAnimation()
            } else {
                isMapMovingByUser = false
            }
        }
    }

    protected open fun enablePinAnimation() {}

    protected fun processGoogleMap(ignore: Boolean, block: (GoogleMap) -> Unit) {
        if(!googleMapJob.isCompleted && ignore) return
        utils.launch {
            if(!googleMapJob.isCompleted) googleMapJob.join()
            block(googleMap)
        }
    }

    protected fun setPolyline(polyline: PolylineModel, routeModel: RouteModel) {
        if (MapHelper.isEmptyPolyline(polyline, routeModel)) {
            return
        } else {
            processGoogleMap(false) {
                MapHelper.setPolyline(this, layoutInflater, googleMap, polyline, routeModel)
            }
        }
    }

    protected fun setPolylineWithoutInfo(polyline: PolylineModel) {
        if(polyline.startPoint == null || polyline.finishPoint == null) {
            return
        }

        val aBitmap = R.drawable.ic_map_label_a
        val bBitmap = R.drawable.ic_map_label_b

        processGoogleMap(false) {
            val bmPinA = getPinBitmapWithoutInfo(aBitmap)
            val bmPinB = getPinBitmapWithoutInfo(bBitmap)
            if(Utils.isValidBitmap(bmPinA) && Utils.isValidBitmap(bmPinB)) {
                val startMakerOptions = MapHelper.createStartMarker(polyline.startPoint, bmPinA)
                val endMakerOptions = MapHelper.createEndMarker(polyline.finishPoint, bmPinB)

                MapHelper.addPolyline(this, googleMap, polyline)
                MapHelper.addMarkers(googleMap, startMakerOptions, endMakerOptions)
                MapHelper.moveCamera(googleMap, polyline)
            }
        }
    }

    protected fun moveCameraWithDriverCoordinate(cameraUpdate: CameraUpdate) {
        processGoogleMap(false) {
            googleMap.setMaxZoomPreference(17f)
            googleMap.moveCamera(cameraUpdate) }
    }

    protected fun setPinForHourlyTransfer(placeName: String, info: String, point: LatLng, cameraUpdate: CameraUpdate) {
        val markerRes = R.drawable.ic_map_label_a
        val bmPinA = MapHelper.getPinBitmap(layoutInflater, placeName, info, markerRes)
        val startMakerOptions = MarkerOptions()
                .position(point)
                .icon(BitmapDescriptorFactory.fromBitmap(bmPinA))
        googleMap.addMarker(startMakerOptions)
        googleMap.moveCamera(cameraUpdate)
    }

    protected fun setPinForHourlyWithoutInfo(point: LatLng, cameraUpdate: CameraUpdate) {
        val bmPinA = getPinBitmapWithoutInfo(R.drawable.ic_map_label_a)
        val startMakerOptions = MarkerOptions()
                .position(point)
                .icon(BitmapDescriptorFactory.fromBitmap(bmPinA))
        googleMap.addMarker(startMakerOptions)
        googleMap.moveCamera(cameraUpdate)
    }

    private fun getPinBitmapWithoutInfo(drawable: Int): Bitmap {
        val pinLayout = layoutInflater.inflate(R.layout.view_maps_pin_without_info, null)
        pinLayout.imgPin.setImageResource(drawable)
        return MapHelper.createBitmapFromView(pinLayout)
    }

    protected fun showTrack (track: CameraUpdate, listener: RouteIsCenteredListener? = null) {
        googleMap.animateCamera(track, object: GoogleMap.CancelableCallback {
            override fun onCancel() {}
            override fun onFinish() {
                listener?.invoke()
            }
        })
        _btnCenter.isVisible = false
    }

    protected fun clearMarkersAndPolylines() = googleMap.clear()

    protected fun addCarToMap(resource: Int): Marker =
            MarkerOptions()
                    .visible(DEFAULT_VISIBILITY)
                    .position(DEFAULT_POSITION)
                    .icon(BitmapDescriptorFactory
                            .fromBitmap(MapHelper.createBitmapFromView(createViewWithCar(resource))))
                    .let { googleMap.addMarker(it) }

    private fun createViewWithCar(res: Int): View {
        val view = layoutInflater.inflate(R.layout.view_car_pin, null)
        view.imgCarPin.setImageResource(res)
        return view
    }


    companion object {
        const val MAP_MIN_ZOOM = 13f
        const val MAP_VIEW_BUNDLE_KEY = "MapViewBundleKey"
        private const val LABEL_VERTICAL_POSITION = 12

        const val DEFAULT_VISIBILITY = false
        val DEFAULT_POSITION = LatLng(0.0, 0.0)
    }
}

typealias RouteIsCenteredListener = () ->Unit
