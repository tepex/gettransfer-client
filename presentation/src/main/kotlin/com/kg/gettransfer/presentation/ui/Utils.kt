package com.kg.gettransfer.presentation.ui

import android.app.Activity

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.net.Uri

import android.os.Build

import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.fragment.app.FragmentActivity

import androidx.core.content.ContextCompat

import android.telephony.TelephonyManager

import android.text.Html
import android.text.Editable
import android.text.Spanned
import android.text.TextWatcher

import android.util.DisplayMetrics
import android.util.Patterns

import android.view.View
import android.view.ViewGroup

import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog

import com.bumptech.glide.Glide
import com.bumptech.glide.load.Transformation
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.FitCenter
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions

import com.google.android.gms.maps.CameraUpdate
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.PolylineOptions

import com.google.maps.android.PolyUtil

import com.kg.gettransfer.R

import com.kg.gettransfer.presentation.mapper.PointMapper

import com.kg.gettransfer.presentation.model.LocaleModel
import com.kg.gettransfer.presentation.model.PolylineModel
import com.kg.gettransfer.presentation.model.RouteModel

import io.michaelrocks.libphonenumber.android.PhoneNumberUtil

import java.text.SimpleDateFormat

import java.util.Date
import java.util.Locale

import org.koin.core.inject
import org.koin.core.KoinComponent

import timber.log.Timber

object Utils : KoinComponent {
    //private val PHONE_PATTERN = Pattern.compile("^\\+\\d{11,13}$")
    private val EMAIL_PATTERN = Patterns.EMAIL_ADDRESS

    @JvmField val DATE_PATTERN = "dd MMM yyyy"
    @JvmField val DATE_WITHOUT_YEAR_PATTERN = "dd MMM"
    @JvmField val TIME_PATTERN = "HH:mm"
    const val MAX_BITMAP_SIZE = 4096

    internal val phoneUtil: PhoneNumberUtil by inject()

    private val pointMapper: PointMapper by inject()

    fun getAlertDialogBuilder(context: Context): AlertDialog.Builder {
        return AlertDialog.Builder(context)
    }

    fun showError(context: Context, finish: Boolean, message: String, onClose: (() -> Unit)? = null) {
        getAlertDialogBuilder(context).apply {
            setTitle(R.string.LNG_ERROR)
            setMessage(message)
            setPositiveButton(R.string.LNG_OK) { dialog, _ ->
                dialog.dismiss()
                if (finish) (context as Activity).finish()
                onClose?.invoke()
            }
            setIcon(android.R.drawable.ic_dialog_alert)
            show()
        }
    }

    fun showLoginDialog(context: Context, message: String, title: String) {
        getAlertDialogBuilder(context)
                .apply {
                    setTitle(title)
                    setMessage(message)
                    setPositiveButton(R.string.LNG_OK) { dialog,_ -> dialog.dismiss() }
                    show()
                }
    }

    fun showAlertCancelRequest(context: Context, listener: (Boolean) -> Unit) {
        getAlertDialogBuilder(context).apply {
            setTitle(R.string.LNG_CANCEL_CONFIRM)
            setPositiveButton(R.string.LNG_YES) { _, _ -> listener(true) }
            setNegativeButton(R.string.LNG_NO)  { _, _ -> listener(false) }
            show()
        }
    }

    fun showAlertSetNewPassword(context: Context, listener: (Boolean) -> Unit) {
        getAlertDialogBuilder(context).apply {
            setTitle(R.string.LNG_AUTHORIZED)
            setMessage(R.string.LNG_PASSWORD_CREATE_HINT)
            setPositiveButton(R.string.LNG_MENU_TITLE_SETTINGS) { _, _ -> listener(true) }
            setNegativeButton(R.string.LNG_REVIEW_REQUEST_LATER)  { _, _ -> listener(false) }
            setOnCancelListener { listener(false) }
            show()
        }
    }

    fun showScreenRedirectingAlert(context: Context, title: String, navigate: () -> Unit) {
        getAlertDialogBuilder(context).apply {
            setTitle(title)
            setPositiveButton(R.string.LNG_OK) { dialog, _ ->
                dialog.dismiss()
                navigate()
            }
            show()
        }
    }

