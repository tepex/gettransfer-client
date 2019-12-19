package com.kg.gettransfer.data.repository

import com.kg.gettransfer.data.PaymentDataStore
import com.kg.gettransfer.data.RemoteException
import com.kg.gettransfer.data.ds.DataStoreFactory
import com.kg.gettransfer.data.ds.PaymentDataStoreCache
import com.kg.gettransfer.data.ds.PaymentDataStoreRemote
import com.kg.gettransfer.data.model.map

import com.kg.gettransfer.domain.interactor.PaymentInteractor
import com.kg.gettransfer.domain.model.Transfer
import com.kg.gettransfer.domain.model.OfferItem
import com.kg.gettransfer.domain.model.PaymentRequest
import com.kg.gettransfer.domain.model.PlatronPayment
import com.kg.gettransfer.domain.model.CheckoutcomPayment
import com.kg.gettransfer.domain.model.BraintreePayment
import com.kg.gettransfer.domain.model.GooglePayPayment
import com.kg.gettransfer.domain.model.PaymentProcessRequest
import com.kg.gettransfer.domain.model.PaymentProcess
import com.kg.gettransfer.domain.model.PaymentStatusRequest
import com.kg.gettransfer.domain.model.PaymentStatus
import com.kg.gettransfer.domain.model.BraintreeToken
import com.kg.gettransfer.domain.model.Result
import com.kg.gettransfer.domain.model.CheckoutcomTokenRequest
import com.kg.gettransfer.domain.model.CheckoutcomToken
import com.kg.gettransfer.domain.repository.PaymentRepository

import org.koin.core.inject

class PaymentRepositoryImpl(
    private val factory: DataStoreFactory<PaymentDataStore, PaymentDataStoreCache, PaymentDataStoreRemote>
) : BaseRepository(), PaymentRepository {

    private val paymentReceiver: PaymentInteractor by inject()

    override var selectedTransfer: Transfer? = null
    override var selectedOffer: OfferItem? = null

    override suspend fun getPlatronPayment(paymentRequest: PaymentRequest): Result<PlatronPayment> =
        try {
            Result(factory.retrieveRemoteDataStore().createPlatronPayment(paymentRequest.map()).map())
        } catch (e: RemoteException) {
            Result(PlatronPayment.EMPTY, e.map())
        }

    override suspend fun getCheckoutcomPayment(paymentRequest: PaymentRequest): Result<CheckoutcomPayment> =
        try {
            Result(factory.retrieveRemoteDataStore().createCheckoutcomPayment(paymentRequest.map()).map())
        } catch (e: RemoteException) {
            Result(CheckoutcomPayment.EMPTY, e.map())
        }

    override suspend fun getCheckoutcomToken(
        tokenRequest: CheckoutcomTokenRequest,
        url: String,
        key: String
    ): Result<CheckoutcomToken> =
        try {
            Result(factory.retrieveRemoteDataStore().getCheckoutcomToken(tokenRequest.map(), url, key).map())
        } catch (e: RemoteException) {
            Result(CheckoutcomToken.EMPTY, e.map())
        }

    override suspend fun getBraintreePayment(paymentRequest: PaymentRequest): Result<BraintreePayment> =
        try {
            Result(factory.retrieveRemoteDataStore().createBraintreePayment(paymentRequest.map()).map())
        } catch (e: RemoteException) {
            Result(BraintreePayment.EMPTY, e.map())
        }

    override suspend fun getGooglePayPayment(paymentRequest: PaymentRequest): Result<GooglePayPayment> =
        try {
            Result(factory.retrieveRemoteDataStore().createGooglePayPayment(paymentRequest.map()).map())
        } catch (e: RemoteException) {
            Result(GooglePayPayment.EMPTY, e.map())
        }

    override suspend fun getGroundPayment(paymentRequest: PaymentRequest): Result<Unit> =
        try {
            Result(factory.retrieveRemoteDataStore().createGroundPayment(paymentRequest.map()))
        } catch (e: RemoteException) {
            Result(Unit, e.map())
        }

    override suspend fun processPayment(paymentProcessRequest: PaymentProcessRequest): Result<PaymentProcess> =
        try {
            Result(factory.retrieveRemoteDataStore().processPayment(paymentProcessRequest.map()).map())
        } catch (e: RemoteException) {
            Result(PaymentProcess.EMPTY, e.map())
        }

    override suspend fun changeStatusPayment(paymentStatusRequest: PaymentStatusRequest): Result<PaymentStatus> =
        try {
            Result(factory.retrieveRemoteDataStore().changeStatusPayment(paymentStatusRequest.map()).map())
        } catch (e: RemoteException) {
            Result(PaymentStatus.EMPTY, e.map())
        }

    override suspend fun getBrainTreeToken(): Result<BraintreeToken> =
        try {
            Result(factory.retrieveRemoteDataStore().getBraintreeToken().map())
        } catch (e: RemoteException) {
            Result(BraintreeToken.EMPTY, e.map())
        }

    override suspend fun confirmPaypal(paymentId: Long, nonce: String): Result<PaymentStatus> =
        try {
            Result(factory.retrieveRemoteDataStore().confirmPaypal(paymentId, nonce).map())
        } catch (e: RemoteException) {
            Result(PaymentStatus.EMPTY, e.map())
        }

    internal fun onNewPaymentStatusEvent(isSuccess: Boolean) {
        paymentReceiver.onNewPaymentStatusEvent(isSuccess)
    }
}
