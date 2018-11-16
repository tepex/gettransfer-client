package com.kg.gettransfer.data.mapper

import com.kg.gettransfer.data.model.MoneyEntity
import com.kg.gettransfer.data.model.TransferEntity

import com.kg.gettransfer.domain.model.CityPoint
import com.kg.gettransfer.domain.model.Money
import com.kg.gettransfer.domain.model.Transfer

import java.text.SimpleDateFormat

import java.util.Locale

/**
 * Map a [TransferEntity] to and from a [Transfer] instance when data is moving between this later and the Domain layer.
 */
open class TransferMapper(private val cityPointMapper: CityPointMapper,
                          private val moneyMapper: MoneyMapper): Mapper<TransferEntity, Transfer> {
    /**
     * Map a [TransferEntity] instance to a [Transfer] instance.
     */
    override fun fromEntity(type: TransferEntity) =
        Transfer(type.id,
                 SimpleDateFormat(Mapper.ISO_FORMAT_STRING, Locale.US).parse(type.createdAt),
                 type.duration,
                 type.distance,
                 type.status,
                 cityPointMapper.fromEntity(type.from),
                 type.to?.let { cityPointMapper.fromEntity(it) },
                 SimpleDateFormat(Mapper.ISO_FORMAT_STRING, Locale.US).parse(type.dateToLocal),
                 type.dateReturnLocal?.let { SimpleDateFormat(Mapper.ISO_FORMAT_STRING, Locale.US).parse(it) },
                 type.dateRefund?.let { SimpleDateFormat(Mapper.ISO_FORMAT_STRING, Locale.US).parse(it) },
                        
                 type.nameSign,
                 type.comment,
                 type.malinaCard,
                 type.flightNumber,
                 type.flightNumberReturn,
                 type.pax,
                 type.childSeats,
                 type.offersCount,
                 type.relevantCarriersCount,
                 type.offersUpdatedAt?.let { SimpleDateFormat(Mapper.ISO_FORMAT_STRING, Locale.US).parse(it) },
                        
                 type.time,
                 type.paidSum?.let { moneyMapper.fromEntity(it) },
                 type.remainsToPay?.let { moneyMapper.fromEntity(it) },
                 type.paidPercentage,
                 type.pendingPaymentId,
                 type.bookNow,
                 type.bookNowExpiration,
                 type.transportTypeIds,
                 type.passengerOfferedPrice,
                 type.price?.let { moneyMapper.fromEntity(it) },
                            
                 type.editableFields)

    /**
     * Map a [Transfer] instance to a [TransferEntity] instance.
     */
    override fun toEntity(type: Transfer): TransferEntity { throw UnsupportedOperationException() }
}
