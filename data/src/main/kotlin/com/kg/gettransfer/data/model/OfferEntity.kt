package com.kg.gettransfer.data.model

data class OfferEntity(val id: Long,
                       val status: String,
                       val wifi: Boolean,
                       val refreshments: Boolean,
                       val createdAt: String,
                       val price: PriceEntity,
                       val ratings: RatingsEntity?,
                       val passengerFeedback: String?,
                       val carrier: CarrierEntity,
                       val vehicle: VehicleEntity,
                       val driver: DriverEntity?)

data class PriceEntity(val base: MoneyEntity,
                       val percentage30: String,
                       val percentage70: String,
                       val amount: Double)

data class RatingsEntity(val average: Double?,
                         val vehicle: Double?,
                         val driver: Double?,
                         val fair: Double?)

data class CarrierEntity(val id: Long,
                         val user: UserEntity,
                         val title: String?,
                         val email: String?,
                         val phone: String?,
                         val approved: Boolean,
                 @SerializedName("completed_transfers") @Expose var completedTransfers: Int,
                 @SerializedName("languages") @Expose var languages: List<ApiLocales>,
                 @SerializedName("ratings") @Expose var ratings: ApiRatings,
                 @SerializedName("can_update_offers") @Expose var canUpdateOffers: Boolean?)

class ApiVehicle(@SerializedName("name") @Expose var name: String,
                 @SerializedName("registration_number") @Expose var registrationNumber: String,
                 @SerializedName("year") @Expose var year: Int,
                 @SerializedName("color") @Expose var color: String,
                 @SerializedName("transport_type_id") @Expose var transportTypeId: String,
                 @SerializedName("pax_max") @Expose var paxMax: Int,
                 @SerializedName("luggage_max") @Expose var luggageMax: Int,
                 @SerializedName("photos") @Expose var photos: List<String>)

class ApiDriver(@SerializedName("full_name") @Expose var fullName: String,
                @SerializedName("phone") @Expose var phone: String,
                @SerializedName("email") @Expose var email: String)
