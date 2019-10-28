package com.kg.gettransfer.presentation.ui.dialogs

import com.google.android.gms.maps.CameraUpdate
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.kg.gettransfer.R
import com.kg.gettransfer.domain.ApiException
import com.kg.gettransfer.domain.DatabaseException
import com.kg.gettransfer.presentation.model.RouteModel
import com.kg.gettransfer.presentation.presenter.BaseMapDialogPresenter
import com.kg.gettransfer.presentation.ui.Utils
import com.kg.gettransfer.presentation.ui.helpers.MapHelper
import com.kg.gettransfer.presentation.view.BaseMapDialogView

abstract class BaseMapBottomSheetDialogFragment : BaseBottomSheetDialogFragment(),
    BaseMapDialogView,
    OnMapReadyCallback {

    abstract fun getPresenter(): BaseMapDialogPresenter<*>

    private lateinit var googleMap: GoogleMap

    private var mapFragment: SupportMapFragment? = null

    protected fun initMapFragment(layoutId: Int) {

        if (mapFragment == null) {
            mapFragment = SupportMapFragment.newInstance()
            mapFragment?.getMapAsync(this)
        }

        mapFragment?.let {
            childFragmentManager.beginTransaction().replace(layoutId, it).commit()
        }
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        customizeGoogleMaps(googleMap)
        getPresenter().onMapReady()
    }

    private fun customizeGoogleMaps(gm: GoogleMap) {
        gm.uiSettings.isRotateGesturesEnabled = false
        gm.uiSettings.isTiltGesturesEnabled = false
        gm.uiSettings.isMyLocationButtonEnabled = false
        gm.uiSettings.isScrollGesturesEnabled = false
        gm.uiSettings.isZoomGesturesEnabled = false
    }

    override fun setRoute(routeModel: RouteModel?, from: String, startPoint: LatLng) {
        if (routeModel != null) {
            val polyline = Utils.getPolyline(routeModel)
            if (MapHelper.isEmptyPolyline(polyline, routeModel)) {
                return
            } else {
                context?.let { MapHelper.setPolyline(it, layoutInflater, googleMap, polyline, routeModel) }
            }
        } else {
            setPinForHourlyTransfer(from, "", startPoint, Utils.getCameraUpdateForPin(startPoint))
        }
    }

    @Suppress("UNUSED_PARAMETER")
    private fun setPinForHourlyTransfer(
        placeName: String,
        info: String,
        point: LatLng,
        cameraUpdate: CameraUpdate,
        driver: Boolean = false
    ) {
        val markerRes = R.drawable.ic_map_label_a
        val bmPinA = MapHelper.getPinBitmap(layoutInflater, placeName, info, markerRes)
        val startMakerOptions = MarkerOptions().position(point).icon(BitmapDescriptorFactory.fromBitmap(bmPinA))
        googleMap.addMarker(startMakerOptions)
        googleMap.moveCamera(cameraUpdate)
    }

    override fun blockInterface(block: Boolean, useSpinner: Boolean) {}
    override fun setError(finish: Boolean, errId: Int, vararg args: String?) {}
    override fun setError(e: ApiException) {}
    override fun setError(e: DatabaseException) {}
    override fun setTransferNotFoundError(transferId: Long, dismissCallBack: (() -> Unit)?) {}
}
