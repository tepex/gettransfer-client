package com.kg.gettransfer.remote.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class TransfersModel(@SerializedName("transfers") val transfers: List<TransferModel>)

class TransferWrapperModel(@SerializedName("transfer") @Expose val transfer: TransferModel)

class TransferModel(@SerializedName("id") val id: Long,
                    @SerializedName("created_at") val createdAt: String,
                    @SerializedName("duration") val duration: Int?,
                    @SerializedName("distance") val distance: Int?,
                    @SerializedName("status") val status: String,
                    @SerializedName("from")  val from: CityPointModel,
                    @SerializedName("to") val to: CityPointModel?,
                    @SerializedName("date_to_local") val dateToLocal: String,
                    @SerializedName("date_return_local") val dateReturnLocal: String?,
                    @SerializedName("date_refund") val dateRefund: String?,
                       
                    @SerializedName("name_sign") val nameSign: String?,
                    @SerializedName("comment") val comment: String?,
                    @SerializedName("malina_card") val malinaCard: String?,
                    @SerializedName("flight_number") val flightNumber: String?,
                    @SerializedName("flight_number_return") val flightNumberReturn: String?,
                    @SerializedName("pax") val pax: Int,
                    @SerializedName("child_seats") val childSeats: Int,
                    @SerializedName("offers_count") val offersCount: Int,
                    @SerializedName("relevant_carriers_count") val relevantCarriersCount: Int,
                    @SerializedName("offers_updated_at") val offersUpdatedAt: String? = null,
                       
                    @SerializedName("time") val time: Int,
                    @SerializedName("paid_sum") val paidSum: MoneyModel,
                    @SerializedName("remains_to_pay") val remainsToPay: MoneyModel,
                    @SerializedName("paid_percentage") val paidPercentage: Int,
                    @SerializedName("pending_payment_id") val pendingPaymentId: Int?,
                    @SerializedName("book_now") val bookNow: Boolean,
                    @SerializedName("book_now_expiration") val bookNowExpiration: String?,
                    @SerializedName("transport_type_ids") val transportTypeIds: List<String>,
                    @SerializedName("passenger_offered_price") val passengerOfferedPrice: String?, // Can be Null!!!
                    @SerializedName("price") val price: MoneyModel?, // Can be Null!!!
                    @SerializedName("editable_fields") val editableFields: List<String>)
