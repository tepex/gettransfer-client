package com.kg.gettransfer.data.mapper

import com.kg.gettransfer.data.model.TransferNewEntity
import com.kg.gettransfer.data.model.TripEntity

import com.kg.gettransfer.domain.model.TransferNew

import org.koin.standalone.get

/**
 * Map a [TransferNewEntity] to and from a [TransferNew] instance when data is moving between this later and the Domain layer.
 */
open class TransferNewMapper: Mapper<TransferNewEntity, TransferNew> {
    private val cityPointMapper = get<CityPointMapper>()
    private val tripMapper      = get<TripMapper>()
    private val moneyMapper     = get<MoneyMapper>()
    private val userMapper      = get<UserMapper>()
    private val destMapper      = get<DestMapper>()

    /**
     * Map a [TransferNewEntity] instance to a [TransferNew] instance.
     */
    override fun fromEntity(type: TransferNewEntity): TransferNew { throw UnsupportedOperationException() }

    /**
     * Map a [TransferNew] instance to a [TransferNewEntity] instance.
     */
    override fun toEntity(type: TransferNew) =
        TransferNewEntity(cityPointMapper.toEntity(type.from),
                          destMapper.toEntity(type.dest),
                          tripMapper.toEntity(type.tripTo),
                          type.tripReturn?.let { tripMapper.toEntity(it) },
                          type.transportTypeIds,
                          type.pax,
                          type.childSeats,
                          type.passengerOfferedPrice,
                          type.user.profile.fullName,
                          type.comment,
                          userMapper.toEntity(type.user),
                          type.promoCode)
}
