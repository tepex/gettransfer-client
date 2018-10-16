package com.kg.gettransfer.remote.mapper

import com.kg.gettransfer.data.model.CityPointEntity
import com.kg.gettransfer.data.model.MoneyEntity
import com.kg.gettransfer.data.model.TransferEntity

import com.kg.gettransfer.remote.model.CityPointModel
import com.kg.gettransfer.remote.model.MoneyModel
import com.kg.gettransfer.remote.model.TransferModel

/**
 * Map a [TransferEntity] to and from a [TransferModel] instance when data is moving between this later and the Data layer.
 */
open class TransferMapper(private val cityPointMapper: CityPointMapper,
                          private val moneyMapper: MoneyMapper): EntityMapper<TransferModel, TransferEntity> {
    /**
     * Map a [TransferModel] instance to a [TransferEntity] instance.
     */
    override fun fromRemote(type: TransferModel): TransferEntity {
        var to: CityPointEntity? = null
        if(type.to != null) to = cityPointMapper.fromRemote(type.to)
        var price: MoneyEntity? = null
        if(type.price != null) price = moneyMapper.fromRemote(type.price)
            
        return TransferEntity(type.id,
                              type.createdAt,
                              type.duration,
                              type.distance,
                              type.status,
                              cityPointMapper.fromRemote(type.from),
                              to,
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
                              moneyMapper.fromRemote(type.paidSum),
                              moneyMapper.fromRemote(type.remainsToPay),
                              type.paidPercentage,
                              type.pendingPaymentId,
                              type.bookNow,
                              type.bookNowExpiration,
                              type.transportTypeIds,
                              type.passengerOfferedPrice,
                              price,
                            
                              type.editableFields)
    }

    /**
     * Map a [TransferEntity] instance to a [TransferModel] instance.
     */
    override fun toRemote(type: TransferEntity): TransferModel { throw UnsupportedOperationException() }
}
