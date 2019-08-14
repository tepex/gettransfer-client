package com.kg.gettransfer.presentation.ui

import android.content.Context

import com.kg.gettransfer.R
import com.kg.gettransfer.domain.interactor.SessionInteractor
import com.kg.gettransfer.domain.model.DistanceUnit

import java.text.SimpleDateFormat
import java.util.Date

import org.koin.core.KoinComponent
import org.koin.core.get
import org.koin.core.inject

internal object SystemUtils : KoinComponent {

    private val sessionInteractor = get<SessionInteractor>()

    private const val MESSAGE_DATE_TIME_PATTERN = "MMM dd, yyyy HH:mm"
    private const val DATE_TIME_PATTERN = "dd MMMM yyyy, HH:mm"
    private const val DATE_PATTERN = "dd MMMM yyyy"
    private const val DATE_TIME_SHORT_MONTH_PATTERN = "dd MMM yyyy 'at' HH:mm"
    private const val DATE_TIME_NO_YEAR_SHORT_MONTH = "dd MMM HH:mm"
    private const val MONTH_PATTERN = "LLLL"
    private const val DAY_MONTH_PATTERN = "dd MMMM, EEEE"
    private const val DATE_WITHOUT_TIME_PATTERN = "dd/MM/yyyy"
    private const val DAY_OF_WEEK_PATTERN = "EEE"
    private const val TIME_PATTERN = "HH:mm"
    private const val MONTH_YEAR_PATTERN = "LLLL yyyy"
    private const val FULL_DATE_SECONDS = "yyyy-MM-dd HH:mm:ss"
    private const val SLASH = "/"

    fun formatDistance(context: Context, distance: Int?, withDistanceText: Boolean): String {
        if (distance == null) return ""
        return if (withDistanceText) {
            context.getString(R.string.LNG_RIDE_DISTANCE).plus(": $distance ").plus(sessionInteractor.distanceUnit.name)
        } else {
            distance.toString().plus(" ${sessionInteractor.distanceUnit.name}")
        }
    }

    fun formatMessageDateTimePattern(date: Date) = getFormattedDate(MESSAGE_DATE_TIME_PATTERN, date)
    fun formatDateTime(date: Date) = getFormattedDate(DATE_TIME_PATTERN, date)
    fun formatDate(date: Date) = getFormattedDate(DATE_PATTERN, date)
    fun formatDateTimeWithShortMonth(date: Date) = getFormattedDate(DATE_TIME_SHORT_MONTH_PATTERN, date)
    fun formatDateTimeNoYearShortMonth(date: Date) = getFormattedDate(DATE_TIME_NO_YEAR_SHORT_MONTH, date)
    fun formatMonth(date: Date) = getFormattedDate(MONTH_PATTERN, date)
    fun formatDayMonth(date: Date) = getFormattedDate(DAY_MONTH_PATTERN, date)
    fun formatDateWithoutTime(date: Date) = getFormattedDate(DATE_WITHOUT_TIME_PATTERN, date)
    fun formatShortNameDayOfWeek(date: Date) = getFormattedDate(DAY_OF_WEEK_PATTERN, date)
    fun formatTime(date: Date) = getFormattedDate(TIME_PATTERN, date)
    fun formatMonthYear(date: Date) = getFormattedDate(MONTH_YEAR_PATTERN, date)
    fun formatSeconds(date: Date) = getFormattedDate(FULL_DATE_SECONDS, date)

    private fun getFormattedDate(pattern: String, date: Date) =
        SimpleDateFormat(pattern, sessionInteractor.locale).format(date)

    fun getUrlWithLocale(url: String) = url.plus(SLASH).plus(sessionInteractor.locale.language)
}
