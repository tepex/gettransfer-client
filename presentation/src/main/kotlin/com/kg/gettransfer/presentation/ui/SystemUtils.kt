package com.kg.gettransfer.presentation.ui

import android.content.Context

import com.kg.gettransfer.R
import com.kg.gettransfer.domain.interactor.SessionInteractor

import java.text.SimpleDateFormat
import java.util.Date

import org.koin.core.KoinComponent
import org.koin.core.get

internal object SystemUtils : KoinComponent {

    private val sessionInteractor = get<SessionInteractor>()

    private const val MESSAGE_DATE_TIME_PATTERN = "MMM d, yyyy HH:mm"
    private const val DATE_TIME_PATTERN = "d MMMM yyyy, HH:mm"
    private const val DATE_TIME_NO_YEAR_SHORT_MONTH = "d MMM HH:mm"
    private const val DATE_PATTERN = "d MMM yyyy"
    private const val TIME_PATTERN = "HH:mm"
    private const val SLASH = "/"

    fun formatDistance(context: Context, distance: Int?, withDistanceText: Boolean): String {
        if (distance == null) return ""
        return if (withDistanceText) {
            context.getString(R.string.LNG_RIDE_DISTANCE).plus(": $distance ").plus(sessionInteractor.distanceUnit.name)
        } else {
            distance.toString().plus("${sessionInteractor.distanceUnit.name}")
        }
    }

    fun formatMessageDateTimePattern(date: Date): String = getFormattedDate(MESSAGE_DATE_TIME_PATTERN, date)
    fun formatDateTime(date: Date): String = getFormattedDate(DATE_TIME_PATTERN, date)
    fun formatDateTimeNoYearShortMonth(date: Date): String = getFormattedDate(DATE_TIME_NO_YEAR_SHORT_MONTH, date)
    fun formatDate(date: Date): String = getFormattedDate(DATE_PATTERN, date)
    fun formatTime(date: Date): String = getFormattedDate(TIME_PATTERN, date)

    private fun getFormattedDate(pattern: String, date: Date) =
        SimpleDateFormat(pattern, sessionInteractor.locale).format(date)

    fun getUrlWithLocale(url: String) = url.plus(SLASH).plus(sessionInteractor.locale.language)
}
