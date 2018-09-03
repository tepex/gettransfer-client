package com.kg.gettransfer.data.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class ApiTransfers(@SerializedName("transfers") var transfers: List<ApiTransfer>)

class ApiTransferWrapper(@SerializedName("transfer") @Expose var transfer: ApiTransfer)

open class ApiTransfer(@SerializedName("id") var id: Long? = null,
                       @SerializedName("created_at") var createdAt: String? = null,
                       @SerializedName("duration") var duration: Int? = null,
                       @SerializedName("distance") var distance: Int? = null,
                       @SerializedName("status") var status: String? = null,
                       @SerializedName("from") @Expose  var from: ApiCityPoint,
                       @SerializedName("to") @Expose var to: ApiCityPoint?,
                       @SerializedName("date_to_local") var dateToLocal: String? = null,
                       @SerializedName("date_return_local") var dateReturnLocal: String? = null,
                       @SerializedName("date_refund") var dateRefund: String? = null,
                       /* Имя на табличке, которую держит встречающий (сейчас поле full_name) */
                       @SerializedName("name_sign") @Expose var nameSign: String,
                       @SerializedName("comment") @Expose var comment: String? = null,
                       @SerializedName("malina_card") var malinaCard: String? = null,
                       @SerializedName("flight_number") var flightNumber: String? = null,
                       @SerializedName("flight_number_return") var flightNumberReturn: String? = null,
                       @SerializedName("pax") @Expose var pax: Int,
                       @SerializedName("child_seats") @Expose var childSeats: Int? = null,
                       @SerializedName("offers_count") var offersCount: Int? = null,
                       @SerializedName("relevant_carriers_count") var relevantCarriersCount: Int? = null,
                       @SerializedName("offers_updated_at") var offersUpdatedAt: String? = null,
                       @SerializedName("time") var time: Int? = null,
                       @SerializedName("paid_sum") var paidSum: ApiMoney? = null,
                       @SerializedName("remains_to_pay") var remainsToPay: ApiMoney? = null,
                       @SerializedName("paid_percentage") var paidPercentage: Int? = null,
                       @SerializedName("pending_payment_id") var pendingPaymentId: Int? = null,
                       @SerializedName("book_now") var bookNow: Boolean? = null,
                       @SerializedName("book_now_expiration") var bookNowExpiration: String? = null,
                       @SerializedName("transport_type_ids") @Expose var transportTypeIds: List<String>,
                       @SerializedName("passenger_offered_price") @Expose var passengerOfferedPrice: String? = null,
                       @SerializedName("price") var price: ApiMoney? = null,
                       @SerializedName("editable_fields") var editableFields: List<String>? = null)

class ApiTransferRequest(from: ApiCityPoint, to: ApiCityPoint,
                         @SerializedName("trip_to") @Expose val tripTo: ApiTrip,
                         @SerializedName("trip_return") @Expose val tripReturn: ApiTrip?,
                         transportTypeIds: List<String>,
                         pax: Int,
                         childSeats: Int?,
                         passengerOfferedPrice: String?,
                         nameSign: String,
                         comment: String?,
                         @SerializedName("passenger_account") @Expose val account: ApiAccount,
                         @SerializedName("promo_code") @Expose val promoCode: String?): ApiTransfer(
                             from = from, to = to, transportTypeIds = transportTypeIds, pax = pax, childSeats = childSeats,
                             passengerOfferedPrice = passengerOfferedPrice, nameSign = nameSign, comment = comment)
/* В данный момент не используется
                         @SerializedName("paypal_only") @Expose val paypalOnly: Boolean) */

class ApiMoney(@SerializedName("default") @Expose var default: String,
               @SerializedName("preferred") @Expose var preferred: String?)

class ApiCityPoint(@SerializedName("name") @Expose var name: String,
                   @SerializedName("point") @Expose var point: String,
                   @SerializedName("place_id") @Expose var placeId: String)

class ApiTrip(@SerializedName("date") @Expose val date: String,
              @SerializedName("time") @Expose val time: String,
              @SerializedName("flight_number") @Expose val flight: String?)