    fun setLocalesDialogListener(
        context: Context,
        view: View,
        items: List<CharSequence>,
        listener: (Int) -> Unit
    ) { setModelsDialogListener(context, view, R.string.LNG_LANGUAGE, items, listener) }

    fun setFirstDayOfWeekDialogListener(
            context: Context,
            view: View,
            items: List<CharSequence>,
            listener: (Int) -> Unit
    ) { setModelsDialogListener(context, view, R.string.LNG_WEEK_FIRST_DAY, items, listener) }

    fun setOfferFilterDialogListener(
            context: Context,
            view: View,
            items: List<CharSequence>,
            listener: (Int) -> Unit
    ) { setModelsDialogListener(context, view, R.string.LNG_FILTER, items, listener) }

    fun setCalendarModesDialogListener(
            context: Context,
            view: View,
            items: List<CharSequence>,
            @StringRes titleId: Int,
            listener: (Int) -> Unit
    ) { setModelsDialogListener(context, view, titleId, items, listener) }

    fun setEndpointsDialogListener(
        context: Context,
        view: View,
        items: List<CharSequence>,
        listener: (Int) -> Unit
    ) { setModelsDialogListener(context, view, R.string.endpoint, items, listener) }

    fun setSelectOperationListener(
        context: Context,
        view: View,
        items: List<CharSequence>,
        @StringRes titleId: Int,
        listener: (Int) -> Unit
    ) { setModelsDialogListener(context, view, titleId, items, listener) }

    fun setModelsDialogListener(
        context: Context,
        view: View,
        @StringRes titleId: Int,
        items: List<CharSequence>,
        listener: (Int) -> Unit
    ) {

        view.setOnClickListener {
            getAlertDialogBuilder(context).apply {
                setTitle(titleId)
                setItems(items.toTypedArray()) { _, which -> listener(which) }
                setNegativeButton(R.string.LNG_CANCEL, null)
                show()
            }
        }
    }

    fun showBackGroundPermissionDialog(context: Context, clickResult: (result: Boolean) -> Unit) {
        getAlertDialogBuilder(context).apply {
            setMessage(R.string.LNG_SEND_COORDINATES_IN_BACKGROUND_MESSAGE)
            setTitle(R.string.LNG_SEND_COORDINATES_IN_BACKGROUND)
            setNegativeButton(R.string.LNG_NO) { _, _ -> clickResult(false) }
            setPositiveButton(R.string.LNG_YES) { _, _ -> clickResult(true) }
            show()
        }
    }

    /**
     * Go to google play without result
     */
    fun goToGooglePlay(context: FragmentActivity, packageName: String) {
        val marketLink = Uri.parse(context.getString(R.string.market_link) + packageName)
        val siteMarketLink = Uri.parse(context.getString(R.string.market_site_link) + packageName)
        try {
            context.startActivity(createGooglePlayIntent(marketLink))
        } catch (anfe: ActivityNotFoundException) {
            context.startActivity(createGooglePlayIntent(siteMarketLink))
        }
    }

    /**
     * Go to google play with return result
     */
    fun goToGooglePlay(context: FragmentActivity, packageName: String, requestCode: Int) {
        val marketLink = Uri.parse(context.getString(R.string.market_link) + packageName)
        val siteMarketLink = Uri.parse(context.getString(R.string.market_site_link) + packageName)
        try {
            context.startActivityForResult(createGooglePlayIntent(marketLink), requestCode)
        } catch (anfe: ActivityNotFoundException) {
            context.startActivityForResult(createGooglePlayIntent(siteMarketLink), requestCode)
        }
    }

    private fun createGooglePlayIntent(uri: Uri) = Intent(Intent.ACTION_VIEW).apply {
        data = uri
        addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
    }

    fun checkEmail(email: String?) = EMAIL_PATTERN.matcher(email ?: "").matches()
        //fun checkPhone(phone: String?) = PHONE_PATTERN.matcher(phone?.trim() ?: "").matches()

    fun checkPhone(phone: String?): Boolean {
        try {
            /*return if(!PHONE_PATTERN.matcher(phone.trim()).matches()) false
            else phoneUtil.isValidNumber(phoneUtil.parse(phone, null))*/
            return phoneUtil.isValidNumber(phoneUtil.parse(phone, Locale.getDefault().country))
        } catch (e: Exception) {
            Timber.w(e, "phone parse error: $phone")
            return false
        }
    }

