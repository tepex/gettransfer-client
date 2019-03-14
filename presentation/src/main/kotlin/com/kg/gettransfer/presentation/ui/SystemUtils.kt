package com.kg.gettransfer.presentation.ui

import android.content.Context

import com.kg.gettransfer.R
import com.kg.gettransfer.domain.interactor.SystemInteractor
import com.kg.gettransfer.domain.model.DistanceUnit

import java.text.SimpleDateFormat

import java.util.Date

import org.koin.standalone.get
import org.koin.standalone.KoinComponent

internal object SystemUtils : KoinComponent {
    private val systemInteractor = get<SystemInteractor>()
    private const val DATE_TIME_PATTERN = "dd MMMM yyyy, HH:mm"
    private const val DATE_IIME_SHORT_MONTH_PATTERN = "dd MMM yyyy, HH:mm"
    private const val MONTH_PATTERN = "LLLL"
    private const val DAY_MONTH_PATTERN = "dd MMMM, EEEE"
    private const val SLASH = "/"

    fun formatDistance(context: Context, _distance: Int?, withDistanceText: Boolean): String {
        if (_distance == null) return ""
        val du = systemInteractor.distanceUnit
        val distance = if(du == DistanceUnit.mi) DistanceUnit.km2Mi(_distance) else _distance
        return if (withDistanceText) context.getString(R.string.LNG_RIDE_DISTANCE).plus(": $distance ").plus(du.name)
        else distance.toString().plus(" ${du.name}")
    }

    fun formatDateTime(date: Date) = SimpleDateFormat(DATE_TIME_PATTERN, systemInteractor.locale).format(date)

    fun formatDateTimeWithShortMonth(date: Date) = SimpleDateFormat(DATE_IIME_SHORT_MONTH_PATTERN, systemInteractor.locale).format(date)

    fun formatMonth(date: Date) = SimpleDateFormat(MONTH_PATTERN, systemInteractor.locale).format(date)

    fun formatDayMonth(date: Date) = SimpleDateFormat(DAY_MONTH_PATTERN, systemInteractor.locale).format(date)

    fun gtUrlWithLocale(context: Context) =
            context.getString(R.string.api_url_prod)
            .plus(SLASH)
            .plus(systemInteractor.locale.language)
}
