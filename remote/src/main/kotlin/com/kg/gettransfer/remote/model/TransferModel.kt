package com.kg.gettransfer.remote.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

import com.kg.gettransfer.data.model.TransferEntity

const val TRANSFER = "transfer"

class TransferModel(@SerializedName(TransferEntity.ID) @Expose val id: Long,
                    @SerializedName(TransferEntity.CREATED_AT) @Expose val createdAt: String,
                    @SerializedName(TransferEntity.DURATION) @Expose val duration: Int?,
                    @SerializedName(TransferEntity.DISTANCE) @Expose val distance: Int?,
                    @SerializedName(TransferEntity.STATUS) @Expose val status: String,
                    @SerializedName(TransferEntity.FROM) @Expose  val from: CityPointModel,
                    @SerializedName(TransferEntity.TO) @Expose val to: CityPointModel?,
                    @SerializedName(TransferEntity.DATE_TO_LOCAL) @Expose val dateToLocal: String,
                    @SerializedName(TransferEntity.DATE_RETURN_LOCAL) @Expose val dateReturnLocal: String?,
                    @SerializedName(TransferEntity.DATE_REFUND) @Expose val dateRefund: String?,

                    @SerializedName(TransferEntity.NAME_SIGN) @Expose val nameSign: String?,
                    @SerializedName(TransferEntity.COMMENT) @Expose val comment: String?,
                    @SerializedName(TransferEntity.MALINA_CARD) @Expose val malinaCard: String?,
                    @SerializedName(TransferEntity.FLIGHT_NUMBER) @Expose val flightNumber: String?,
                    @SerializedName(TransferEntity.FLIGHT_NUMBER_RETURN) @Expose val flightNumberReturn: String?,
                    @SerializedName(TransferEntity.PAX) @Expose val pax: Int,
                    @SerializedName(TransferEntity.CHILD_SEATS) @Expose val childSeats: Int,
                    @SerializedName(TransferEntity.PROMO_CODE) @Expose val promoCode: String?,
                    @SerializedName(TransferEntity.OFFERS_COUNT) @Expose val offersCount: Int,
                    @SerializedName(TransferEntity.RELEVANT_CARRIERS_COUNT) @Expose val relevantCarriersCount: Int,
                    @SerializedName(TransferEntity.OFFERS_UPDATED_AT) @Expose val offersUpdatedAt: String? = null,

                    @SerializedName(TransferEntity.TIME) @Expose val time: Int,
                    @SerializedName(TransferEntity.PAID_SUM) @Expose val paidSum: MoneyModel?,
                    @SerializedName(TransferEntity.REMAINS_TO_PAY) @Expose val remainsToPay: MoneyModel?,
                    @SerializedName(TransferEntity.PAID_PERCENTAGE) @Expose val paidPercentage: Int,
                    @SerializedName(TransferEntity.PENDING_PAYMENT_ID) @Expose val pendingPaymentId: Int?,
                    @SerializedName(TransferEntity.BOOK_NOW) @Expose val bookNow: Boolean,
                    @SerializedName(TransferEntity.BOOK_NOW_EXPIRATION) @Expose val bookNowExpiration: String?,
                    @SerializedName(TransferEntity.TRANSPORT_TYPE_IDS) @Expose val transportTypeIds: List<String>,
                    @SerializedName(TransferEntity.PASSENGER_OFFERED_PRICE) @Expose val passengerOfferedPrice: String?, // Can be Null!!!
                    @SerializedName(TransferEntity.PRICE) @Expose val price: MoneyModel?, // Can be Null!!!
                    @SerializedName(TransferEntity.PAYMENT_PERCENTAGES) @Expose val paymentPercentages: List<Int>,

                    @SerializedName(TransferEntity.EDITABLE_FIELDS) @Expose val editableFields: List<String>)

class TransfersModel(@SerializedName("transfers") @Expose val transfers: List<TransferModel>)

class TransferWrapperModel(@SerializedName(TRANSFER) @Expose val transfer: TransferModel)
