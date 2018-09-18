package com.kg.gettransfer.presentation.ui

import android.app.Activity

import android.content.Context

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color

import android.os.Build

import android.support.annotation.StringRes

import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog

import android.text.Editable
import android.text.TextWatcher

import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager

import android.widget.EditText
import android.widget.RelativeLayout

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.*

import com.google.maps.android.PolyUtil

import com.kg.gettransfer.R
import com.kg.gettransfer.domain.model.GTAddress
import com.kg.gettransfer.domain.model.DistanceUnit

import com.kg.gettransfer.presentation.model.CurrencyModel
import com.kg.gettransfer.presentation.model.RouteModel

import java.util.Locale
import java.util.regex.Pattern

import kotlinx.android.synthetic.main.view_maps_pin.view.*

import timber.log.Timber

internal class Utils {
    companion object {
        private val PHONE_PATTERN = Pattern.compile("^\\+\\d{11}$")
        @JvmField val DATE_TIME_PATTERN = "dd MMMM yyyy, HH:mm"
        
        fun getAlertDialogBuilder(context: Context): AlertDialog.Builder {
            return if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && false)
                AlertDialog.Builder(context, android.R.style.Theme_Material_Dialog_Alert)
            else AlertDialog.Builder(context)
        }
        
        fun showError(context: Context, finish: Boolean, message: String) {
            getAlertDialogBuilder(context)
                .setTitle(R.string.err_title)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, { dialog, _ ->
                   dialog.dismiss()
                   if(finish) (context as Activity).finish()
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show()
        }
        
        fun setCurrenciesDialogListener(context: Context, view: View, items: List<CharSequence>,
            listener: (Int) -> Unit) { setModelsDialogListener(context, view, R.string.currency, items, listener) }
        fun setLocalesDialogListener(context: Context, view: View, items: List<CharSequence>,
            listener: (Int) -> Unit) { setModelsDialogListener(context, view, R.string.application_language, items, listener) }
        fun setDistanceUnitsDialogListener(context: Context, view: View, items: List<CharSequence>,
            listener: (Int) -> Unit) { setModelsDialogListener(context, view, R.string.distance_units, items, listener) }

        fun setModelsDialogListener(context: Context, view: View, @StringRes titleId: Int, items: List<CharSequence>, 
                                    listener: (Int) -> Unit) {
            view.setOnClickListener {
                getAlertDialogBuilder(context)
                    .setTitle(titleId)
                    .setItems(items.toTypedArray()) { _, which -> listener(which) }
                    .setNegativeButton(android.R.string.cancel, null)
                    .show()
            }
        }
        
        fun checkPhone(phone: String?): Boolean {
            if(phone == null) return false
            return PHONE_PATTERN.matcher(phone.trim()).matches()
        }
        
        fun formatDistance(context: Context, distance: Int?, distanceUnit: DistanceUnit): String {
            if(distance == null) return ""
            var d = distance
            if(distanceUnit == DistanceUnit.Mi) d = DistanceUnit.km2Mi(distance)
            return context.getString(R.string.distance, d, distanceUnit.name)
        }
        
        fun setPins(activity: Activity, googleMap: GoogleMap, routeModel: RouteModel) {
            //Создание пинов с информацией
            val pinLayout = activity.layoutInflater.inflate(R.layout.view_maps_pin, null)

            pinLayout.tvPlace.text = routeModel.from
            //pinLayout.tvInfo.text = tvDateTimeTransfer.text
            pinLayout.tvPlaceMirror.text = routeModel.from
            //pinLayout.tvInfoMirror.text = tvDateTimeTransfer.text
            pinLayout.imgPin.setImageResource(R.drawable.map_label_a)
            val bmPinA = createBitmapFromView(pinLayout)

            val distance = formatDistance(activity, routeModel.distance, routeModel.distanceUnit)
            pinLayout.tvPlace.text = routeModel.to
            pinLayout.tvInfo.text = distance
            pinLayout.tvPlaceMirror.text = routeModel.to
            pinLayout.tvInfoMirror.text = distance
            pinLayout.imgPin.setImageResource(R.drawable.map_label_b)
            val bmPinB = createBitmapFromView(pinLayout)
            
            //Создание polyline

            // Для построения подробного маршрута
            val mPoints = arrayListOf<LatLng>()
            for(item in routeModel.polyLines) mPoints.addAll(PolyUtil.decode(item))

            // Для построения упрощённого маршрута (меньше точек)
            //val mPoints = PolyUtil.decode(routeInfo.overviewPolyline)

            val line = PolylineOptions().width(10f).color(ContextCompat.getColor(activity, R.color.colorPolyline))

            val latLngBuilder = LatLngBounds.Builder()
            for(i in mPoints.indices) {
                if(i == 0) {
                    val startMakerOptions = MarkerOptions()
                            .position(mPoints.get(i))
                            .icon(BitmapDescriptorFactory.fromBitmap(bmPinA))
                    googleMap.addMarker(startMakerOptions)
                } else if(i == mPoints.size - 1) {
                    val endMakerOptions = MarkerOptions()
                            .position(mPoints.get(i))
                            .icon(BitmapDescriptorFactory.fromBitmap(bmPinB))
                    googleMap.addMarker(endMakerOptions)
                }
                line.add(mPoints.get(i))
                latLngBuilder.include(mPoints.get(i))
            }
            googleMap.addPolyline(line)
        
            /*
            val sizeWidth = resources.displayMetrics.widthPixels
            val sizeHeight = mapView.height
            val latLngBounds = latLngBuilder.build()
            val track = CameraUpdateFactory.newLatLngBounds(latLngBounds, sizeWidth, sizeHeight, 150)
            */
            val track = CameraUpdateFactory.newLatLngBounds(latLngBuilder.build(), 15)
            try { googleMap.moveCamera(track) }
            catch(e: Exception) { Timber.e(e) }
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
	}
}

fun EditText.onTextChanged(cb: (String) -> Unit) {
    this.addTextChangedListener(object: TextWatcher {
        override fun afterTextChanged(s: Editable?) {}
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = cb(s.toString())
    })
}
