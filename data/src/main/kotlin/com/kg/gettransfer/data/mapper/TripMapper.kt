package com.kg.gettransfer.data.mapper

import com.kg.gettransfer.data.model.TripEntity

import com.kg.gettransfer.domain.model.Trip

import java.text.SimpleDateFormat
import java.util.Locale

/**
 * Map a [TripEntity] to and from a [Trip] instance when data is moving between this later and the Domain layer.
 */
open class TripMapper(): Mapper<TripEntity, Trip> {
    /**
     * Map a [TripEntity] instance to a [Trip] instance.
     */
    override fun fromEntity(type: TripEntity): Trip { throw UnsupportedOperationException() }
    
    /**
     * Map a [Trip] instance to a [TripEntity] instance.
     */
    override fun toEntity(type: Trip) = TripEntity(SimpleDateFormat("yyyy/MM/dd", Locale.US).format(type.dateTime),
                                                   SimpleDateFormat("HH:mm", Locale.US).format(type.dateTime),
                                                   type.flightNumber)
}
