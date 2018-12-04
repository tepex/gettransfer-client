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

    fun formatDistance(context: Context, _distance: Int?, withDistanceText: Boolean): String {
        if (_distance == null) return ""
        val du = systemInteractor.distanceUnit
        val distance = if(du == DistanceUnit.mi) DistanceUnit.km2Mi(_distance) else _distance
        return if (withDistanceText) context.getString(R.string.LNG_RIDE_DISTANCE).plus(": $distance ").plus(du.name)
        else distance.toString().plus(du.name)
    }

    fun formatDateTime(date: Date) = SimpleDateFormat(DATE_TIME_PATTERN, systemInteractor.locale).format(date)
}
