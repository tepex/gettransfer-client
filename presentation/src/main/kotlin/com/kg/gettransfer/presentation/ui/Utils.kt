package com.kg.gettransfer.presentation.ui

import android.app.Activity
import android.app.Dialog

import android.content.Context

import android.graphics.Bitmap
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable

import android.os.Build

import android.support.annotation.DrawableRes
import android.support.annotation.StringRes
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog

import android.telephony.TelephonyManager

import android.text.Editable
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextWatcher
import android.text.style.ImageSpan

import android.util.DisplayMetrics
import android.util.Patterns

import android.view.View

import android.widget.EditText
import android.widget.Toast

import com.google.android.gms.maps.CameraUpdate
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.PolylineOptions

import com.google.maps.android.PolyUtil

import com.kg.gettransfer.R

import com.kg.gettransfer.domain.model.DistanceUnit

import com.kg.gettransfer.presentation.model.Mappers
import com.kg.gettransfer.presentation.model.PolylineModel
import com.kg.gettransfer.presentation.model.RouteModel

import com.yandex.metrica.impl.ob.it

import io.michaelrocks.libphonenumber.android.PhoneNumberUtil
import kotlinx.android.synthetic.main.view_hourly_picker.*

import java.text.SimpleDateFormat

import java.util.Date
import java.util.Locale
import java.util.regex.Pattern

import timber.log.Timber

internal class Utils {
    companion object {
        private val PHONE_PATTERN = Pattern.compile("^\\+\\d{11,13}$")
        private val EMAIL_PATTERN = Patterns.EMAIL_ADDRESS
        @JvmField val DATE_TIME_PATTERN = "dd MMMM yyyy, HH:mm"
        const val MAX_BITMAP_SIZE = 4096

        private lateinit var phoneUtil: PhoneNumberUtil
        
        fun getAlertDialogBuilder(context: Context): AlertDialog.Builder {
            return if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && false)
                AlertDialog.Builder(context, android.R.style.Theme_Material_Dialog_Alert)
            else AlertDialog.Builder(context)
        }
        
