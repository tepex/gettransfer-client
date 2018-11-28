package com.kg.gettransfer.remote.mapper

import com.kg.gettransfer.data.model.TransferNewEntity
import com.kg.gettransfer.remote.model.*

import org.koin.standalone.get

/**
 * Map a [TransferNewEntity] to and from a [TransferNewModel] instance when data is moving between this later and the Data layer.
 */
open class TransferNewMapper: EntityMapper<TransferNewBase, TransferNewEntity> {
    private val cityPointMapper = get<CityPointMapper>()
    private val tripMapper      = get<TripMapper>()
    private val moneyMapper     = get<MoneyMapper>()
    private val userMapper      = get<UserMapper>()

    /**
     * Map a [TransferNewModel] instance to a [TransferNewEntity] instance.
     */
    override fun fromRemote(type: TransferNewBase): TransferNewEntity { throw UnsupportedOperationException() }

    /**
     * Map a [TransferNewEntity] instance to a [TransferNewModel] instance.
     */
    override fun toRemote(type: TransferNewEntity) = getCurrentType(type)
//        TransferNewModel(cityPointMapper.toRemote(type.from),
//                         cityPointMapper.toRemote(type.to),
//                         tripMapper.toRemote(type.tripTo),
//                         type.tripReturn?.let { tripMapper.toRemote(it) },
//                         type.transportTypeIds,
//                         type.pax,
//                         type.childSeats,
//                         //type.passengerOfferedPrice?.let { "%.2f".format(it.toFloat() / 100) },
//                         type.passengerOfferedPrice?.let { it.toDouble() / 100 },
//                         type.nameSign,
//                         type.comment,
//                         userMapper.toRemote(type.user),
//                         type.promoCode)

    private fun getCurrentType(type: TransferNewEntity): TransferNewBase {
        return if (type.duration != null) getHourly(type) else getPointToPoint(type)
    }
    private fun getHourly(type: TransferNewEntity) =
            type.let {  TransferHourlyNewModel(
                        cityPointMapper.toRemote(it.from),
                        tripMapper.toRemote(it.tripTo),
                        it.transportTypeIds,
                        it.pax,
                        it.childSeats,
                        it.passengerOfferedPrice?.let { int ->  int.toDouble() / 100 },
                        it.nameSign,
                        it.comment,
                        userMapper.toRemote(it.user),
                        it.promoCode,
                        it.duration!!
            ) }

    private fun getPointToPoint(type: TransferNewEntity) =
            type.let { TransferPointToPointNewModel(
                        cityPointMapper.toRemote(it.from),
                        cityPointMapper.toRemote(it.to!!),
                        tripMapper.toRemote(it.tripTo),
                        it.transportTypeIds,
                        it.pax,
                        it.childSeats,
                        it.passengerOfferedPrice?.let { int -> int.toDouble() / 100 },
                        it.nameSign,
                        it.comment,
                        userMapper.toRemote(it.user),
                        it.promoCode) }

}
