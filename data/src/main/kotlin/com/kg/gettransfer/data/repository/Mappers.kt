package com.kg.gettransfer.data.repository

import com.kg.gettransfer.data.model.*
import com.kg.gettransfer.domain.model.*

import java.text.SimpleDateFormat

import java.util.Locale

import timber.log.Timber

class Mappers {
    companion object {
        private val SERVER_DATE_FORMAT = SimpleDateFormat("yyyy/MM/dd", Locale.US)
        private val SERVER_TIME_FORMAT = SimpleDateFormat("HH:mm", Locale.US)
        /**
         * Simple mapper: [ApiAccount] -> [Account]
         */
        fun mapApiAccount(apiAccount: ApiAccount, configs: Configs): Account {
            return Account(apiAccount.email,
                           apiAccount.phone,
                           configs.availableLocales.find { it.language == apiAccount.locale }!!,
                           configs.supportedCurrencies.find { it.currencyCode == apiAccount.currency }!!,
                           apiAccount.distanceUnit,
                           apiAccount.fullName,
                           apiAccount.groups,
                           apiAccount.termsAccepted)
        }

        /**
         * [Account] -> [ApiAccount]
         */
        fun mapAccount(account: Account): ApiAccount {
            return ApiAccount(account.email,
                              account.phone,
                              account.locale?.language,
                              account.currency?.currencyCode,
                              account.distanceUnit,
                              account.fullName,
                              account.groups,
                              account.termsAccepted)
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

                
                
                
                
            
            /* Align to line:60 */
            return Transfer(apiTransfer.id!!,
                            apiTransfer.createdAt!!,
                            apiTransfer.duration,
                            apiTransfer.distance,
                            apiTransfer.status!!,
                            CityPoint(apiTransfer.from.name, apiTransfer.from.point, apiTransfer.from.placeId),
                            to,
                            apiTransfer.dateToLocal!!,
                            apiTransfer.dateReturnLocal,
                            apiTransfer.dateRefund,
                            
                            apiTransfer.nameSign,
                            apiTransfer.comment,
                            apiTransfer.malinaCard,
                            apiTransfer.flightNumber,
                            apiTransfer.flightNumberReturn,
                            apiTransfer.pax,
                            apiTransfer.childSeats,
                            apiTransfer.offersCount,
                            apiTransfer.relevantCarriersCount,
                            apiTransfer.offersUpdatedAt,
                            
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
                               promoCode: String?,
                               /* Not used now */
                               paypalOnly: Boolean): ApiTransferRequest {
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
    }
}
