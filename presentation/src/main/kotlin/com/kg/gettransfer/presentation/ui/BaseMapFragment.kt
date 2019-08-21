package com.kg.gettransfer.presentation.ui

import android.os.Bundle
import androidx.annotation.CallSuper
import android.widget.ImageView
import com.arellomobile.mvp.MvpAppCompatFragment
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*

import com.kg.gettransfer.R
import com.kg.gettransfer.extensions.isVisible
import com.kg.gettransfer.presentation.model.PolylineModel
import com.kg.gettransfer.presentation.model.RouteModel
import com.kg.gettransfer.presentation.ui.helpers.MapHelper
import com.kg.gettransfer.utilities.CoroutineObserver
import kotlinx.android.synthetic.main.view_car_pin.view.*
import kotlinx.android.synthetic.main.view_maps_pin.view.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
//import leakcanary.AppWatcher
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

abstract class BaseMapFragment : MvpAppCompatFragment() {

    private lateinit var googleMapJob: Job
    private val coroutine by lazy { CoroutineObserver() }

    private lateinit var googleMap: GoogleMap
    protected lateinit var baseMapView: SupportMapFragment
    protected lateinit var baseBtnCenter: ImageView
    protected var isMapMovingByUser = false

    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycle.addObserver(coroutine)
    }

    protected open fun initMap() {}

    protected fun initMapView(savedInstanceState: Bundle?) {
        val baseMapViewBundle = savedInstanceState?.getBundle(MAP_VIEW_BUNDLE_KEY)
        baseMapView.onCreate(baseMapViewBundle)
        googleMapJob = coroutine.launch {
            googleMap = suspendCoroutine { cont -> baseMapView.getMapAsync { gm ->
                initMap()
                cont.resume(gm)
            } }
            customizeGoogleMaps(googleMap)
        }
    }

    protected open suspend fun customizeGoogleMaps(gm: GoogleMap) {
        gm.uiSettings.isRotateGesturesEnabled = false
        gm.uiSettings.isTiltGesturesEnabled = false
        gm.uiSettings.isMyLocationButtonEnabled = false
        gm.setMapStyle(MapStyleOptions.loadRawResourceStyle(requireContext(), R.raw.style_json))
        gm.setPadding(0, 0, 0, LABEL_VERTICAL_POSITION)
        gm.setOnCameraMoveStartedListener { reason ->
            if (reason == GoogleMap.OnCameraMoveStartedListener.REASON_GESTURE) {
                baseBtnCenter.isVisible = true
                isMapMovingByUser = true
                enablePinAnimation()
            } else {
                isMapMovingByUser = false
            }
        }
    }

    protected open fun enablePinAnimation() {}

    protected fun processGoogleMap(ignore: Boolean, block: (GoogleMap) -> Unit) {
        if (!googleMapJob.isCompleted && ignore) return
        coroutine.launch {
            if (!googleMapJob.isCompleted) googleMapJob.join()
            block(googleMap)
        }
    }

    protected fun setPolyline(polyline: PolylineModel, routeModel: RouteModel) {
        if (MapHelper.isEmptyPolyline(polyline, routeModel)) {
            return
        } else {
            processGoogleMap(false) {
                MapHelper.setPolyline(requireContext(), layoutInflater, googleMap, polyline, routeModel)
            }
        }
    }

    protected fun setPolylineWithoutInfo(polyline: PolylineModel) {
        if (polyline.startPoint == null || polyline.finishPoint == null) return

        val aBitmap = R.drawable.ic_map_label_a
        val bBitmap = R.drawable.ic_map_label_b

        processGoogleMap(false) {
            val bmPinA = getPinBitmapWithoutInfo(aBitmap)
            val bmPinB = getPinBitmapWithoutInfo(bBitmap)
            if (Utils.isValidBitmap(bmPinA) && Utils.isValidBitmap(bmPinB)) {
                val startMakerOptions = MapHelper.createStartMarker(polyline.startPoint, bmPinA)
                val endMakerOptions = MapHelper.createEndMarker(polyline.finishPoint, bmPinB)

                MapHelper.addPolyline(requireContext(), googleMap, polyline)
                MapHelper.addMarkers(googleMap, startMakerOptions, endMakerOptions)
                MapHelper.moveCamera(googleMap, polyline)
            }
        }
    }

    protected fun moveCameraWithDriverCoordinate(cameraUpdate: CameraUpdate) {
        processGoogleMap(false) {
            googleMap.setMaxZoomPreference(MAP_MAX_ZOOM)
            googleMap.moveCamera(cameraUpdate)
        }
    }

    protected fun setPinForHourlyTransfer(placeName: String, info: String, point: LatLng, cameraUpdate: CameraUpdate) {
        val markerRes = R.drawable.ic_map_label_a
        val bmPinA = MapHelper.getPinBitmap(layoutInflater, placeName, info, markerRes)
        val startMakerOptions = MarkerOptions().position(point).icon(BitmapDescriptorFactory.fromBitmap(bmPinA))

        googleMap.addMarker(startMakerOptions)
        googleMap.moveCamera(cameraUpdate)
    }

    protected fun setPinForHourlyWithoutInfo(point: LatLng, cameraUpdate: CameraUpdate) {
        val bmPinA = getPinBitmapWithoutInfo(R.drawable.ic_map_label_a)
        val startMakerOptions = MarkerOptions().position(point).icon(BitmapDescriptorFactory.fromBitmap(bmPinA))
        googleMap.addMarker(startMakerOptions)
        googleMap.moveCamera(cameraUpdate)
    }

    private fun getPinBitmapWithoutInfo(drawable: Int) =
            MapHelper.createBitmapFromView(
                    layoutInflater.inflate(R.layout.view_maps_pin_without_info, null).apply {
                        imgPin.setImageResource(drawable)
                    }
            )

    protected fun showTrack(track: CameraUpdate, listener: RouteIsCenteredListener? = null) {
        googleMap.animateCamera(track, object : GoogleMap.CancelableCallback {
            override fun onCancel() {}
            override fun onFinish() {
                listener?.invoke()
            }
        })
        baseBtnCenter.isVisible = false
    }

    protected fun clearMarkersAndPolylines() = googleMap.clear()

    protected fun addCarToMap(resource: Int): Marker = MarkerOptions()
            .visible(DEFAULT_VISIBILITY)
            .position(DEFAULT_POSITION)
            .icon(BitmapDescriptorFactory.fromBitmap(MapHelper.createBitmapFromView(createViewWithCar(resource))))
            .let { googleMap.addMarker(it) }

    private fun createViewWithCar(res: Int) =
            layoutInflater.inflate(R.layout.view_car_pin, null).apply { imgCarPin.setImageResource(res) }

    @CallSuper
    override fun onLowMemory() {
        baseMapView.onLowMemory()
        super.onLowMemory()
    }

    @CallSuper
    override fun onDestroyView() {
        baseMapView.onDestroy()
        googleMapJob.cancel()
        super.onDestroyView()
    }

    override fun onDestroy() {
        super.onDestroy()
//        AppWatcher.objectWatcher.watch(this)
    }

    companion object {
        const val MAP_MIN_ZOOM = 13f
        const val MAP_MAX_ZOOM = 17f
        const val MAP_VIEW_BUNDLE_KEY = "MapViewBundleKey"
        private const val LABEL_VERTICAL_POSITION = 12

        const val DEFAULT_VISIBILITY = false
        val DEFAULT_POSITION = LatLng(0.0, 0.0)
    }
}
