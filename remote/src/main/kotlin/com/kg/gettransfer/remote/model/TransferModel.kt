package com.kg.gettransfer.remote.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

import com.kg.gettransfer.data.model.TransferEntity

const val TRANSFER = "transfer"

class TransferModel(@SerializedName(TransferEntity.ID) val id: Long,
                    @SerializedName(TransferEntity.CREATED_AT) val createdAt: String,
                    @SerializedName(TransferEntity.DURATION) val duration: Int?,
                    @SerializedName(TransferEntity.DISTANCE) val distance: Int?,
                    @SerializedName(TransferEntity.STATUS) val status: String,
                    @SerializedName(TransferEntity.FROM)  val from: CityPointModel,
                    @SerializedName(TransferEntity.TO) val to: CityPointModel?,
                    @SerializedName(TransferEntity.DATE_TO_LOCAL) val dateToLocal: String,
                    @SerializedName(TransferEntity.DATE_RETURN_LOCAL) val dateReturnLocal: String?,
                    @SerializedName(TransferEntity.DATE_REFUND) val dateRefund: String?,
                       
                    @SerializedName(TransferEntity.NAME_SIGN) val nameSign: String?,
                    @SerializedName(TransferEntity.COMMENT) val comment: String?,
                    @SerializedName(TransferEntity.MALINA_CARD) val malinaCard: String?,
                    @SerializedName(TransferEntity.FLIGHT_NUMBER) val flightNumber: String?,
                    @SerializedName(TransferEntity.FLIGHT_NUMBER_RETURN) val flightNumberReturn: String?,
                    @SerializedName(TransferEntity.PAX) val pax: Int,
                    @SerializedName(TransferEntity.CHILD_SEATS) val childSeats: Int,
                    @SerializedName(TransferEntity.OFFERS_COUNT) val offersCount: Int,
                    @SerializedName(TransferEntity.RELEVANT_CARRIERS_COUNT) val relevantCarriersCount: Int,
                    @SerializedName(TransferEntity.OFFERS_UPDATED_AT) val offersUpdatedAt: String? = null,
                       
                    @SerializedName(TransferEntity.TIME) val time: Int,
                    @SerializedName(TransferEntity.PAID_SUM) val paidSum: MoneyModel,
                    @SerializedName(TransferEntity.REMAINS_TO_PAY) val remainsToPay: MoneyModel?,
                    @SerializedName(TransferEntity.PAID_PERCENTAGE) val paidPercentage: Int,
                    @SerializedName(TransferEntity.PENDING_PAYMENT_ID) val pendingPaymentId: Int?,
                    @SerializedName(TransferEntity.BOOK_NOW) val bookNow: Boolean,
                    @SerializedName(TransferEntity.BOOK_NOW_EXPIRATION) val bookNowExpiration: String?,
                    @SerializedName(TransferEntity.TRANSPORT_TYPE_IDS) val transportTypeIds: List<String>,
                    @SerializedName(TransferEntity.PASSENGER_OFFERED_PRICE) val passengerOfferedPrice: String?, // Can be Null!!!
                    @SerializedName(TransferEntity.PRICE) val price: MoneyModel?, // Can be Null!!!
                    
                    @SerializedName(TransferEntity.EDITABLE_FIELDS) val editableFields: List<String>)

class TransfersModel(@SerializedName("transfers") val transfers: List<TransferModel>)

class TransferWrapperModel(@SerializedName(TRANSFER) @Expose val transfer: TransferModel)
