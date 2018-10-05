package com.kg.gettransfer.data.repository

import com.kg.gettransfer.data.model.*
import com.kg.gettransfer.domain.model.*

import java.text.SimpleDateFormat

import java.util.Currency
import java.util.Date
import java.util.Locale

import timber.log.Timber

class Mappers {
    companion object {
        private val SERVER_DATE_FORMAT = SimpleDateFormat("yyyy/MM/dd", Locale.US)
        private val SERVER_TIME_FORMAT = SimpleDateFormat("HH:mm", Locale.US)
        private val ISO_FORMAT = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US)
        
        fun mapApiConfigs(apiConfigs: ApiConfigs): Configs {
            val locales = apiConfigs.availableLocales.map { Locale(it.code) }
            return Configs(apiConfigs.transportTypes.map { TransportType(it.id, it.paxMax, it.luggageMax) },
		                   PaypalCredentials(apiConfigs.paypalCredentials.id, apiConfigs.paypalCredentials.env),
		                   locales,
		                   locales.find { it.language == apiConfigs.preferredLocale }!!,
		                   apiConfigs.supportedCurrencies.map { Currency.getInstance(it.code)!! },
		                   apiConfigs.supportedDistanceUnits.map { DistanceUnit.parse(it) },
		                   CardGateways(apiConfigs.cardGateways.default, apiConfigs.cardGateways.countryCode),
		                   apiConfigs.officePhone,
		                   apiConfigs.baseUrl)
		}
        
        /**
         * Simple mapper: [ApiAccount] -> [Account]
         */
        fun mapApiAccount(apiAccount: ApiAccount, configs: Configs): Account {
            return Account(User(apiAccount.email, apiAccount.phone, apiAccount.fullName),
                           configs.availableLocales.find { it.language == apiAccount.locale }!!,
                           configs.supportedCurrencies.find { it.currencyCode == apiAccount.currency }!!,
                           DistanceUnit.parse(apiAccount.distanceUnit),
                           apiAccount.groups,
                           apiAccount.carrierId)
        }

        /**
         * [Account] -> [ApiAccount]
         */
        fun mapAccount(account: Account): ApiAccount {
            return ApiAccount(account.user.email,
                              account.user.phone,
                              account.locale?.language,
                              account.currency?.currencyCode,
                              account.distanceUnit?.name,
                              account.user.fullName,
                              account.groups,
                              account.user.termsAccepted,
                              null)
        }
        
        /**
         * [ApiTransfer] -> [Transfer]
         */
        fun mapApiTransfer(apiTransfer: ApiTransfer): Transfer {
            var to: CityPoint? = null
            if(apiTransfer.to != null) to = CityPoint(apiTransfer.to!!.name, apiTransfer.to!!.point, apiTransfer.to!!.placeId)
            var paidSum: Money? = null
            if(apiTransfer.paidSum != null) paidSum = Money(apiTransfer.paidSum!!.default, apiTransfer.paidSum!!.preferred)
            var remainsToPay: Money? = null
            if(apiTransfer.remainsToPay != null) remainsToPay = Money(apiTransfer.remainsToPay!!.default, apiTransfer.remainsToPay!!.preferred)
            var price: Money? = null
            if(apiTransfer.price != null) price = Money(apiTransfer.price!!.default, apiTransfer.price!!.preferred)
            var dateReturnLocal: Date? = null
            if(apiTransfer.dateReturnLocal != null) dateReturnLocal = ISO_FORMAT.parse(apiTransfer.dateReturnLocal)
            var dateRefund: Date? = null
            if(apiTransfer.dateRefund != null) dateRefund = ISO_FORMAT.parse(apiTransfer.dateRefund) 
            var offersUpdatedAt: Date? = null
            if(apiTransfer.offersUpdatedAt != null) offersUpdatedAt = ISO_FORMAT.parse(apiTransfer.offersUpdatedAt)
            /* Align to line:80 */
            return Transfer(apiTransfer.id!!,
                            ISO_FORMAT.parse(apiTransfer.createdAt!!),
                            apiTransfer.duration,
                            apiTransfer.distance,
                            apiTransfer.status!!,
                            CityPoint(apiTransfer.from.name, apiTransfer.from.point, apiTransfer.from.placeId),
                            to,
                            ISO_FORMAT.parse(apiTransfer.dateToLocal!!),
                            dateReturnLocal,
                            dateRefund,
                            
                            apiTransfer.nameSign,
                            apiTransfer.comment,
                            apiTransfer.malinaCard,
                            apiTransfer.flightNumber,
                            apiTransfer.flightNumberReturn,
                            apiTransfer.pax,
                            apiTransfer.childSeats,
                            apiTransfer.offersCount,
                            apiTransfer.relevantCarriersCount,
                            offersUpdatedAt,
                            
                            apiTransfer.time,
                            paidSum,
                            remainsToPay,
                            apiTransfer.paidPercentage,
                            apiTransfer.pendingPaymentId,
                            apiTransfer.bookNow!!,
                            apiTransfer.bookNowExpiration,
                            apiTransfer.transportTypeIds,
                            apiTransfer.passengerOfferedPrice,
                            price,
                            
                            apiTransfer.editableFields!!)
        }
	
