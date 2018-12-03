package com.kg.gettransfer.data.mapper

import com.kg.gettransfer.data.model.MoneyEntity
import com.kg.gettransfer.data.model.TransferEntity

import com.kg.gettransfer.domain.model.CityPoint
import com.kg.gettransfer.domain.model.Money
import com.kg.gettransfer.domain.model.Transfer

import java.text.DateFormat

import org.koin.standalone.get

/**
 * Map a [TransferEntity] to and from a [Transfer] instance when data is moving between this later and the Domain layer.
 */
open class TransferMapper: Mapper<TransferEntity, Transfer> {
    private val cityPointMapper = get<CityPointMapper>()
    private val moneyMapper = get<MoneyMapper>()
    private val dateFormat = get<ThreadLocal<DateFormat>>("iso_date")

    /**
     * Map a [TransferEntity] instance to a [Transfer] instance.
     */
    override fun fromEntity(type: TransferEntity) =
        Transfer(type.id,
                 dateFormat.get().parse(type.createdAt),
                 type.duration,
                 type.distance,
                 type.status,
                 cityPointMapper.fromEntity(type.from),
                 type.to?.let { cityPointMapper.fromEntity(it) },
                 dateFormat.get().parse(type.dateToLocal),
                 type.dateReturnLocal?.let { dateFormat.get().parse(it) },
                 type.dateRefund?.let { dateFormat.get().parse(it) },

                 type.nameSign,
                 type.comment,
                 type.malinaCard,
                 type.flightNumber,
                 type.flightNumberReturn,
                 type.pax,
                 type.childSeats,
                 type.offersCount,
                 type.relevantCarriersCount,
                 type.offersUpdatedAt?.let { dateFormat.get().parse(it) },

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
                 type.paymentPercentages,

                 type.editableFields)

    /**
     * Map a [Transfer] instance to a [TransferEntity] instance.
     */
    override fun toEntity(type: Transfer): TransferEntity { throw UnsupportedOperationException() }
}
