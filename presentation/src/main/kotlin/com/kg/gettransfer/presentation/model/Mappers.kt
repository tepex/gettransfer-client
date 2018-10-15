package com.kg.gettransfer.presentation.model

import android.support.annotation.StringRes

import com.google.android.gms.maps.model.LatLng

import com.kg.gettransfer.R
import com.kg.gettransfer.domain.model.*
import com.kg.gettransfer.presentation.ui.Utils

import java.util.Currency
import java.util.Locale

object Mappers {
    fun point2LatLng(point: Point) = LatLng(point.latitude, point.longitude)
    fun latLng2Point(latLng: LatLng) = Point(latLng.latitude, latLng.longitude)
    
    fun getUserModel(account: Account) = UserModel(getProfileModel(account), account.user.termsAccepted)
    
    fun getProfileModel(account: Account) = ProfileModel(account.user.profile.name,
                                                         account.user.profile.email,
                                                         account.user.profile.phone)

    fun getProfile(profileModel: ProfileModel) = Profile(profileModel.name, profileModel.email, profileModel.phone)
    
    fun getUser(userModel: UserModel) = User(getProfile(userModel.profile), userModel.termsAccepted)

    fun getTransportTypesModels(transportTypes: List<TransportType>, prices: Map<String, String>?) =
        transportTypes.map {
            val id = it.id
            val imageRes = R.drawable::class.members.find( { it.name == "ic_transport_type_$id" } )
            val imageId = (imageRes?.call() as Int?) ?: R.drawable.ic_transport_type_unknown
            TransportTypeModel(id, getTransportTypeName(id), imageId, it.paxMax, it.luggageMax, prices?.get(id))
        }

    @StringRes
    fun getTransportTypeName(id: String): Int {
        val nameRes = R.string::class.members.find( { it.name == "transport_type_$id" } )
        return (nameRes?.call() as Int?) ?: R.string.transport_type_unknown
    }
    
    fun getCurrenciesModels(currencies: List<Currency>) = currencies.map { CurrencyModel(it) }
    fun getLocalesModels(locales: List<Locale>) = locales.map { LocaleModel(it) }
    fun getDistanceUnitsModels(distanceUnits: List<DistanceUnit>) = distanceUnits.map { DistanceUnitModel(it) }
    fun getEndpointsModels(endpoints: List<Endpoint>) = endpoints.map { EndpointModel(it) }
    
    fun getRouteModel(distance: Int?,
                      distanceUnit: DistanceUnit,
                      polyLines: List<String>?,
                      from: String,
                      to: String,
                      fromPoint: String,
                      toPoint: String,
                      dateTime: String): RouteModel{
        val dist = checkDistance(distance, fromPoint, toPoint)
        return RouteModel(dist, distanceUnit, polyLines, from, to, fromPoint, toPoint, dateTime)
    }
    
    fun getTransferModel(transfer: Transfer,
                         locale: Locale,
                         distanceUnit: DistanceUnit,
                         transportTypes: List<TransportType>): TransferModel {
        val selected = transportTypes.filter { transfer.transportTypeIds.contains(it.id) }

        val distance = checkDistance(transfer.distance, transfer.from.point, transfer.to!!.point)
        
        return TransferModel(transfer.id,
                      transfer.status,
                      transfer.from.name!!,
                      transfer.to!!.name!!,
                      //SimpleDateFormat(Utils.DATE_TIME_PATTERN, locale).format(transfer.dateToLocal),
                      Utils.getFormatedDate(locale, transfer.dateToLocal),
                      distance,
                      distanceUnit,
                      transfer.pax,
                      transfer.nameSign,
                      transfer.childSeats,
                      transfer.flightNumber,
                      transfer.comment,
                      selected.map { TransportTypeModel(it.id, getTransportTypeName(it.id), null, it.paxMax, it.luggageMax, null) },
                      transfer.paidSum.default,
                      transfer.paidPercentage,
                      transfer.remainsToPay.default,
                      transfer.price?.default,
                      transfer.relevantCarriersCount,
                      transfer.checkOffers)
    }
    
    fun getTransferNew(from: CityPoint,
                       to: CityPoint,
                       tripTo: Trip,
                       tripReturn: Trip?,
                       transportTypes: List<String>,
                       pax: Int,
                       childSeats: Int?,
                       passengerOfferedPrice: Int?,
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
                                                          passengerOfferedPrice,
                                                          comment,
                                                          user,
                                                          promoCode,
                                                          paypalOnly)

    fun getOfferModel(offer: Offer): OfferModel{
        var carrierRatings = RatingsModel(offer.carrier.ratings.average,
                offer.carrier.ratings.vehicle, offer.carrier.ratings.driver, offer.carrier.ratings.fair)

        return OfferModel(offer.id,
                offer.driver?.fullName,
                offer.driver?.email,
                offer.driver?.phone,
                offer.vehicle.transportTypeId,
                offer.vehicle.name,
                offer.vehicle.registrationNumber,
                offer.price.base.default,
                offer.price.base.preferred,
                offer.vehicle.paxMax,
                offer.vehicle.luggageMax,
                offer.vehicle.year,
                offer.carrier.id,
                offer.carrier.completedTransfers,
                offer.wifi,
                offer.refreshments,
                carrierRatings,
                offer.price.amount,
                offer.price.percentage30,
                offer.vehicle.photos,
                offer.carrier.languages,
                offer.vehicle.color)
    }

    fun getCarrierTripModel(carrierTrip: CarrierTrip,
                            locale: Locale,
                            distanceUnit: DistanceUnit): CarrierTripModel{

        val distance = checkDistance(carrierTrip.distance, carrierTrip.from.point, carrierTrip.to.point)

        return CarrierTripModel(carrierTrip.id,
                carrierTrip.transferId,
                carrierTrip.from.name,
                carrierTrip.to.name,
                //SimpleDateFormat(Utils.DATE_TIME_PATTERN, locale).format(carrierTrip.dateLocal),
                Utils.getFormatedDate(locale, carrierTrip.dateLocal),
                distance,
                distanceUnit,
                carrierTrip.childSeats,
                carrierTrip.comment,
                carrierTrip.price,
                carrierTrip.vehicle.name,
                carrierTrip.pax,
                carrierTrip.nameSign,
                carrierTrip.flightNumber,
                carrierTrip.remainToPay)
    }

    private fun checkDistance(distance: Int?, fromPoint: String, toPoint: String): Int{
        return distance ?: Utils.getDistanceBetweenTwoCoordinates(Utils.getPointsArrayFromString(fromPoint).map { it.toDouble() },
                Utils.getPointsArrayFromString(toPoint).map { it.toDouble() })
    }
}
