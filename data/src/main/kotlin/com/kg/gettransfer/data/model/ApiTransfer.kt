package com.kg.gettransfer.data.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class ApiTransfers(@SerializedName("transfers") @Expose var transfers: List<ApiTransfer>)

class ApiTransferWrapper(@SerializedName("transfer") @Expose var transfer: ApiTransfer)

class ApiTransfer(@SerializedName("id") @Expose var id: Long,
                  @SerializedName("created_at") @Expose var createdAt: String,
                  @SerializedName("duration") @Expose var duration: Int?,
                  @SerializedName("distance") @Expose var distance: Int?,
                  @SerializedName("status") @Expose var status: String,
                  @SerializedName("from") @Expose var from: ApiCityPoint,
                  @SerializedName("to") @Expose var to: ApiCityPoint?,
                  @SerializedName("date_to_local") @Expose var dateToLocal: String,
                  @SerializedName("date_return_local") @Expose var dateReturnLocal: String?,
                  @SerializedName("date_refund") @Expose var dateRefund: String,
                  @SerializedName("name_sign") @Expose var nameSign: String?,
                  @SerializedName("comment") @Expose var comment: String?,
                  @SerializedName("malina_card") @Expose var malinaCard: String?,
                  @SerializedName("flight_number") @Expose var flightNumber: String?,
                  @SerializedName("flight_number_return") @Expose var flightNumberReturn: String?,
                  @SerializedName("pax") @Expose var pax: Int,
                  @SerializedName("child_seats") @Expose var childSeats: Int,
                  @SerializedName("offers_count") @Expose var offersCount: Int,
                  @SerializedName("relevant_carriers_count") @Expose var relevantCarriersCount: Int,
                  @SerializedName("offers_updated_at") @Expose var offersUpdatedAt: String?,
                  @SerializedName("time") @Expose var time: Int,
                  @SerializedName("paid_sum") @Expose var paidSum: ApiMoney,
                  @SerializedName("remains_to_pay") @Expose var remainsToPay: ApiMoney,
                  @SerializedName("paid_percentage") @Expose var paidPercentage: Int,
                  @SerializedName("pending_payment_id") @Expose var pendingPaymentId: Int?,
                  @SerializedName("book_now") @Expose var bookNow: Boolean,
                  @SerializedName("book_now_expiration") @Expose var bookNowExpiration: String?,
                  @SerializedName("transport_type_ids") @Expose var transportTypeIds: List<String>,
                  @SerializedName("passenger_offered_price") @Expose var passengerOfferedPrice: String?,
                  @SerializedName("price") @Expose var price: ApiMoney?,
                  @SerializedName("editable_fields") @Expose var editableFields: List<String>)

class ApiMoney(@SerializedName("default") @Expose var default: String,
               @SerializedName("preferred") @Expose var preferred: String?)

class ApiCityPoint(@SerializedName("name") @Expose var name: String,
                   @SerializedName("point") @Expose var point: String,
                   @SerializedName("place_id") @Expose var placeId: String)
