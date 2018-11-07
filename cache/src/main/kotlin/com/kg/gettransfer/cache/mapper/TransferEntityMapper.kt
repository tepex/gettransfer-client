package com.kg.gettransfer.cache.mapper

import com.kg.gettransfer.cache.model.TransferCached
import com.kg.gettransfer.data.model.TransferEntity

class TransferEntityMapper(private val cityPointMapper: CityPointEntityMapper,
                           private val moneyMapper: MoneyEntityMapper) : EntityMapper<TransferCached, TransferEntity> {

    override fun mapFromCached(type: TransferCached): TransferEntity {
        return TransferEntity(type.id, type.createdAt, type.duration, type.distance, type.status,
                cityPointMapper.mapFromCached(type.from), type.to?.let { cityPointMapper.mapFromCached(it) },
                type.dateToLocal, type.dateReturnLocal, type.dateRefund, type.nameSign, type.comment,
                type.malinaCard, type.flightNumber, type.flightNumberReturn, type.pax, type.childSeats,
                type.offersCount, type.relevantCarriersCount, type.offersUpdatedAt, type.time,
                type.paidSum?.let { moneyMapper.mapFromCached(it) }, type.remainsToPay?.let { moneyMapper.mapFromCached(it) },
                type.paidPercentage, type.pendingPaymentId, type.bookNow, type.bookNowExpiration,
                type.transportTypeIds, type.passengerOfferedPrice,
                type.price?.let { moneyMapper.mapFromCached(it) }, type.editableFields)
    }

    override fun mapToCached(type: TransferEntity): TransferCached {
        return TransferCached(type.id, type.createdAt, type.duration, type.distance, type.status,
                cityPointMapper.mapToCached(type.from), type.to?.let { cityPointMapper.mapToCached(it) },
                type.dateToLocal, type.dateReturnLocal, type.dateRefund, type.nameSign, type.comment,
                type.malinaCard, type.flightNumber, type.flightNumberReturn, type.pax, type.childSeats,
                type.offersCount, type.relevantCarriersCount, type.offersUpdatedAt, type.time,
                type.paidSum?.let { moneyMapper.mapToCached(it) }, type.remainsToPay?.let { moneyMapper.mapToCached(it) },
                type.paidPercentage, type.pendingPaymentId, type.bookNow, type.bookNowExpiration,
                type.transportTypeIds, type.passengerOfferedPrice,
                type.price?.let { moneyMapper.mapToCached(it) }, type.editableFields)
    }
}