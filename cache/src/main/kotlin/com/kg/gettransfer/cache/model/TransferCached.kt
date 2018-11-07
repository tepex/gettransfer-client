package com.kg.gettransfer.cache.model

import android.arch.persistence.room.Entity
import android.arch.persistence.room.TypeConverters
import android.arch.persistence.room.PrimaryKey
import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Embedded

import com.kg.gettransfer.data.model.TransferEntity

const val TABLE_TRANSFER = "Transfer"

@Entity(tableName = TABLE_TRANSFER)
data class TransferCached(@PrimaryKey var id: Long = 0,
                          @ColumnInfo(name = TransferEntity.CREATED_AT) var createdAt: String = "",
                          @ColumnInfo(name = TransferEntity.DURATION) var duration: Int? = 0,
                          @ColumnInfo(name = TransferEntity.DISTANCE) var distance: Int? = 0,
                          @ColumnInfo(name = TransferEntity.STATUS) var status: String = "",
                          @Embedded(prefix = TransferEntity.FROM)
                          var from: CityPointCached,
                          @Embedded(prefix = TransferEntity.TO)
                          var to: CityPointCached?,
                          @ColumnInfo(name = TransferEntity.DATE_TO_LOCAL) var dateToLocal: String = "",
                          @ColumnInfo(name = TransferEntity.DATE_RETURN_LOCAL) var dateReturnLocal: String? = "",
                          @ColumnInfo(name = TransferEntity.DATE_REFUND) var dateRefund: String? = "",

        /* Имя на табличке, которую держит встречающий (сейчас поле full_name) */
                          @ColumnInfo(name = TransferEntity.NAME_SIGN) var nameSign: String? = "",
                          @ColumnInfo(name = TransferEntity.COMMENT) var comment: String? = "",
                          @ColumnInfo(name = TransferEntity.MALINA_CARD) var malinaCard: String? = "",
                          @ColumnInfo(name = TransferEntity.FLIGHT_NUMBER) var flightNumber: String? = "",
                          @ColumnInfo(name = TransferEntity.FLIGHT_NUMBER_RETURN) var flightNumberReturn: String? = "",
                          @ColumnInfo(name = TransferEntity.PAX) var pax: Int = 0,
                          @ColumnInfo(name = TransferEntity.CHILD_SEATS) var childSeats: Int = 0,
                          @ColumnInfo(name = TransferEntity.OFFERS_COUNT) var offersCount: Int = 0,
                          @ColumnInfo(name = TransferEntity.RELEVANT_CARRIERS_COUNT) var relevantCarriersCount: Int = 0,
                          @ColumnInfo(name = TransferEntity.OFFERS_UPDATED_AT) var offersUpdatedAt: String? = "",

                          @ColumnInfo(name = TransferEntity.TIME) var time: Int = 0,
                          @Embedded(prefix = TransferEntity.PAID_SUM)
                          var paidSum: MoneyCached?,
                          @Embedded(prefix = TransferEntity.REMAINS_TO_PAY)
                          var remainsToPay: MoneyCached?,
                          @ColumnInfo(name = TransferEntity.PAID_PERCENTAGE) var paidPercentage: Int = 0,
                          @ColumnInfo(name = TransferEntity.PENDING_PAYMENT_ID) var pendingPaymentId: Int? = 0,
                          @ColumnInfo(name = TransferEntity.BOOK_NOW) var bookNow: Boolean = false,
                          @ColumnInfo(name = TransferEntity.BOOK_NOW_EXPIRATION) var bookNowExpiration: String? = "",
                          @ColumnInfo(name = TransferEntity.TRANSPORT_TYPE_IDS) var transportTypeIds: List<String> = emptyList(),
                          @ColumnInfo(name = TransferEntity.PASSENGER_OFFERED_PRICE) var passengerOfferedPrice: String? = "",
                          @Embedded(prefix = TransferEntity.PRICE)
                          var price: MoneyCached?,
                          @ColumnInfo(name = TransferEntity.EDITABLE_FIELDS)
                          var editableFields: List<String> = emptyList())