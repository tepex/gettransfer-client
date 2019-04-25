package com.kg.gettransfer.data.ds

import com.kg.gettransfer.data.RouteRemote
import com.kg.gettransfer.data.RouteDataStore
import com.kg.gettransfer.data.model.RouteInfoEntity
import org.koin.standalone.get

import org.koin.standalone.inject
import java.lang.UnsupportedOperationException
import java.text.DateFormat
import java.util.Date

/**
 * Implementation of the [RouteDataStore] interface to provide a means of communicating with the remote data source
 */
open class RouteDataStoreRemote: RouteDataStore {
    private val remote: RouteRemote by inject()
    private val dateFormatTZ = get<ThreadLocal<DateFormat>>("iso_date_TZ")
    
    override suspend fun getRouteInfo(from: String, to: String, withPrices: Boolean?, returnWay: Boolean?, currency: String?, dateTime: Date?) =
        remote.getRouteInfo(arrayOf(from, to), withPrices!!, returnWay!!, currency!!, dateTime?.let { dateFormatTZ.get().format(it) })

    override suspend fun getRouteInfo(from: String, hourlyDuration: Int, currency: String, dateTime: Date?) =
            remote.getRouteInfo(arrayOf(from), hourlyDuration, currency, dateTime?.let { dateFormatTZ.get().format(it) })

    override suspend fun setRouteInfo(from: String, to: String, routeInfo: RouteInfoEntity) {
        throw UnsupportedOperationException()
    }
}
