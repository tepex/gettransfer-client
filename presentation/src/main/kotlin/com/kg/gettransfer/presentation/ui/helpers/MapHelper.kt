package com.kg.gettransfer.presentation.ui.helpers

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.core.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.widget.RelativeLayout
import androidx.annotation.DrawableRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.graphics.drawable.toBitmap
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.kg.gettransfer.R
import com.kg.gettransfer.presentation.model.PolylineModel
import com.kg.gettransfer.presentation.model.RouteModel
import com.kg.gettransfer.presentation.ui.SystemUtils
import com.kg.gettransfer.presentation.ui.Utils
import com.kg.gettransfer.presentation.ui.utils.DistanceUtils
import kotlinx.android.synthetic.main.view_maps_pin.view.*
import timber.log.Timber

object MapHelper {

    fun getPinBitmap(layoutInflater: LayoutInflater, placeName: String, info: String, drawable: Int): Bitmap {
        val pinLayout = layoutInflater.inflate(R.layout.view_maps_pin, null)
        pinLayout.tvPlace.text = placeName
        pinLayout.tvInfo.text = info
        pinLayout.tvPlaceMirror.text = placeName
        pinLayout.tvInfoMirror.text = info
        pinLayout.imgPin.setImageResource(drawable)
        return createBitmapFromView(pinLayout)
    }

    fun createBitmapFromView(v: View): Bitmap {
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

    fun isEmptyPolyline(polyline: PolylineModel, routeModel: RouteModel): Boolean {
        if (polyline.startPoint == null || polyline.finishPoint == null) {
            Timber.w("Polyline model is empty for route: $routeModel")
            return true
        }
        return false
    }

    fun setPolyline(context: Context, layoutInflater: LayoutInflater, map: GoogleMap,
                    polyline: PolylineModel, routeModel: RouteModel) {

        val aBitmap = R.drawable.ic_map_label_a
        val bBitmap = R.drawable.ic_map_label_b

        val distance = routeModel.distance
            ?: DistanceUtils.getPointToPointDistance(routeModel.fromPoint, routeModel.toPoint)

        val bmPinA = getPinBitmap(layoutInflater, routeModel.from, routeModel.dateTime, aBitmap)
        val bmPinB = getPinBitmap(layoutInflater, routeModel.to!!,
            SystemUtils.formatDistance(context, distance, routeModel.isRoundTrip, true), bBitmap)

        if (Utils.isValidBitmap(bmPinA) && Utils.isValidBitmap(bmPinB)) {
            val startMakerOptions = polyline.startPoint?.let { createStartMarker(it, bmPinA) }
            val endMakerOptions = polyline.finishPoint?.let { createEndMarker(it, bmPinB) }
            addPolyline(context, map, polyline)
            addMarkers(map, startMakerOptions, endMakerOptions)
            moveCamera(map, polyline)
        }
    }

    fun moveCamera(map: GoogleMap, polyline: PolylineModel) {
        try {
            polyline.track?.let { map.moveCamera(it) }
        } catch (e: Exception) {
            Timber.e(e)
        }
    }

    fun addMarkers(map: GoogleMap, startMakerOptions: MarkerOptions?, endMakerOptions: MarkerOptions?) {
        map.addMarker(startMakerOptions)
        map.addMarker(endMakerOptions)
    }

    fun addPolyline(context: Context, map: GoogleMap, polyline: PolylineModel) =
        polyline.line?.let {
            it.width(10f).color(ContextCompat.getColor(context, R.color.colorPolyline))
            map.addPolyline(it)
        }


    fun createEndMarker(finishPoint: LatLng, bmPinB: Bitmap): MarkerOptions? =
        createMarker(finishPoint, bmPinB)

    fun createStartMarker(startPoint: LatLng, bmPinA: Bitmap): MarkerOptions? =
        createMarker(startPoint, bmPinA)

    private fun createMarker(position: LatLng, pin: Bitmap) =
        MarkerOptions().position(position).icon(BitmapDescriptorFactory.fromBitmap(pin))

    private fun createBitmapFromDrawable(context: Context, @DrawableRes drawableId: Int): Bitmap? =
        AppCompatResources.getDrawable(context, drawableId)?.toBitmap()

    /**
     * Adds a marker to this map
     *
     * @param location where we need to add marker
     * @param drawableId drawable resource reference to marker
     *
     * @return Marker that was added to the map
     */
    fun addMarker(
        context: Context,
        map: GoogleMap,
        location: LatLng,
        @DrawableRes drawableId: Int
    ): Marker? {
        val markerBitmap = createBitmapFromDrawable(context, drawableId)
        return markerBitmap?.let { map.addMarker(createMarker(location, markerBitmap)) }
    }

    /**
     * Animates the movement of the camera to the position defined in the location
     */
    fun animateCamera(context: Context, map: GoogleMap, location: LatLng) {
        val zoom = context.resources.getInteger(R.integer.map_min_zoom).toFloat()
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(location, zoom))
    }
}