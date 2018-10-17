package com.kg.gettransfer.data.mapper

import com.kg.gettransfer.data.model.TransferNewEntity
import com.kg.gettransfer.data.model.TripEntity

import com.kg.gettransfer.domain.model.TransferNew

/**
 * Map a [TransferNewEntity] to and from a [TransferNew] instance when data is moving between this later and the Domain layer.
 */
open class TransferNewMapper(private val cityPointMapper: CityPointMapper,
                             private val tripMapper: TripMapper,
                             private val moneyMapper: MoneyMapper,
                             private val userMapper: UserMapper): Mapper<TransferNewEntity, TransferNew> {
    /**
     * Map a [TransferNewEntity] instance to a [TransferNew] instance.
     */
    override fun fromEntity(type: TransferNewEntity): TransferNew { throw UnsupportedOperationException() }
    
    /**
     * Map a [TransferNew] instance to a [TransferNewEntity] instance.
     */
    override fun toEntity(type: TransferNew): TransferNewEntity {
        var tripReturn: TripEntity? = null
        if(type.tripReturn != null) tripReturn = tripMapper.toEntity(type.tripReturn!!)
        return TransferNewEntity(cityPointMapper.toEntity(type.from),
                                 cityPointMapper.toEntity(type.to),
                                 tripMapper.toEntity(type.tripTo),
                                 tripReturn,
                                 type.transportTypeIds,
                                 type.pax,
                                 type.childSeats,
                                 type.passengerOfferedPrice,
                                 type.user.profile.name,
                                 type.comment,
                                 userMapper.toEntity(type.user),
                                 type.promoCode)
    }
}
