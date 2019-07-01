package com.kg.gettransfer.data.repository

import com.kg.gettransfer.data.PaymentDataStore
import com.kg.gettransfer.data.RemoteException
import com.kg.gettransfer.data.ds.DataStoreFactory
import com.kg.gettransfer.data.ds.PaymentDataStoreCache
import com.kg.gettransfer.data.ds.PaymentDataStoreRemote
import com.kg.gettransfer.data.model.map
import com.kg.gettransfer.domain.interactor.PaymentInteractor
import com.kg.gettransfer.domain.model.BraintreeToken
import com.kg.gettransfer.domain.model.OfferItem
import com.kg.gettransfer.domain.model.Payment
import com.kg.gettransfer.domain.model.PaymentRequest
import com.kg.gettransfer.domain.model.PaymentStatus
import com.kg.gettransfer.domain.model.PaymentStatusRequest
import com.kg.gettransfer.domain.model.Result
import com.kg.gettransfer.domain.model.Transfer
import com.kg.gettransfer.domain.repository.PaymentRepository
import org.koin.core.inject

class PaymentRepositoryImpl(
    private val factory: DataStoreFactory<PaymentDataStore, PaymentDataStoreCache, PaymentDataStoreRemote>
) : BaseRepository(), PaymentRepository {

    private val paymentReceiver: PaymentInteractor by inject()

    override var selectedTransfer: Transfer? = null
    override var selectedOffer: OfferItem? = null

    override suspend fun getPayment(paymentRequest: PaymentRequest): Result<Payment> =
        try {
            Result(factory.retrieveRemoteDataStore().createPayment(paymentRequest.map()).map())
        } catch (e: RemoteException) {
            Result(Payment.EMPTY, e.map())
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
