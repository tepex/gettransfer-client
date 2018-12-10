package com.kg.gettransfer.presentation.model

import android.location.Location

import android.support.annotation.StringRes

import com.google.android.gms.maps.model.LatLng

import com.kg.gettransfer.R
import com.kg.gettransfer.domain.interactor.SystemInteractor
import com.kg.gettransfer.domain.model.*

import com.kg.gettransfer.presentation.mapper.Mapper
import com.kg.gettransfer.presentation.mapper.TransportTypeMapper

import com.kg.gettransfer.presentation.presenter.CreateOrderPresenter

import com.kg.gettransfer.presentation.ui.SystemUtils
import com.kg.gettransfer.presentation.ui.Utils

import com.kg.gettransfer.utilities.Analytics

import java.util.Currency
import java.util.Locale
import java.util.Calendar


import org.koin.standalone.get
import org.koin.standalone.KoinComponent

object Mappers : KoinComponent {
    fun getRouteModel(distance: Int?,
                      polyLines: List<String>?,
                      from: String,
                      to: String,
                      fromPoint: Point,
                      toPoint: Point,
                      dateTime: String) = RouteModel(distance ?: Mapper.checkDistance(fromPoint, toPoint),
                                                     polyLines,
                                                     from,
                                                     to,
                                                     fromPoint,
                                                     toPoint,
                                                     dateTime)

}
