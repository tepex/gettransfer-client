package com.kg.gettransfer.data.mapper

import com.kg.gettransfer.data.model.TransferNewEntity
import com.kg.gettransfer.data.model.TripEntity
import com.kg.gettransfer.data.model.map

import com.kg.gettransfer.domain.model.TransferNew
import com.kg.gettransfer.domain.model.TransportType

import java.text.DateFormat

import org.koin.standalone.get

/**
 * Map a [TransferNewEntity] to and from a [TransferNew] instance when data is moving between this later and the Domain layer.
 */
open class TransferNewMapper : Mapper<TransferNewEntity, TransferNew> {
    private val serverDateFormat = get<ThreadLocal<DateFormat>>("server_date")
    private val serverTimeFormat = get<ThreadLocal<DateFormat>>("server_time")

    /**
     * Map a [TransferNewEntity] instance to a [TransferNew] instance.
     */
    override fun fromEntity(type: TransferNewEntity): TransferNew { throw UnsupportedOperationException() }

    /**
     * Map a [TransferNew] instance to a [TransferNewEntity] instance.
     */
    override fun toEntity(type: TransferNew) =
        TransferNewEntity(
            from                  = type.from.map(),
            dest                  = type.dest.map(),
            tripTo                = type.tripTo.map(serverDateFormat, serverTimeFormat),
            tripReturn            = type.tripReturn?.let { it.map(serverDateFormat, serverTimeFormat) },
            transportTypeIds      = type.transportTypeIds.map { it.toString() },
            pax                   = type.pax,
            childSeatsInfant      = type.childSeatsInfant,
            childSeatsConvertible = type.childSeatsConvertible,
            childSeatsBooster     = type.childSeatsBooster,
            passengerOfferedPrice = type.passengerOfferedPrice,
            nameSign              = type.nameSign,
            comment               = type.comment,
            user                  = type.user.map(),
            promoCode             = type.promoCode
        )
}