    fun convertToInternationalPhone(phone: String): String {
        val phoneNumber = phoneUtil.parse(phone, Locale.getDefault().country)
        val internationalPhone = phoneUtil.format(phoneNumber, PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL).replace(Regex("\\D"), "")
        return "+".plus(internationalPhone)
    }

    fun getPolyline(routeModel: RouteModel): PolylineModel {
        val mPoints = mutableListOf<LatLng>()
        var line: PolylineOptions? = null
        val latLngBuilder = LatLngBounds.Builder()
        val track: CameraUpdate?

        if (routeModel.polyLines != null && routeModel.polyLines.isNotEmpty()) {
            for (item in routeModel.polyLines) mPoints.addAll(PolyUtil.decode(item))

            // Для построения упрощённого маршрута (меньше точек)
            //val mPoints = PolyUtil.decode(routeInfo.overviewPolyline)

            line = PolylineOptions()

            for (i in mPoints.indices) {
                line.add(mPoints.get(i))
                latLngBuilder.include(mPoints.get(i))
            }
        } else {
            mPoints.add(pointMapper.toLatLng(routeModel.fromPoint))
            mPoints.add(pointMapper.toLatLng(routeModel.toPoint))

            for (i in mPoints.indices) latLngBuilder.include(mPoints.get(i))
        }

        Timber.d("latLngBuilder: $latLngBuilder")

        val build = latLngBuilder.build()
        val northeast = build.northeast
        val southwest = build.southwest
        val isVerticalRoute = northeast.latitude - southwest.latitude >= northeast.longitude - southwest.longitude

        track = try { CameraUpdateFactory.newLatLngBounds(build, 150) }
        catch (e: Exception) {
            Timber.w(e, "Create order error: $latLngBuilder")
            null
        }
        return PolylineModel(mPoints.firstOrNull(), mPoints.getOrNull(mPoints.size - 1), line, track, isVerticalRoute)
    }

    fun getCameraUpdate(list: List<LatLng>): CameraUpdate  =
        LatLngBounds.Builder()
                .also { b -> list.forEach { b.include(it) } }
                .build()
                .let { CameraUpdateFactory.newLatLngBounds(it, 150) }

    fun getCameraUpdateForPin(point: LatLng) = CameraUpdateFactory.newLatLngZoom(point, BaseGoogleMapActivity.MAP_MIN_ZOOM)



    fun getDateTimeTransferDetails(locale: Locale, dateToLocal: Date, withYear: Boolean): Pair<String, String> {
        val dateString = if (withYear) SimpleDateFormat(DATE_PATTERN, locale).format(dateToLocal)
                         else SimpleDateFormat(DATE_WITHOUT_YEAR_PATTERN, locale).format(dateToLocal)
        val timeString = SimpleDateFormat(TIME_PATTERN, locale).format(dateToLocal)
        return Pair(dateString, timeString)
    }

    fun convertDuration(min: Int): Triple<Int, Int, Int> {
        val hours = min / 60
        return Triple(hours / 24, hours % 24, min % 60)
    }

    fun convertHoursToMinutes(hours: Int) = hours * 60

    fun formatDuration(context: Context, duration: Int): String {
        val days = duration / 24
        return if (days > 0) "$days ".plus(context.getString(R.string.LNG_DAYS))
        else "$duration ".plus(context.getString(R.string.LNG_HOURS))
    }

    fun durationToString(context: Context, duration: Triple<Int, Int, Int>) = buildString {
        val (days: Int, hours: Int, minutes: Int) = duration
        context.getString(R.string.LNG_DATE_IN_HOURS)
        if (days != 0) {
            append(" $days")
            append(context.getString(R.string.LNG_D))
        }
        append(" ${hours % 24}")
        append(context.getString(R.string.LNG_H))
        append(" ${minutes % 60}")
        append(context.getString(R.string.LNG_M))
    }

