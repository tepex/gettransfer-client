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
    private val systemInteractor = get<SystemInteractor>()
    private val transportTypeMapper = get<TransportTypeMapper>()

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

    fun getTransferNew(from: CityPoint,
                       dest: Dest<CityPoint, Int>,
                       tripTo: Trip,
                       tripReturn: Trip?,
                       transportTypes: List<String>,
                       pax: Int,
                       childSeats: Int?,
                       passengerOfferedPrice: Double?,
                       comment: String?,
                       user: User,
                       promoCode: String = "",
                       paypalOnly: Boolean) = TransferNew(from,
                                                          dest,
                                                          tripTo,
                                                          tripReturn,
                                                          transportTypes,
                                                          pax,
                                                          childSeats,
                                                          passengerOfferedPrice?.let { it.times(100).toInt() },
                                                          comment,
                                                          user,
                                                          promoCode,
                                                          paypalOnly)



}
