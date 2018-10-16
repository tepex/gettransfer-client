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
        val ISO_FORMAT = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US)
        private val POINT_REGEX = "\\(([\\d\\.\\-]+)\\,([\\d\\.\\-]+)\\)".toRegex()
        
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
            return Account(User(Profile(apiAccount.fullName, apiAccount.email, apiAccount.phone)),
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
            return ApiAccount(account.user.profile.email,
                              account.user.profile.phone,
                              account.locale?.language,
                              account.currency?.currencyCode,
                              account.distanceUnit?.name,
                              account.user.profile.name,
                              account.groups,
                              account.user.termsAccepted,
                              account.carrierId)
        }
        
        fun mapProfile(profile: Profile) = ApiProfile(profile.email, profile.phone, profile.name)        
        fun mapUser(user: User) = ApiUser(user.profile.email, user.profile.phone, user.profile.name, user.termsAccepted)
        /**
         * [ApiTransfer] -> [Transfer]
         */
         /*
        fun mapApiTransfer(apiTransfer: ApiTransfer): Transfer {
            var to: CityPoint? = null
            if(apiTransfer.to != null) to = mapApiCityPoint(apiTransfer.to!!)
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

            return Transfer(apiTransfer.id!!,
                            ISO_FORMAT.parse(apiTransfer.createdAt!!),
                            apiTransfer.duration,
                            apiTransfer.distance,
                            apiTransfer.status!!,
                            mapApiCityPoint(apiTransfer.from),
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
        */
	
        /**
         * [TransferNew] -> [ApiTransferRequest]
         */
         /*
        fun mapTransferRequest(transferNew: TransferNew): ApiTransferRequest {
            var apiTripReturn: ApiTrip? = null
            if(transferNew.tripReturn != null) apiTripReturn = mapTrip(transferNew.tripReturn!!)
            
            return ApiTransferRequest(mapCityPoint(transferNew.from),
                                      mapCityPoint(transferNew.to),
                                      mapTrip(transferNew.tripTo),
                                      apiTripReturn,
                                      transferNew.transportTypes,
                                      transferNew.pax,
                                      transferNew.childSeats,
                                      transferNew.passengerOfferedPrice?.toString(),
                                      transferNew.user.profile.name!!,
                                      transferNew.comment,
                                      mapUser(transferNew.user),
                                      transferNew.promoCode)
        }
        */
        
        /**
         * [GTAddress] -> [ApiCityPoint]
         */
        /*
        fun mapAddress(address: GTAddress): ApiCityPoint {
            Timber.d("address.name: %s", address.name)
            Timber.d("address.point: %s", address.point)
            Timber.d("address.id: %s", address.id)
            return ApiCityPoint(address.name, address.point!!.toString(), address.id)
        }
        */
        fun mapCityPoint(cityPoint: CityPoint) = ApiCityPoint(cityPoint.name!!, cityPoint.point!!.toString(), cityPoint.placeId)
        
        /**
         * [ApiCityPont] -> [CityPoint]
         */
        fun mapApiCityPoint(apiCityPoint: ApiCityPoint): CityPoint {
            val latLng = POINT_REGEX.find(apiCityPoint.point)!!.groupValues
            val point = Point(latLng.get(1).toDouble(), latLng.get(2).toDouble())
            return CityPoint(apiCityPoint.name, point, apiCityPoint.placeId)
        }
        
        /**
         * [ApiCityPoint] -> [GTAddress]
         */
         /*
        fun mapApiCityPoint(apiCityPoint: ApiCityPoint): GTAddress {
            val latLng = POINT_REGEX.find(apiCityPoint.point)!!.groupValues
            val point = Point(latLng.get(1).toDouble(), latLng.get(2).toDouble())
            val cityPoint = CityPoint(apiCityPoint.name, point, apiCityPoint.placeId) 
            return GTAddress(cityPoint, null, null, null, null)
        }
        
        
        fun mapTrip(trip: Trip): ApiTrip {
            return ApiTrip(SERVER_DATE_FORMAT.format(trip.dateTime), SERVER_TIME_FORMAT.format(trip.dateTime), trip.flightNumber)
        }
        */
        
        fun mapApiRouteInfo(apiRouteInfo: ApiRouteInfo): RouteInfo {
            var polylines: List<String>? = null
            var overviewPolylines: String? = null
            if(apiRouteInfo.routes != null) {
                polylines = apiRouteInfo.routes!!.first().legs.first().steps.map { it.polyline.points }
                overviewPolylines = apiRouteInfo.routes!!.first().overviewPolyline.points
            }
            return RouteInfo(apiRouteInfo.success,
                             apiRouteInfo.distance,
                             apiRouteInfo.duration,
                             apiRouteInfo.prices?.map { TransportTypePrice(it.key, it.value.minFloat, it.value.min, it.value.max) },
                             apiRouteInfo.watertaxi,
                             polylines,
                             overviewPolylines)
        }

        /**
         * [ApiCarrierTrip] -> [CarrierTrip]
         */
        fun mapApiCarrierTrip(apiCarrierTrip: ApiCarrierTrip): CarrierTrip {
            var passengerAccount: PassengerAccount? = null
            apiCarrierTrip.passengerAccount?.let { passengerAccount =
                PassengerAccount(Profile(it.fullName, it.email, it.phone), ISO_FORMAT.parse(it.lastSeen))
            }
            return CarrierTrip(apiCarrierTrip.id,
                               apiCarrierTrip.transferId,
                               mapApiCityPoint(apiCarrierTrip.from),
                               mapApiCityPoint(apiCarrierTrip.to),
                               ISO_FORMAT.parse(apiCarrierTrip.dateLocal),
                               apiCarrierTrip.duration,
                               apiCarrierTrip.distance,
                               apiCarrierTrip.time,
                               apiCarrierTrip.childSeats,
                               apiCarrierTrip.comment,
                               apiCarrierTrip.waterTaxi,
                               apiCarrierTrip.price,
                               VehicleBase(apiCarrierTrip.vehicle.name, apiCarrierTrip.vehicle.registrationNumber),
                               apiCarrierTrip.pax,
                               apiCarrierTrip.nameSign,
                               apiCarrierTrip.flightNumber,
                               apiCarrierTrip.paidSum,
                               apiCarrierTrip.remainToPay,
                               apiCarrierTrip.paidPercentage,
                               passengerAccount)
        }

        fun mapPaymentResult(payment: ApiPaymentResult): Payment {
            return Payment(payment.type, payment.url)
        }
    }
}