        /**
         * [TransferRequest] -> [ApiTransferRequest]
         */
        fun mapTransferRequest(from: GTAddress,
                               to: GTAddress,
                               tripTo: Trip,
                               tripReturn: Trip?,
                               transportTypes: List<String>,
                               pax: Int,
                               childSeats: Int?,
                               passengerOfferedPrice: Int?,
                               nameSign: String,
                               comment: String?,
                               account: Account,
                               promoCode: String?/*,
                                Not used now 
                               paypalOnly: Boolean*/): ApiTransferRequest {
            var apiTripReturn: ApiTrip? = null
            if(tripReturn != null) apiTripReturn = mapTrip(tripReturn)
            
            return ApiTransferRequest(mapAddress(from), mapAddress(to), mapTrip(tripTo), apiTripReturn,
                transportTypes, pax, childSeats, passengerOfferedPrice?.toString(), nameSign, comment, mapAccount(account),
                promoCode)
        }
        
        /**
         * [GTAddress] -> [ApiCityPoint]
         */
        fun mapAddress(address: GTAddress): ApiCityPoint {
            Timber.d("address.name: %s", address.name)
            Timber.d("address.point: %s", address.point)
            Timber.d("address.id: %s", address.id)
            return ApiCityPoint(address.name, address.point!!.toString(), address.id)
        }
        
        fun mapTrip(trip: Trip): ApiTrip {
            return ApiTrip(SERVER_DATE_FORMAT.format(trip.dateTime), SERVER_TIME_FORMAT.format(trip.dateTime), trip.flightNumber)
        }
        
        fun mapApiRouteInfo(apiRouteInfo: ApiRouteInfo): RouteInfo {
            return RouteInfo(apiRouteInfo.success,
                             apiRouteInfo.distance,
                             apiRouteInfo.duration,
                             apiRouteInfo.prices?.map { TransportTypePrice(it.key, it.value.minFloat, it.value.min, it.value.max) },
                             apiRouteInfo.watertaxi,
                             apiRouteInfo.routes.first().legs.first().steps.map { it.polyline.points },
                             apiRouteInfo.routes.first().overviewPolyline.points)
        }

        /**
         * [ApiCarrierTrip] -> [CarrierTrip]
         */
        fun mapApiCarrierTrip(apiCarrierTrip: ApiCarrierTrip): CarrierTrip {
            var passengerAccount: PassengerAccount? = null
            apiCarrierTrip.passengerAccount?.let { passengerAccount =
                PassengerAccount(User(it.email, it.phone, it.fullName), ISO_FORMAT.parse(it.lastSeen))
            }
            return CarrierTrip(apiCarrierTrip.id,
                               apiCarrierTrip.transferId,
                               CityPoint(apiCarrierTrip.from.name, apiCarrierTrip.from.point, apiCarrierTrip.from.placeId),
                               CityPoint(apiCarrierTrip.to.name, apiCarrierTrip.to.point, apiCarrierTrip.to.placeId),
                               ISO_FORMAT.parse(apiCarrierTrip.dateLocal),
                               apiCarrierTrip.duration,
                               apiCarrierTrip.distance,
                               apiCarrierTrip.time,
                               apiCarrierTrip.childSeats,
                               apiCarrierTrip.comment,
                               apiCarrierTrip.waterTaxi,
                               apiCarrierTrip.price,
                               CarrierTripVehicle(apiCarrierTrip.vehicle.name, apiCarrierTrip.vehicle.registrationNumber),
                               apiCarrierTrip.pax,
                               apiCarrierTrip.nameSign,
                               apiCarrierTrip.flightNumber,
                               apiCarrierTrip.paidSum,
                               apiCarrierTrip.remainToPay,
                               apiCarrierTrip.paidPercentage,
                               passengerAccount)
        }
    }
}
