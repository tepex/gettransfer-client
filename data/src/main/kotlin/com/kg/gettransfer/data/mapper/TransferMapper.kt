package com.kg.gettransfer.data.mapper

import com.kg.gettransfer.data.model.TransferEntity

import com.kg.gettransfer.domain.model.Transfer

/**
 * Map a [TransferEntity] to and from a [Transfer] instance when data is moving between
 * this later and the Domain layer.
 */
open class TransferMapper(private val cityPointMapper: CityPointMapper,
                          private val moneyMapper: MoneyMapper,
                             private val carrierVehicleMapper: CarrierVehicleMapper,
                             private val passengerAccountMapper: PassengerAccountMapper): Mapper<TransferEntity, Transfer> {
    /**
     * Map a [TransferEntity] instance to a [Transfer] instance.
     */
    override fun fromEntity(type: TransferEntity): Transfer {
        var to: CityPoint? = null
        if(type.to != null) to = cityPointMapper.fromEntity(type.to!!)
            
        var dateReturnLocal: Date? = null
        if(type.dateReturnLocal != null) dateReturnLocal = Mapper.ISO_FORMAT.parse(type.dateReturnLocal)
        var dateRefund: Date? = null
        if(type.dateRefund != null) dateRefund = Mapper.ISO_FORMAT.parse(type.dateRefund) 
        var offersUpdatedAt: Date? = null
        if(type.offersUpdatedAt != null) offersUpdatedAt = Mapper.ISO_FORMAT.parse(type.offersUpdatedAt)
            
        var paidSum: Money? = null
        if(type.paidSum != null) paidSum = moneyMapper.fromEntity(type.paidSum!!)
        var remainsToPay: Money? = null
        if(type.remainsToPay != null) remainsToPay = moneyMapper.fromEntity(type.remainsToPay!!)
        var price: Money? = null
        if(type.price != null) price = moneyMapper.fromEntity(type.price!!)

        return Transfer(type.id,
                        Mapper.ISO_FORMAT.parse(type.createdAt),
                        type.duration,
                        type.distance,
                        type.status,
                        cityPointMapper.fromEntity(type.from),
                        to,
                        Mapper.ISO_FORMAT.parse(type.dateToLocal),
                        dateReturnLocal,
                        dateRefund,
                        
                        type.nameSign,
                        type.comment,
                        type.malinaCard,
                        type.flightNumber,
                        type.flightNumberReturn,
                        type.pax,
                        type.childSeats,
                        type.offersCount,
                        type.relevantCarriersCount,
                        offersUpdatedAt,
                        
                        apiTransfer.time,
                        paidSum,
                        remainsToPay,
                        type.paidPercentage,
                        type.pendingPaymentId,
                        type.bookNow!!,
                        type.bookNowExpiration,
                        type.transportTypeIds,
                        type.passengerOfferedPrice,
                        price,
                            
                        type.editableFields!!)
    }

    /**
     * Map a [Transfer] instance to a [TransferEntity] instance.
     */
    override fun toEntity(type: Transfer): TransferEntity {
        throw UnsupportedOperationException()
    }
}
