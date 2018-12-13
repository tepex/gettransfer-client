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
open class TransferMapper : Mapper<TransferEntity, Transfer> {
    private val cityPointMapper = get<CityPointMapper>()
    private val moneyMapper     = get<MoneyMapper>()
    private val dateFormat      = get<ThreadLocal<DateFormat>>("iso_date")





    /**
     * Map a [TransferEntity] instance to a [Transfer] instance.
     */
    override fun fromEntity(type: TransferEntity) =
        Transfer(
            id = type.id,
            createdAt = dateFormat.get().parse(type.createdAt),
            duration = type.duration,
            distance = type.distance,
            status = Transfer.Status.valueOf(type.status.toUpperCase()),
            from = cityPointMapper.fromEntity(type.from),
            to = type.to?.let { cityPointMapper.fromEntity(it) },
            dateToLocal = dateFormat.get().parse(type.dateToLocal),
            dateReturnLocal = type.dateReturnLocal?.let { dateFormat.get().parse(it) },
            dateRefund = type.dateRefund?.let { dateFormat.get().parse(it) },
/* ================================================== */
            nameSign = type.nameSign,
            comment = type.comment,
            malinaCard = type.malinaCard,
            flightNumber = type.flightNumber,
            flightNumberReturn = type.flightNumberReturn,
            pax = type.pax,
            childSeats = type.childSeats,
            promoCode = type.promoCode,
            offersCount = type.offersCount,
            relevantCarriersCount = type.relevantCarriersCount,
            /* offersUpdatedAt */
/* ================================================== */
            time = type.time,
            paidSum = type.paidSum?.let { moneyMapper.fromEntity(it) },
            remainsToPay = type.remainsToPay?.let { moneyMapper.fromEntity(it) },
            paidPercentage = type.paidPercentage,
            pendingPaymentId = type.pendingPaymentId,
            bookNow = type.bookNow,
            bookNowExpiration = type.bookNowExpiration,
            transportTypeIds = type.transportTypeIds,
            passengerOfferedPrice = type.passengerOfferedPrice,
            price = type.price?.let { moneyMapper.fromEntity(it) },
/* ================================================== */
            paymentPercentages = type.paymentPercentages,
            editableFields = type.editableFields
        )

    /**
     * Map a [Transfer] instance to a [TransferEntity] instance.
     */
    override fun toEntity(type: Transfer): TransferEntity { throw UnsupportedOperationException() }
}
