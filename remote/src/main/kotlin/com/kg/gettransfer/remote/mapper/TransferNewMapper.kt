package com.kg.gettransfer.remote.mapper

import com.kg.gettransfer.data.model.TransferNewEntity
import com.kg.gettransfer.data.model.TripEntity

import com.kg.gettransfer.remote.model.TransferNewModel
import com.kg.gettransfer.remote.model.TripModel

/**
 * Map a [TransferNewEntity] to and from a [TransferNewModel] instance when data is moving between this later and the Data layer.
 */
open class TransferNewMapper(private val cityPointMapper: CityPointMapper,
                             private val tripMapper: TripMapper,
                             private val moneyMapper: MoneyMapper,
                             private val userMapper: UserMapper): EntityMapper<TransferNewModel, TransferNewEntity> {
    /**
     * Map a [TransferNewModel] instance to a [TransferNewEntity] instance.
     */
    override fun fromRemote(type: TransferNewModel): TransferNewEntity { throw UnsupportedOperationException() }
    
    /**
     * Map a [TransferNewEntity] instance to a [TransferNewModel] instance.
     */
    override fun toRemote(type: TransferNewEntity) =
        TransferNewModel(cityPointMapper.toRemote(type.from),
                         cityPointMapper.toRemote(type.to),
                         tripMapper.toRemote(type.tripTo),
                         type.tripReturn?.let { tripMapper.toRemote(it) },
                         type.transportTypeIds,
                         type.pax,
                         type.childSeats,
                         type.passengerOfferedPrice?.let { "%.2f".format(it.toFloat() / 100) },
                         type.nameSign,
                         type.comment,
                         userMapper.toRemote(type.user),
                         type.promoCode)
}
