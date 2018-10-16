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
                      fromPoint: Point,
                      toPoint: Point,
                      dateTime: String) = RouteModel(distance,
                                                     distanceUnit,
                                                     polyLines,
                                                     from.toString(),
                                                     to.toString(),
                                                     fromPoint.toString(),
                                                     toPoint.toString(),
                                                     dateTime)
    
    fun getTransferModel(transfer: Transfer,
                         locale: Locale,
                         distanceUnit: DistanceUnit,
                         transportTypes: List<TransportType>): TransferModel {
        val selected = transportTypes.filter { transfer.transportTypeIds.contains(it.id) }
        return TransferModel(transfer.id,
                      transfer.status,
                      transfer.from.name!!,
                      transfer.to!!.name!!,
                      //SimpleDateFormat(Utils.DATE_TIME_PATTERN, locale).format(transfer.dateToLocal),
                      Utils.getFormatedDate(locale, transfer.dateToLocal),
                      transfer.distance,
                      distanceUnit,
                      transfer.pax!!,
                      transfer.nameSign,
                      transfer.childSeats ?: 0,
                      transfer.flightNumber,
                      transfer.comment,
                      selected.map { TransportTypeModel(it.id, getTransportTypeName(it.id), null, it.paxMax, it.luggageMax, null) },
                      transfer.paidSum!!.default,
                      transfer.paidPercentage!!,
                      transfer.remainsToPay!!.default,
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

    fun getOfferModel(offer: Offer): OfferModel { 
        return OfferModel(offer.id,
                   ProfileModel(offer.driver?.name, offer.driver?.email, offer.driver?.phone),
                   VehicleModel(VehicleBaseModel(offer.vehicle.vehicleBase.name, offer.vehicle.vehicleBase.registrationNumber),
                                offer.vehicle.year,
                                offer.vehicle.color,
                                TransportTypeModel(offer.vehicle.transportType.id,
                                                   getTransportTypeName(offer.vehicle.transportType.id),
                                                   null,
                                                   offer.vehicle.transportType.paxMax,
                                                   offer.vehicle.transportType.luggageMax,
                                                   null),
                                offer.vehicle.photos),
                   offer.price.base.default,
                   offer.price.base.preferred,
                   offer.carrier.id,
                   offer.carrier.completedTransfers,
                   offer.wifi,
                   offer.refreshments,
                   offer.carrier.ratings.average,
                   offer.price.amount,
                   offer.price.percentage30,
                   offer.carrier.languages)
    }

    fun getCarrierTripModel(carrierTrip: CarrierTrip, locale: Locale, distanceUnit: DistanceUnit) = 
        CarrierTripModel(carrierTrip.id,
                         carrierTrip.transferId,
                         carrierTrip.from.name!!,
                         carrierTrip.to.name!!,
                         //SimpleDateFormat(Utils.DATE_TIME_PATTERN, locale).format(carrierTrip.dateLocal),
                         Utils.getFormatedDate(locale, carrierTrip.dateLocal),
                         carrierTrip.distance,
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