    fun getSpannedStringFromHtmlString(htmlString: String): Spanned {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) Html.fromHtml(htmlString, Html.FROM_HTML_MODE_LEGACY)
        else Html.fromHtml(htmlString)
    }

    @DrawableRes
    fun getLanguageImage(code: String): Int {
        val imageRes = R.drawable::class.members.find( { it.name == "ic_language_$code" } )
        return (imageRes?.call() as Int?) ?: R.drawable.ic_language_unknown
    }

    @StringRes
    fun getCarColorTextRes(color: String): Int {
        val colorRes = R.string::class.members.find({ it.name == "LNG_COLOR_${color.toUpperCase()}" })
        return (colorRes?.call() as Int?) ?: R.string.LNG_COLOR_WHITE
    }

    @ColorRes
    private fun getCarColorResId(color: String): Int {
        val colorRes = R.color::class.members.find({ it.name == "color_car_$color" })
        return (colorRes?.call() as Int?) ?: R.color.color_car_white
    }

    fun getCarColorTextBackFormRes(context: Context, color: String) =
        getCarColorTextBackDrawable(context, getCarColorResId(color))

    private fun getCarColorTextBackDrawable(context: Context, colorId: Int) = GradientDrawable().apply {
        setColor(ContextCompat.getColor(context, colorId))
        if (colorId == R.color.color_car_white) {
            setStroke(
                dpToPxInt(context, 1f),
                ContextCompat.getColor(context, R.color.color_gtr_light_grey)
            )
        }
        shape = GradientDrawable.RECTANGLE
        cornerRadius = convertDpToPixels(context, 24.0f)
    }

    fun getCarColorFormRes(context: Context, color: String) = getCarColorDrawable(context, getCarColorResId(color))

    private fun getCarColorDrawable(context: Context, colorId: Int) = GradientDrawable().apply {
        setColor(ContextCompat.getColor(context, colorId))
        if (colorId == R.color.color_car_white) {
            setStroke(
                dpToPxInt(context, 1f),
                ContextCompat.getColor(context, R.color.color_gtr_light_grey)
            )
        }
        shape = GradientDrawable.OVAL
        cornerRadius = 5.0f
    }

    fun getPhoneCodeByCountryIso(context: Context): Int {
        val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        return phoneUtil.getCountryCodeForRegion(telephonyManager.simCountryIso.toUpperCase())
    }

    fun formatPersons(context: Context, persons: Int) = context.getString(R.string.count_persons_and_baggage, persons)
    fun formatLuggage(context: Context, luggage: Int) = context.getString(R.string.count_persons_and_baggage, luggage)

    @Suppress("UNUSED_PARAMETER")
    fun formatPrice(context: Context, price: String) = "($price)"

    private fun displayMetrics(context: Context) = context.resources.displayMetrics

    fun convertDpToPixels(context: Context, dp: Float) =
        dp * displayMetrics(context).densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT

    fun dpToPxInt(context: Context, dp: Float) = convertDpToPixels(context, dp).toInt()

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

    fun setDrawables(textView: TextView,
                     @DrawableRes start: Int,
                     @DrawableRes top: Int,
                     @DrawableRes end: Int,
                     @DrawableRes bottom: Int) =
            textView.setCompoundDrawablesRelativeWithIntrinsicBounds(start, top, end, bottom)

    fun setDrawables(textView: TextView, start: Drawable?,
                     top: Drawable?, end: Drawable?,
                     bottom: Drawable?) =
            textView.setCompoundDrawablesRelativeWithIntrinsicBounds(start, top, end, bottom)

    fun bindMainOfferPhoto(
        view: ImageView,
        parent: View,
        path: String? = null,
        @DrawableRes resource: Int = 0
    ) = Glide
        .with(parent).let { if (path != null) it.load(path) else it.load(resource) }
        .apply(
            RequestOptions()
                .error(resource)
                .placeholder(resource)
                .transform(
                    *arrayOf<Transformation<Bitmap>>(
                        path?.let { CenterCrop() } ?: FitCenter(),
                        RoundedCorners(parent.context.resources.getDimensionPixelSize(R.dimen.view_offer_photo_corner))
                    )
                )
        )
        .into(view)
}

fun EditText.onTextChanged(cb: (String) -> Unit) {
    this.addTextChangedListener(object: TextWatcher {
        override fun afterTextChanged(s: Editable?) {}
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = cb(s.toString())
    })
}

fun EditText.afterTextChanged(cb: (String) -> Unit) {
    this.addTextChangedListener(object: TextWatcher {
        override fun afterTextChanged(s: Editable?) = cb(s.toString())
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
    })
}
