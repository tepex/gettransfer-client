package com.kg.gettransfer.data.mapper

import com.kg.gettransfer.data.model.TripEntity

import com.kg.gettransfer.domain.model.Trip

import java.text.SimpleDateFormat
import java.util.Locale

/**
 * Map a [TripEntity] to and from a [Trip] instance when data is moving between this later and the Domain layer.
 */
open class TripMapper(): Mapper<TripEntity, Trip> {
    private val SERVER_DATE_FORMAT by lazy { SimpleDateFormat("yyyy/MM/dd", Locale.US) }
    private val SERVER_TIME_FORMAT by lazy { SimpleDateFormat("HH:mm", Locale.US) }
    
    /**
     * Map a [TripEntity] instance to a [Trip] instance.
     */
    override fun fromEntity(type: TripEntity): Trip { throw UnsupportedOperationException() }
    
    /**
     * Map a [Trip] instance to a [TripEntity] instance.
     */
    override fun toEntity(type: Trip) = TripEntity(SERVER_DATE_FORMAT.format(type.dateTime),
                                                   SERVER_TIME_FORMAT.format(type.dateTime),
                                                   type.flightNumber)
}
