package com.kg.gettransfer.data.mapper

import com.kg.gettransfer.data.model.TripEntity

import com.kg.gettransfer.domain.model.Trip

import java.text.DateFormat

import org.koin.standalone.get

/**
 * Map a [TripEntity] to and from a [Trip] instance when data is moving between this later and the Domain layer.
 */
open class TripMapper(): Mapper<TripEntity, Trip> {
    private val serverDateFormat = get<ThreadLocal<DateFormat>>("server_date")
    private val serverTimeFormat = get<ThreadLocal<DateFormat>>("server_time")
    /**
     * Map a [TripEntity] instance to a [Trip] instance.
     */
    override fun fromEntity(type: TripEntity): Trip { throw UnsupportedOperationException() }
    
    /**
     * Map a [Trip] instance to a [TripEntity] instance.
     */
    override fun toEntity(type: Trip) = TripEntity(serverDateFormat.get().format(type.dateTime),
                                                   serverTimeFormat.get().format(type.dateTime),
                                                   type.flightNumber)
}
