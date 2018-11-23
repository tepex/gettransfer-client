package com.kg.gettransfer.presentation.model

import android.location.Location

import android.support.annotation.StringRes

import com.google.android.gms.maps.model.LatLng

import com.kg.gettransfer.R
import com.kg.gettransfer.domain.model.*
import com.kg.gettransfer.presentation.ui.Utils

import java.util.Currency
import java.util.Locale

object Mappers {
    fun point2LatLng(point: Point) = LatLng(point.latitude, point.longitude)
    
    fun point2Location(point: Point): Location {
        val ret = Location("")
        ret.latitude = point.latitude
        ret.longitude = point.longitude
        return ret
    }
    
    fun latLng2Point(latLng: LatLng) = Point(latLng.latitude, latLng.longitude)
    
    fun getUserModel(type: Account) = UserModel(getProfileModel(type.user.profile), type.user.termsAccepted)
    
    fun getProfileModel(type: Profile) = ProfileModel(type.fullName, type.email, type.phone)

    fun getProfile(model: ProfileModel) = Profile(model.name, model.email, model.phone)
    
    fun getUser(model: UserModel) = User(getProfile(model.profile), model.termsAccepted)

    /*
    fun getAccount(model: User,
                   locale: Locale?,
                   currency: Currency?,
                   distanceUnit: DistanceUnit?,
                   groups: List<String>?,
                   carrierId: Long?) = Account(model, locale, currency, distanceUnit, groups, carrierId)
                   */

    fun getTransportTypeModel(type: TransportType, prices: Map<String, TransportPrice>?): TransportTypeModel {
        val imageRes = R.drawable::class.members.find( { it.name == "ic_transport_type_${type.id}" } )
        val imageId = (imageRes?.call() as Int?) ?: R.drawable.ic_transport_type_unknown
        return TransportTypeModel(type.id,
                                  getTransportTypeName(type.id),
                                  imageId,
                                  type.paxMax,
                                  type.luggageMax,
                                  prices?.get(type.id))
    }
        
    @StringRes
    fun getTransportTypeName(id: String): Int {
        val nameRes = R.string::class.members.find( { it.name == "LNG_TRANSPORT_${id.toUpperCase()}" } )
        return (nameRes?.call() as Int?) ?: R.string.LNG_TRANSPORT_ECONOMY
    }

    fun getCurrenciesModels(types: List<Currency>)        = types.map { CurrencyModel(it) }
    fun getLocalesModels(types: List<Locale>)             = types.map { LocaleModel(it) }
    fun getDistanceUnitsModels(types: List<DistanceUnit>) = types.map { DistanceUnitModel(it) }
    
    fun getEndpointModel(endpoint: Endpoint) = EndpointModel(endpoint)
    
    fun getRouteModel(distance: Int?,
                      distanceUnit: DistanceUnit,
                      polyLines: List<String>?,
                      from: String,
                      to: String,
                      fromPoint: Point,
                      toPoint: Point,
                      dateTime: String) = RouteModel(distance ?: checkDistance(fromPoint, toPoint),
                                                     distanceUnit,
                                                     polyLines,
                                                     from,
                                                     to,
                                                     fromPoint,
                                                     toPoint,
                                                     dateTime)
    
    fun getTransferModel(type: Transfer,
                         locale: Locale,
                         distanceUnit: DistanceUnit,
                         transportTypes: List<TransportType>): TransferModel {
        val selected = transportTypes.filter { type.transportTypeIds.contains(it.id) }
        var distance: Int? = null
        if(type.to != null) distance = type.distance ?: checkDistance(type.from.point!!, type.to!!.point!!)
        
        return TransferModel(type.id,
                             type.status,
                             type.from.name!!,
                             type.to?.name,
                             Utils.getFormattedDate(locale, type.dateToLocal),
                             distance,
                             distanceUnit,
                             type.pax,
                             type.nameSign,
                             type.childSeats,
                             type.flightNumber,
                             type.comment,
                             selected.map { getTransportTypeModel(it, null) },
                             type.paidSum?.default,
                             type.paidPercentage,
                             type.remainsToPay?.default,
                             type.price?.default,
                             type.relevantCarriersCount,
                             type.checkOffers,
                             type.dateRefund,
                             type.duration)
    }
    
    fun getTransferNew(from: CityPoint,
                       to: CityPoint,
                       tripTo: Trip,
                       tripReturn: Trip?,
                       transportTypes: List<String>,
                       pax: Int,
                       childSeats: Int?,
                       passengerOfferedPrice: Double?,
                       comment: String?,
                       user: User,
                       promoCode: String?,
                       paypalOnly: Boolean) = TransferNew(from, 
                                                          to,
                                                          tripTo,
                                                          tripReturn,
                                                          transportTypes,
                                                          pax,
                                                          childSeats,
                                                          passengerOfferedPrice?.let { it.times(100).toInt() },
                                                          comment,
                                                          user,
                                                          promoCode,
                                                          paypalOnly)

    fun getOfferModel(type: Offer, locale: Locale) = 
        OfferModel(type.id,
                   type.status,
                   type.wifi,
                   type.refreshments,
                   Utils.getFormattedDate(locale, type.createdAt),
                   getPriceModel(type.price),
                   type.ratings?.let { getRatingsModel(it) },
                   type.passengerFeedback,
                   getCarrierModel(type.carrier),
                   getVehicleModel(type.vehicle),
                   type.driver?.let { getProfileModel(it) })

    fun getCarrierTripModel(type: CarrierTrip, locale: Locale, distanceUnit: DistanceUnit) =
        CarrierTripModel(type.id,
                         type.transferId,
                         type.from.name!!,
                         type.to.name!!,
                         Utils.getFormattedDate(locale, type.dateLocal),
                         type.distance ?: checkDistance(type.from.point!!, type.to.point!!),
                         distanceUnit,
                         type.childSeats,
                         type.comment,
                         type.price,
                         type.vehicle.name,
                         type.pax,
                         type.nameSign,
                         type.flightNumber,
                         type.remainToPay)
    
    fun getRatingsModel(type: Ratings) = RatingsModel(type.average, type.vehicle, type.driver, type.fair)
    
    fun getMoneyModel(type: Money) = MoneyModel(type.default, type.preferred)
    
    fun getPriceModel(type: Price) = PriceModel(getMoneyModel(type.base),
                                                type.withoutDiscount?.let { getMoneyModel(it)},
                                                type.percentage30,
                                                type.percentage70,
                                                "%.2f".format(type.amount))
    
    fun getCarrierModel(type: Carrier) = CarrierModel(type.id,
                                                         getProfileModel(type.profile),
                                                         type.approved,
                                                         type.completedTransfers,
                                                         getLocalesModels(type.languages),
                                                         getRatingsModel(type.ratings),
                                                         type.canUpdateOffers)
    
    fun getVehicleModel(type: Vehicle) =
        VehicleModel(VehicleBaseModel(type.vehicleBase.name, type.vehicleBase.registrationNumber),
                     type.year,
                     type.color,
                     getTransportTypeModel(type.transportType, null),
                     type.photos)
        
    fun getPaymentRequest(model: PaymentRequestModel) =
        PaymentRequest(model.transferId, model.offerId, model.gatewayId, model.percentage)

    fun getPaymentStatusRequest(model: PaymentStatusRequestModel) =
        PaymentStatusRequest(model.paymentId, model.pgOrderId, model.withoutRedirect, model.success)
        
    private fun checkDistance(from: Point, to: Point) = (point2Location(from).distanceTo(point2Location(to)) / 1000).toInt()
}
