package com.kg.gettransfer.data.repository

import android.content.Context

import com.kg.gettransfer.data.Api
import com.kg.gettransfer.data.TransportTypesDeserializer
import com.kg.gettransfer.data.model.*

import com.kg.gettransfer.domain.ApiException
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

import timber.log.Timber

class ApiRepositoryImpl(private val context: Context, url: String, private val apiKey: String): ApiRepository {
    private var cacheRepository = CacheRepositoryImpl(context)
    private var api: Api
    private val gson = GsonBuilder().registerTypeAdapter(ApiTransportTypesWrapper::class.java, TransportTypesDeserializer()).create()
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
		configs = Mappers.mapApiConfigs(response.data!!)
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
            val ae = apiException(e)
            if(!ae.isInvalidToken()) throw ae

            try { updateAccessToken() } catch(e1: Exception) { throw apiException(e1) }
            return try { api.putAccount(apiAccount).await() } catch(e2: Exception) { throw apiException(e2) }
        }
    }

    /* Not used now.
    override suspend fun createAccount(account: Account) {
    }
    */
    
    override suspend fun login(email: String, password: String): Account {
        val response: ApiResponse<ApiAccountWrapper> = tryLogin(email, password)
        val account = Mappers.mapApiAccount(response.data!!.account, configs!!)
        cacheRepository.account = account
        return account
    }
    
    private suspend fun tryLogin(email: String, password: String): ApiResponse<ApiAccountWrapper> {
        return try { api.login(email, password).await() }
        catch(e: Exception) {
            if(e is ApiException) throw e /* second invocation */
            val ae = apiException(e)
            if(!ae.isInvalidToken()) throw ae

            try { updateAccessToken() } catch(e1: Exception) { throw apiException(e1) }
            return try { api.login(email, password).await() } catch(e2: Exception) { throw apiException(e2) }
        }
    }
    
    override fun logout() {
        cacheRepository.accessToken = CacheRepositoryImpl.INVALID_TOKEN
        cacheRepository.cleanAccount()
    }
    
    override suspend fun getRouteInfo(points: Array<String>, withPrices: Boolean, returnWay: Boolean): RouteInfo {
        val response: ApiResponse<ApiRouteInfo> = tryGetRouteInfo(points, withPrices, returnWay)
        return Mappers.mapApiRouteInfo(response.data!!)
    }
    
    private suspend fun tryGetRouteInfo(points: Array<String>, withPrices: Boolean, returnWay: Boolean):
        ApiResponse<ApiRouteInfo> {
        return try { api.getRouteInfo(points, withPrices, returnWay).await() }
        catch(e: Exception) {
            if(e is ApiException) throw e /* second invocation */
            val ae = apiException(e)
            if(!ae.isInvalidToken()) throw ae

            try { updateAccessToken() } catch(e1: Exception) { throw apiException(e1) }
            return try { api.getRouteInfo(points, withPrices, returnWay).await() } catch(e2: Exception) { throw apiException(e2) }
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
            val ae = apiException(e)
            if(!ae.isInvalidToken()) throw ae

            try { updateAccessToken() } catch(e1: Exception) { throw apiException(e1) }
            return try { apiCall().await() } catch(e2: Exception) { throw apiException(e2) }
        }
    }
    
    private suspend fun <R> tryTwice(id: Long, apiCall: (Long) -> Deferred<R>): R {
        return try { apiCall(id).await() }
        catch(e: Exception) {
            if(e is ApiException) throw e /* second invocation */
            val ae = apiException(e)
            if(!ae.isInvalidToken()) throw ae

            try { updateAccessToken() } catch(e1: Exception) { throw apiException(e1) }
            return try { apiCall(id).await() } catch(e2: Exception) { throw apiException(e2) }
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
            account, promoCode/*, paypalOnly*/)))
        
        return Mappers.mapApiTransfer(response.data?.transfer!!)
    }

    override suspend fun cancelTransfer(idTransfer: Long, reason: String): Transfer {
        val response: ApiResponse<ApiTransferWrapper> = tryTwice(idTransfer) { id -> api.cancelTransfer(id, ApiReason(reason)) }
        return Mappers.mapApiTransfer(response.data?.transfer!!)
    }
    
    private suspend fun tryPostTransfer(apiTransfer: ApiTransferWrapper): ApiResponse<ApiTransferWrapper> {
        return try { api.postTransfer(apiTransfer).await() }
        catch(e: Exception) {
            if(e is ApiException) throw e /* second invocation */
            val ae = apiException(e)
            if(!ae.isInvalidToken()) throw ae

            try { updateAccessToken() } catch(e1: Exception) { throw apiException(e1) }
            return try { api.postTransfer(apiTransfer).await() } catch(e2: Exception) { throw apiException(e2) }
        }
    }

	override suspend fun getAllTransfers(): List<Transfer> {
		val response: ApiResponse<ApiTransfers> = tryTwice { api.getAllTransfers() }
		val transfers: List<ApiTransfer> = response.data!!.transfers
		return transfers.map {transfer -> Mappers.mapApiTransfer(transfer) }
	}

	override suspend fun getTransfer(idTransfer: Long): Transfer {
		val response: ApiResponse<ApiTransferWrapper> = tryTwice(idTransfer, { id -> api.getTransfer(id) })
		val transfer: ApiTransfer = response.data!!.transfer
		return Mappers.mapApiTransfer(transfer)
	}
	
    override suspend fun getTransfersArchive(): List<Transfer> {
        val response: ApiResponse<ApiTransfers> = tryTwice { api.getTransfersArchive() }
        val transfers: List<ApiTransfer> = response.data!!.transfers
        return transfers.map {transfer -> Mappers.mapApiTransfer(transfer) }
    }

    override suspend fun getTransfersActive(): List<Transfer> {
        val response: ApiResponse<ApiTransfers> = tryTwice { api.getTransfersActive() }
        val transfers: List<ApiTransfer> = response.data!!.transfers
        return transfers.map {transfer -> Mappers.mapApiTransfer(transfer) }
    }

    override suspend fun getOffers(idTransfer: Long): List<Offer> {
        val response: ApiResponse<ApiOffers> = tryTwice(idTransfer, { id -> api.getOffers(id) })
        val transfers: List<ApiOffer> = response.data!!.offers
        return transfers.map {offer -> setOfferData(offer) }
    }

    fun setOfferData(offer: ApiOffer): Offer {
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
    
    private fun apiException(e: Exception): ApiException {
        if(e is HttpException)
            return ApiException(e.code(), gson.fromJson(e.response().errorBody()?.string(), ApiResponse::class.java).
                error?.details?.toString() ?: e.message!!)
        else return ApiException(ApiException.NOT_HTTP, e.message!!)
    }
}