        fun showError(context: Context, finish: Boolean, message: String, onClose: (() -> Unit)? = null) {
            getAlertDialogBuilder(context)
                .setTitle(R.string.LNG_ERROR)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, { dialog, _ ->
                   dialog.dismiss()
                   if(finish) (context as Activity).finish()
                   onClose?.invoke()
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show()
        }

        fun showAlertCancelRequest(context: Context, listener: (Boolean) -> Unit){
            getAlertDialogBuilder(context)
                    .setTitle(R.string.LNG_CANCEL_CONFIRM)
                    .setPositiveButton(android.R.string.yes) { _, _ -> listener(true) }
                    .setNegativeButton(android.R.string.no)  { _, _ -> listener(false) }
                    .show()
        }

        fun showScreenRedirectingAlert(context: Context, title: String, message: String, navigate: () -> Unit){
            getAlertDialogBuilder(context)
                    .setTitle(title)
                    .setMessage(message)
                    .setPositiveButton(android.R.string.ok){ dialog, _ ->
                        dialog.dismiss()
                        navigate()
                    }
                    .show()
        }

        fun showEmptyFieldsForTransferRequest(context: Context, message: String){
            getAlertDialogBuilder(context)
                    .setTitle(context.resources.getString(R.string.LNG_RIDE_CANT_CREATE))
                    .setMessage(message)
                    .setPositiveButton(android.R.string.ok){ dialog, _ ->
                        dialog.dismiss()
                    }
                    .show()
        }

        fun setCurrenciesDialogListener(context: Context, view: View, items: List<CharSequence>,
            listener: (Int) -> Unit) { setModelsDialogListener(context, view, R.string.LNG_CURRENCY, items, listener) }
        fun setLocalesDialogListener(context: Context, view: View, items: List<CharSequence>,
            listener: (Int) -> Unit) { setModelsDialogListener(context, view, R.string.LNG_LANGUAGE, items, listener) }
        fun setDistanceUnitsDialogListener(context: Context, view: View, items: List<CharSequence>,
            listener: (Int) -> Unit) { setModelsDialogListener(context, view, R.string.LNG_DISTANCE_UNIT, items, listener) }
        fun setEndpointsDialogListener(context: Context, view: View, items: List<CharSequence>,
                                       listener: (Int) -> Unit) { setModelsDialogListener(context, view, R.string.endpoint, items, listener)}

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

        fun checkEmail(email: String?) = EMAIL_PATTERN.matcher(email ?: "").matches()
        //fun checkPhone(phone: String?) = PHONE_PATTERN.matcher(phone?.trim() ?: "").matches()
        fun checkPhone(phone: String): Boolean {
            try {
                if(!PHONE_PATTERN.matcher(phone.trim()).matches()) return false
                return phoneUtil.isValidNumber(phoneUtil.parse(phone, null))
            }
            catch(e: Exception) {
                Timber.w("phone parse error: $phone", e)
                return false
            }
        }

        fun formatDistance(context: Context, distance: Int?, distanceUnit: DistanceUnit): String {
            if(distance == null) return ""
            var d = distance
            if(distanceUnit == DistanceUnit.Mi) d = DistanceUnit.km2Mi(distance)
            return context.getString(R.string.LNG_RIDE_DISTANCE).plus(": $d ").plus(distanceUnit.name)
        }

        fun formatDuration(context: Context, duration: Int) =
            context.getString(R.string.LNG_RIDE_DURATION).plus(": $duration ").plus(context.getString(R.string.LNG_H))

        fun getPolyline(routeModel: RouteModel): PolylineModel {
            var mPoints = mutableListOf<LatLng>()
            var line: PolylineOptions? = null
            val latLngBuilder = LatLngBounds.Builder()
            var track: CameraUpdate?

            if(routeModel.polyLines != null) {
                for(item in routeModel.polyLines) mPoints.addAll(PolyUtil.decode(item))

                // Для построения упрощённого маршрута (меньше точек)
                //val mPoints = PolyUtil.decode(routeInfo.overviewPolyline)

                line = PolylineOptions()

                for(i in mPoints.indices) {
                    line.add(mPoints.get(i))
                    latLngBuilder.include(mPoints.get(i))
                }
            } else {
                mPoints.add(Mappers.point2LatLng(routeModel.fromPoint))
                mPoints.add(Mappers.point2LatLng(routeModel.toPoint))

                for(i in mPoints.indices) latLngBuilder.include(mPoints.get(i))
            }
            Timber.d("latLngBuilder: $latLngBuilder")
            track = try { CameraUpdateFactory.newLatLngBounds(latLngBuilder.build(), 150) }
            catch(e: Exception) {
                Timber.w("Create order error: $latLngBuilder", e)
                null
            }
            return PolylineModel(mPoints.firstOrNull(), mPoints.getOrNull(mPoints.size - 1), line, track)
        }

        fun getFormattedDate(locale: Locale, dateToLocal: Date) = SimpleDateFormat(DATE_TIME_PATTERN, locale).format(dateToLocal)
        
        /*fun setPins(activity: Activity, googleMap: GoogleMap, routeModel: RouteModel) {
        
            //Создание пинов с информацией
            val pinLayout = activity.layoutInflater.inflate(R.layout.view_maps_pin, null)

            pinLayout.tvPlace.text = routeModel.from
            pinLayout.tvInfo.text = routeModel.dateTime
            pinLayout.tvPlaceMirror.text = routeModel.from
            pinLayout.tvInfoMirror.text = routeModel.dateTime
            pinLayout.imgPin.setImageResource(R.drawable.ic_map_label_a)
            val bmPinA = createBitmapFromView(pinLayout)

            val distance = formatDistance(activity, routeModel.distance, routeModel.distanceUnit)
            pinLayout.tvPlace.text = routeModel.to
            pinLayout.tvInfo.text = distance
            pinLayout.tvPlaceMirror.text = routeModel.to
            pinLayout.tvInfoMirror.text = distance
            pinLayout.imgPin.setImageResource(R.drawable.ic_map_label_b)
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
        
            *//*
            val sizeWidth = resources.displayMetrics.widthPixels
            val sizeHeight = mapView.height
            val latLngBounds = latLngBuilder.build()
            val track = CameraUpdateFactory.newLatLngBounds(latLngBounds, sizeWidth, sizeHeight, 150)
            *//*
            val track = CameraUpdateFactory.newLatLngBounds(latLngBuilder.build(), 150)
            try { //googleMap.moveCamera(track)
            googleMap.animateCamera(track)}
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
        }*/

        /*
        fun getTransportTypeName(id: String): Int{
            val nameRes = R.string::class.members.find( { it.name == "transport_type_$id" } )
            return (nameRes?.call() as Int?) ?: R.string.transport_type_unknown
        }
        */

        @DrawableRes
        fun getLanguageImage(code: String): Int {
            val imageRes = R.drawable::class.members.find( { it.name == "ic_language_$code" } )
            return (imageRes?.call() as Int?) ?: R.drawable.ic_language_unknown
        }

        fun getVehicleNameWithColor(context: Context, name: String, color: String): SpannableStringBuilder {
            val drawableCompat = getVehicleColorFormRes(context, color)
                .also { it.setBounds(4, 0, it.intrinsicWidth + 4, it.intrinsicHeight) }
            return SpannableStringBuilder("$name ").apply {
                setSpan(ImageSpan(drawableCompat, ImageSpan.ALIGN_BASELINE), length - 1, length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
        }

        fun getVehicleColorFormRes(context: Context, color: String): Drawable {
            val colorRes = R.color::class.members.find( { it.name == "color_vehicle_$color" } )
            val colorId = (colorRes?.call() as Int?) ?: R.color.color_vehicle_white
            
            return ContextCompat.getDrawable(context, R.drawable.ic_circle_car_color_indicator)!!
                .constantState!!
                .newDrawable()
                .mutate()
                .apply { setColorFilter(ContextCompat.getColor(context, colorId), PorterDuff.Mode.SRC_IN) }
        }

        fun initPhoneNumberUtil(context: Context) { phoneUtil = PhoneNumberUtil.createInstance(context) }

        fun getPhoneCodeByCountryIso(context: Context): Int {
            val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            val countryCode = telephonyManager.simCountryIso
            return phoneUtil.getCountryCodeForRegion(countryCode.toUpperCase())
        }
        
        fun formatPersons(context: Context, persons: Int) = context.getString(R.string.count_persons_and_baggage, persons)
        fun formatLuggage(context: Context, luggage: Int) = context.getString(R.string.count_persons_and_baggage, luggage)
        @Suppress("UNUSED_PARAMETER")
        fun formatPrice(context: Context, price: String)  = "($price)"

        fun convertDpToPixels(context: Context, dp: Float) =
            dp * context.resources.displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT

/*
        fun isConnectedToInternet(context: Context?): Boolean {
            try {
                if (context != null) {
                    val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                    val networkInfo = connectivityManager.activeNetworkInfo
                    return networkInfo != null && networkInfo.isConnected
                }
                return false
            } catch (e: Exception) {
                return false
            }
        }
        */
        fun isValidBitmap(bitmap: Bitmap) = bitmap.width <= MAX_BITMAP_SIZE && bitmap.height <= MAX_BITMAP_SIZE
    }
}

fun EditText.onTextChanged(cb: (String) -> Unit) {
    this.addTextChangedListener(object: TextWatcher {
        override fun afterTextChanged(s: Editable?) {}
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = cb(s.toString())
    })
}
