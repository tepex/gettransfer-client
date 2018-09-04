package com.kg.gettransfer.data.repository

import android.content.Context

import com.kg.gettransfer.data.Api
import com.kg.gettransfer.data.TransportTypesDeserializer

import com.kg.gettransfer.data.model.*
import com.kg.gettransfer.domain.model.*

import com.kg.gettransfer.domain.repository.ApiRepository

import com.google.gson.GsonBuilder

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.experimental.CoroutineCallAdapterFactory

import java.util.Currency
import java.util.Locale

import kotlinx.coroutines.experimental.*

import okhttp3.CookieJar
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor

import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ApiRepositoryImpl(private val context: Context, url: String, private val apiKey: String): ApiRepository {
    private var cacheRepository = CacheRepositoryImpl(context)
    private var api: Api
    private var configs: Configs? = null
    
	/**
	 * @throws ApiException
	 */
    init {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        val builder = OkHttpClient.Builder()
        builder.addInterceptor(loggingInterceptor)
        builder.addInterceptor { chain ->
            var request = chain.request()
            if(request.url().encodedPath() != Api.API_ACCESS_TOKEN) request = request.newBuilder()
	                .addHeader(Api.HEADER_TOKEN, cacheRepository.accessToken)
	                .build()
		    chain.proceed(request)
		}
		
		builder.cookieJar(CookieJar.NO_COOKIES)
 
		val gson = GsonBuilder()
			.registerTypeAdapter(ApiTransportTypesWrapper::class.java, TransportTypesDeserializer())
			.create()

	    api = Retrofit.Builder()
		        .baseUrl(url)
		        .client(builder.build())
		        .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(CoroutineCallAdapterFactory()) // https://github.com/JakeWharton/retrofit2-kotlin-coroutines-adapter
                .build()
                .create(Api::class.java)
    }
	
	/**
	 * @throws ApiException
	 */
	override suspend fun getConfigs(): Configs {
		if(configs != null) return configs!!
	    
		val response: ApiResponse<ApiConfigs> = tryTwice { api.getConfigs() }
		val data: ApiConfigs = response.data!!
		
		val locales = data.availableLocales.map { Locale(it.code) }
		configs = Configs(data.transportTypes.map { TransportType(it.id, it.paxMax, it.luggageMax) },
		        PaypalCredentials(data.paypalCredentials.id, data.paypalCredentials.env),
		        locales,
                locales.find { it.language == data.preferredLocale }!!,
                data.supportedCurrencies.map { Currency.getInstance(it.code)!! },
                data.supportedDistanceUnits,
                CardGateways(data.cardGateways.default, data.cardGateways.countryCode),
                data.officePhone,
                data.baseUrl)
        
        getAccount(true)
        return configs!!
    }
    
    override suspend fun getAccount(request: Boolean): Account {
        var account = cacheRepository.account
        if(request) {
            val response: ApiResponse<ApiAccountWrapper> = tryTwice { api.getAccount() }
            if(response.data?.account != null) account = Mappers.mapApiAccount(response.data?.account!!, configs!!)
            cacheRepository.account = account
        }
        return account
    }

    override suspend fun putAccount(account: Account) {
        cacheRepository.account = account
        tryPutAccount(Mappers.mapAccount(account))
    }
    
    private suspend fun tryPutAccount(apiAccount: ApiAccount): ApiResponse<ApiAccountWrapper> {
        return try { api.putAccount(apiAccount).await() }
        catch(e: Exception) {
            if(e is ApiException) throw e /* second invocation */
            val ae = ApiException(e)
            if(!ae.isInvalidToken()) throw ae

            try { updateAccessToken() } catch(e1: Exception) { throw ApiException(e1) }
            return try { api.putAccount(apiAccount).await() } catch(e2: Exception) { throw ApiException(e2) }
        }
    }

    /* Not used now.
    override suspend fun createAccount(account: Account) {
    }
    */
    
    override suspend fun login(email: String, password: String): Account {
        val response: ApiResponse<ApiAccountWrapper> = try {
            api.login(email, password).await()
        } catch(httpException: HttpException) {
            throw httpException
        }
        val account = Mappers.mapApiAccount(response.data!!.account, configs!!)
        //cacheRepository.saveAccount(account)
        return account
    }
    
    override fun logout() {
        cacheRepository.accessToken = CacheRepositoryImpl.INVALID_TOKEN
    }
    
    override suspend fun getRouteInfo(points: Array<String>, withPrices: Boolean, returnWay: Boolean): RouteInfo {
        val response: ApiResponse<ApiRouteInfo> = tryGetRouteInfo(points, withPrices, returnWay)
        val apiRouteInfo: ApiRouteInfo = response.data!!
        return RouteInfo(apiRouteInfo.success, apiRouteInfo.distance, apiRouteInfo.duration,
				apiRouteInfo.prices?.map { TransportTypePrice(it.key, it.value.minFloat, it.value.min, it.value.max) },
				apiRouteInfo.watertaxi, apiRouteInfo.routes.get(0).legs.get(0).steps.map { it.polyline.points },
				apiRouteInfo.routes.get(0).overviewPolyline.points)
    }
    
    private suspend fun tryGetRouteInfo(points: Array<String>, withPrices: Boolean, returnWay: Boolean):
        ApiResponse<ApiRouteInfo> {
        return try { api.getRouteInfo(points, withPrices, returnWay).await() }
        catch(e: Exception) {
            if(e is ApiException) throw e /* second invocation */
            val ae = ApiException(e)
            if(!ae.isInvalidToken()) throw ae

            try { updateAccessToken() } catch(e1: Exception) { throw ApiException(e1) }
            return try { api.getRouteInfo(points, withPrices, returnWay).await() } catch(e2: Exception) { throw ApiException(e2) }
        }
    }

    /**
     * 1. Try to call [apiCall] first time.
     * 2. If response code is 401 (token expired) — try to call [apiCall] second time.
     */
    private suspend fun <R> tryTwice(apiCall: () -> Deferred<R>): R {
        return try { apiCall().await() }
        catch(e: Exception) {
            if(e is ApiException) throw e /* second invocation */
            val ae = ApiException(e)
            if(!ae.isInvalidToken()) throw ae

            try { updateAccessToken() } catch(e1: Exception) { throw ApiException(e1) }
            return try { apiCall().await() } catch(e2: Exception) { throw ApiException(e2) }
        }
    }
    /*
    private suspend fun <T, R> tryTwice(vararg param: T, apiCall: (T) -> Deferred<R>): R {
        return apiCall(param).await()
    }
    */

    private suspend fun updateAccessToken() {
        val response: ApiResponse<ApiToken> = api.accessToken(apiKey).await()
        cacheRepository.accessToken = response.data!!.token
    }

    override suspend fun createTransfer(from: GTAddress,
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
                                        paypalOnly: Boolean): Transfer {
        val response: ApiResponse<ApiTransferWrapper> = tryPostTransfer(ApiTransferWrapper(Mappers.mapTransferRequest(
            from, to, tripTo, tripReturn, transportTypes, pax, childSeats, passengerOfferedPrice, nameSign, comment,
            account, promoCode, paypalOnly)))
        
        return Mappers.mapApiTransfer(response.data?.transfer!!)
    }
    
    private suspend fun tryPostTransfer(apiTransfer: ApiTransferWrapper): ApiResponse<ApiTransferWrapper> {
        return try { api.postTransfer(apiTransfer).await() }
        catch(e: Exception) {
            if(e is ApiException) throw e /* second invocation */
            val ae = ApiException(e)
            if(!ae.isInvalidToken()) throw ae

            try { updateAccessToken() } catch(e1: Exception) { throw ApiException(e1) }
            return try { api.postTransfer(apiTransfer).await() } catch(e2: Exception) { throw ApiException(e2) }
        }
    }

	override suspend fun getAllTransfers(): List<Transfer> {
		val response: ApiResponse<ApiTransfers> = try {
			api.getAllTransfers().await()
		} catch (httpException: HttpException) {
			throw httpException
		}

		val transfers: List<ApiTransfer> = response.data!!.transfers
		return transfers.map {transfer -> Mappers.mapApiTransfer(transfer) }
	}

	override suspend fun getTransfer(idTransfer: Long): Transfer {
		val response: ApiResponse<ApiTransferWrapper> = try {
			api.getTransfer(idTransfer).await()
		} catch (httpException: HttpException) {
			throw httpException
		}

		val transfer: ApiTransfer = response.data!!.transfer
		return setTransferData(transfer)
	}

    override suspend fun getTransfersArchive(): List<Transfer> {
        val response: ApiResponse<ApiTransfers> = try {
            api.getTransfersArchive().await()
        } catch (httpException: HttpException) {
            throw httpException
        }

        val transfers: List<ApiTransfer> = response.data!!.transfers
        return transfers.map {transfer -> setTransferData(transfer) }
    }

    override suspend fun getTransfersActive(): List<Transfer> {
        val response: ApiResponse<ApiTransfers> = try {
            api.getTransfersActive().await()
        } catch (httpException: HttpException) {
            throw httpException
        }

        val transfers: List<ApiTransfer> = response.data!!.transfers
        return transfers.map {transfer -> setTransferData(transfer) }
    }

	fun setTransferData(transfer: ApiTransfer): Transfer{
        val from = CityPoint(transfer.from.name, transfer.from.point, transfer.from.placeId)

        val to = if (transfer.to != null) CityPoint(transfer.to?.name, transfer.to?.point, transfer.to?.placeId)
                 else null

        val paidSum = Money(transfer.paidSum.default, transfer.paidSum.preferred)

        val remainsToPay = Money(transfer.remainsToPay.default, transfer.remainsToPay.preferred)

        val price = if(transfer.price != null) Money(transfer.price?.default, transfer.price?.preferred)
                    else null

		return Transfer(transfer.id, transfer.createdAt, transfer.duration, transfer.distance, transfer.status,
				from, to, transfer.dateToLocal, transfer.dateReturnLocal, transfer.dateRefund, transfer.nameSign,
                transfer.comment, transfer.malinaCard, transfer.flightNumber, transfer.flightNumberReturn, transfer.pax,
				transfer.childSeats, transfer.offersCount, transfer.relevantCarriersCount, transfer.offersUpdatedAt,
				transfer.time, paidSum, remainsToPay, transfer.paidPercentage, transfer.pendingPaymentId,
                transfer.bookNow, transfer.bookNowExpiration, transfer.transportTypeIds, transfer.passengerOfferedPrice,
                price, transfer.editableFields)
//		return Mappers.mapApiTransfer(transfer)
	}

    override suspend fun getOffers(idTransfer: Long): List<Offer> {
        val response: ApiResponse<ApiOffers> = try {
            api.getOffers(idTransfer).await()
        } catch (httpException: HttpException) {
            throw httpException
        }

        val transfers: List<ApiOffer> = response.data!!.offers
        return transfers.map {offer -> setOfferData(offer) }
    }

    fun setOfferData(offer: ApiOffer): Offer{
        val price = Price(Money(offer.price.base.default, offer.price.base.preferred), offer.price.percentage30,
                offer.price.percentage70, offer.price.amount)

        val ratings = if(offer.ratings != null) Ratings(offer.ratings?.average, offer.ratings?.vehicle,
                offer.ratings?.driver, offer.ratings?.fair)
                      else null

        val carrierLanguages = offer.carrier.languages.map { Locale(it.code) }
        val carrierRatings = Ratings(offer.carrier.ratings.average, offer.carrier.ratings.vehicle,
                offer.carrier.ratings.driver, offer.carrier.ratings.fair)
        val carrier = Carrier(offer.carrier.title, offer.carrier.email, offer.carrier.phone, offer.carrier.id,
                offer.carrier.approved, offer.carrier.completedTransfers, carrierLanguages, carrierRatings, offer.carrier.canUpdateOffers)

        val vehicle = Vehicle(offer.vehicle.name, offer.vehicle.registrationNumber, offer.vehicle.year, offer.vehicle.color,
                offer.vehicle.transportTypeId, offer.vehicle.paxMax, offer.vehicle.luggageMax, offer.vehicle.photos)

        val driver = if(offer.driver != null) Driver(offer.driver!!.fullName, offer.driver!!.phone)
                     else null

        return Offer(offer.id, offer.status, offer.wifi, offer.refreshments, offer.createdAt,
                price, ratings, offer.passengerFeedback, carrier, vehicle, driver)
    }
}
