package com.kg.gettransfer.presentation.ui

import android.graphics.Bitmap
import android.graphics.Canvas

import android.os.Bundle

import android.support.annotation.CallSuper
import android.support.v4.content.ContextCompat

import android.view.View
import android.widget.ImageView

import android.widget.RelativeLayout

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

import kotlinx.android.synthetic.main.view_maps_pin.view.* //don't delete
import kotlinx.android.synthetic.main.view_car_pin.view.*  //don't delete

import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

import org.koin.android.ext.android.get

import timber.log.Timber

import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

abstract class BaseGoogleMapActivity : BaseActivity() {
    private lateinit var googleMapJob: Job
    protected lateinit var _mapView: MapView
    private lateinit var googleMap: GoogleMap
    protected lateinit var _btnCenter: ImageView

    private val compositeDisposable = Job()
    private val utils = AsyncUtils(get<CoroutineContexts>(), compositeDisposable)

    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {
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
                enablePinAnimation()
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

    protected fun setPolyline(polyline: PolylineModel, routeModel: RouteModel, driverMode: Boolean = false) {
        if(polyline.startPoint == null || polyline.finishPoint == null) {
            Timber.w("Polyline model is empty for route: $routeModel")
            return
        }
        val aBitmap = R.drawable.ic_map_label_a_orange
        val bBitmap = R.drawable.ic_map_label_b_orange

        processGoogleMap(false) {
            val bmPinA = getPinBitmap(routeModel.from, routeModel.dateTime, aBitmap)
            val bmPinB = getPinBitmap(routeModel.to!!, SystemUtils.formatDistance(this, routeModel.distance, true), bBitmap)
            if(Utils.isValidBitmap(bmPinA) && Utils.isValidBitmap(bmPinB)) {
                val startMakerOptions = createStartMarker(polyline.startPoint, bmPinA)
                val endMakerOptions = createEndMarker(polyline.finishPoint, bmPinB)
                addPolyline(polyline)
                addMarkers(startMakerOptions, endMakerOptions)
                moveCamera(polyline)
            }
        }
    }

    protected fun setPolylineWithoutInfo(polyline: PolylineModel, driverMode: Boolean = true) {
        if(polyline.startPoint == null || polyline.finishPoint == null) {
            return
        }

        val aBitmap = R.drawable.ic_map_label_a_orange
        val bBitmap = R.drawable.ic_map_label_b_orange

        processGoogleMap(false) {
            val bmPinA = getPinBitmapWithoutInfo(aBitmap)
            val bmPinB = getPinBitmapWithoutInfo(bBitmap)
            if(Utils.isValidBitmap(bmPinA) && Utils.isValidBitmap(bmPinB)) {
                val startMakerOptions = createStartMarker(polyline.startPoint, bmPinA)
                val endMakerOptions = createEndMarker(polyline.finishPoint, bmPinB)

                addPolyline(polyline)
                addMarkers(startMakerOptions, endMakerOptions)
                moveCamera(polyline)
            }
        }
    }

    private fun createEndMarker(finishPoint: LatLng, bmPinB: Bitmap): MarkerOptions? =
         MarkerOptions().position(finishPoint).icon(BitmapDescriptorFactory.fromBitmap(bmPinB))

    private fun createStartMarker(startPoint: LatLng, bmPinA: Bitmap): MarkerOptions? =
         MarkerOptions().position(startPoint).icon(BitmapDescriptorFactory.fromBitmap(bmPinA))


    private fun addPolyline(polyline: PolylineModel) {
        polyline.line?.let {
            it.width(10f).color(ContextCompat.getColor(this@BaseGoogleMapActivity, R.color.colorPolyline))
            googleMap.addPolyline(it)
        }
    }

    private fun addMarkers(startMakerOptions: MarkerOptions?, endMakerOptions: MarkerOptions?) {
        googleMap.addMarker(startMakerOptions)
        googleMap.addMarker(endMakerOptions)
    }

    private fun moveCamera(polyline: PolylineModel) {
        try {
            polyline.track?.let { googleMap.moveCamera(it) }
        } catch (e: Exception) {
            Timber.e(e)
        }
    }

    protected fun moveCameraWithDriverCoordinate(cameraUpdate: CameraUpdate) {
        processGoogleMap(false) {
            googleMap.setMaxZoomPreference(17f)
            googleMap.moveCamera(cameraUpdate) }
    }

    protected fun setPinForHourlyTransfer(placeName: String, info: String, point: LatLng, cameraUpdate: CameraUpdate, driver: Boolean = false) {
        val markerRes = R.drawable.ic_map_label_a_orange
        val bmPinA = getPinBitmap(placeName, info, markerRes)
        val startMakerOptions = MarkerOptions()
                .position(point)
                .icon(BitmapDescriptorFactory.fromBitmap(bmPinA))
        googleMap.addMarker(startMakerOptions)
        googleMap.moveCamera(cameraUpdate)
    }

    protected fun setPinForHourlyWithoutInfo(point: LatLng, cameraUpdate: CameraUpdate) {
        val bmPinA = getPinBitmapWithoutInfo(R.drawable.ic_map_label_a_orange)
        val startMakerOptions = MarkerOptions()
                .position(point)
                .icon(BitmapDescriptorFactory.fromBitmap(bmPinA))
        googleMap.addMarker(startMakerOptions)
        googleMap.moveCamera(cameraUpdate)
    }


    private fun getPinBitmap(placeName: String, info: String, drawable: Int): Bitmap {
        val pinLayout = layoutInflater.inflate(R.layout.view_maps_pin, null)
        pinLayout.tvPlace.text = placeName
        pinLayout.tvInfo.text = info
        pinLayout.tvPlaceMirror.text = placeName
        pinLayout.tvInfoMirror.text = info
        pinLayout.imgPin.setImageResource(drawable)
        return createBitmapFromView(pinLayout)
    }

    private fun getPinBitmapWithoutInfo(drawable: Int): Bitmap {
        val pinLayout = layoutInflater.inflate(R.layout.view_maps_pin_without_info, null)
        pinLayout.imgPin.setImageResource(drawable)
        return createBitmapFromView(pinLayout)
    }


    protected fun showTrack (track: CameraUpdate) {
        googleMap.animateCamera(track)
        _btnCenter.isVisible = false
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

    protected fun clearMarkersAndPolylines() = googleMap.clear()

    protected fun addCarToMap(resource: Int): Marker =
            MarkerOptions()
                    .visible(DEFAULT_VISIBILITY)
                    .position(DEFAULT_POSITION)
                    .icon(BitmapDescriptorFactory
                            .fromBitmap(createBitmapFromView(createViewWithCar(resource))))
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
