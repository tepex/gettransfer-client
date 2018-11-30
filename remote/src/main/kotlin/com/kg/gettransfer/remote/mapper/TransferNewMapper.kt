package com.kg.gettransfer.remote.mapper

import com.kg.gettransfer.data.model.CityPointEntity
import com.kg.gettransfer.data.model.DestDurationEntity
import com.kg.gettransfer.data.model.DestEntity
import com.kg.gettransfer.data.model.DestPointEntity
import com.kg.gettransfer.data.model.TransferNewEntity

import com.kg.gettransfer.remote.model.TransferHourlyNewModel
import com.kg.gettransfer.remote.model.TransferNewBase
import com.kg.gettransfer.remote.model.TransferPointToPointNewModel

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
    override fun toRemote(type: TransferNewEntity): TransferNewBase {
        val dest = type.dest
        return when(dest) {
            is DestDurationEntity -> getHourly(type, dest.duration)
            is DestPointEntity    -> getPointToPoint(type, dest.to)
        }
    }

    private fun getHourly(type: TransferNewEntity, duration: Int) =
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
                        duration
            ) }

    private fun getPointToPoint(type: TransferNewEntity, to: CityPointEntity) =
            type.let { TransferPointToPointNewModel(
                       cityPointMapper.toRemote(it.from),
                       cityPointMapper.toRemote(to),
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
