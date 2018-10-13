package com.kg.gettransfer.presentation.model

import com.google.android.gms.maps.model.LatLng

import com.kg.gettransfer.R
import com.kg.gettransfer.domain.model.*
import com.kg.gettransfer.presentation.ui.Utils

import java.util.Currency
import java.util.Locale

object Mappers {
    fun point2LatLng(point: Point) = LatLng(point.latitude, point.longitude)
    fun latLng2Point(latLng: LatLng) = Point(latLng.latitude, latLng.longitude)
    
    fun getUserModel(account: Account) = UserModel(account.user.profile.name,
                                                   account.user.profile.email,
                                                   account.user.profile.phone,
                                                   account.user.termsAccepted)
    
    fun getProfile(userModel: UserModel) = Profile(userModel.name, userModel.email, userModel.phone)
    
    fun getUser(userModel: UserModel) = User(getProfile(userModel), userModel.termsAccepted)

    fun getTransportTypesModels(transportTypes: List<TransportType>, prices: Map<String, String>?) =
        transportTypes.map {
            val id = it.id
            val nameRes = R.string::class.members.find( { it.name == "transport_type_$id" } )
            val nameId = (nameRes?.call() as Int?) ?: R.string.transport_type_unknown
            val imageRes = R.drawable::class.members.find( { it.name == "ic_transport_type_$id" } )
            val imageId = (imageRes?.call() as Int?) ?: R.drawable.ic_transport_type_unknown
            TransportTypeModel(id, nameId, imageId, it.paxMax, it.luggageMax, prices?.get(id))
        }
    
    fun getCurrenciesModels(currencies: List<Currency>) = currencies.map { CurrencyModel(it) }
    fun getLocalesModels(locales: List<Locale>) = locales.map { LocaleModel(it) }
    fun getDistanceUnitsModels(distanceUnits: List<DistanceUnit>) = distanceUnits.map { DistanceUnitModel(it) }
    
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
                      transfer.from.name,
                      transfer.to!!.name,
                      //SimpleDateFormat(Utils.DATE_TIME_PATTERN, locale).format(transfer.dateToLocal),
                      Utils.getFormatedDate(locale, transfer.dateToLocal),
                      transfer.distance,
                      distanceUnit,
                      transfer.pax!!,
                      transfer.nameSign,
                      transfer.childSeats ?: 0,
                      transfer.flightNumber,
                      transfer.comment,
                      selected.map { TransportTypeModel(it.id, null, null, it.paxMax, it.luggageMax, null) },
                      transfer.paidSum!!.default,
                      transfer.paidPercentage!!,
                      transfer.remainsToPay!!.default,
                      transfer.price?.default,
                      transfer.relevantCarriersCount,
                      transfer.checkOffers)
    }
    
    fun getTransferNew(from: GTAddress,
                       to: GTAddress,
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
    

    fun getOfferModel(offer: Offer) = OfferModel(offer.id,
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
                                                 offer.carrier.ratings.average,
                                                 offer.price.amount,
                                                 offer.price.percentage30,
                                                 offer.vehicle.photos,
                                                 offer.carrier.languages,
                                                 offer.vehicle.color)

    fun getCarrierTripModel(carrierTrip: CarrierTrip, locale: Locale, distanceUnit: DistanceUnit) = 
        CarrierTripModel(carrierTrip.id,
                         carrierTrip.transferId,
                         carrierTrip.from.name,
                         carrierTrip.to.name,
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
