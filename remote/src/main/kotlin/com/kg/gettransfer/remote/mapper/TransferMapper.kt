package com.kg.gettransfer.remote.mapper

import com.kg.gettransfer.data.model.TransferEntity
import com.kg.gettransfer.remote.model.TransferModel

import org.koin.standalone.get

/**
 * Map a [TransferEntity] to and from a [TransferModel] instance when data is moving between this later and the Data layer.
 */
open class TransferMapper: EntityMapper<TransferModel, TransferEntity> {
    private val cityPointMapper = get<CityPointMapper>()
    private val moneyMapper     = get<MoneyMapper>()

    /**
     * Map a [TransferModel] instance to a [TransferEntity] instance.
     */
    override fun fromRemote(type: TransferModel) =
        TransferEntity(type.id,
                       type.createdAt,
                       type.duration,
                       type.distance,
                       type.status,
                       cityPointMapper.fromRemote(type.from),
                       type.to?.let { cityPointMapper.fromRemote(it) },
                       type.dateToLocal,
                       type.dateReturnLocal,
                       type.dateRefund,

                       type.nameSign,
                       type.comment,
                       type.malinaCard,
                       type.flightNumber,
                       type.flightNumberReturn,
                       type.pax,
                       type.childSeats,
                       type.offersCount,
                       type.relevantCarriersCount,
                       type.offersUpdatedAt,

                       type.time,
                       type.paidSum?.let { moneyMapper.fromRemote(it) },
                       type.remainsToPay?.let { moneyMapper.fromRemote(it) },
                       type.paidPercentage,
                       type.pendingPaymentId,
                       type.bookNow,
                       type.bookNowExpiration,
                       type.transportTypeIds,
                       type.passengerOfferedPrice,
                       type.price?.let { moneyMapper.fromRemote(it) },

                       type.editableFields)

    /**
     * Map a [TransferEntity] instance to a [TransferModel] instance.
     */
    override fun toRemote(type: TransferEntity): TransferModel { throw UnsupportedOperationException() }
}
