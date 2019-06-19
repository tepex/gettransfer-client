package com.kg.gettransfer.data.repository

import com.kg.gettransfer.data.PaymentDataStore
import com.kg.gettransfer.data.ds.DataStoreFactory
import com.kg.gettransfer.data.ds.PaymentDataStoreCache
import com.kg.gettransfer.data.ds.PaymentDataStoreRemote

import com.kg.gettransfer.data.mapper.BraintreeTokenMapper
import com.kg.gettransfer.data.mapper.PaymentMapper
import com.kg.gettransfer.data.mapper.PaymentRequestMapper
import com.kg.gettransfer.data.mapper.PaymentStatusRequestMapper
import com.kg.gettransfer.data.mapper.PaymentStatusMapper

import com.kg.gettransfer.data.model.PaymentEntity
import com.kg.gettransfer.data.model.PaymentStatusEntity

import com.kg.gettransfer.domain.interactor.PaymentInteractor

import com.kg.gettransfer.domain.model.Transfer
import com.kg.gettransfer.domain.model.OfferItem
import com.kg.gettransfer.domain.model.PaymentRequest
import com.kg.gettransfer.domain.model.Payment
import com.kg.gettransfer.domain.model.PaymentStatusRequest
import com.kg.gettransfer.domain.model.PaymentStatus
import com.kg.gettransfer.domain.model.BraintreeToken
import com.kg.gettransfer.domain.model.Result

import com.kg.gettransfer.domain.repository.PaymentRepository

import org.koin.standalone.get
import org.koin.standalone.inject

class PaymentRepositoryImpl(
    private val factory: DataStoreFactory<PaymentDataStore, PaymentDataStoreCache, PaymentDataStoreRemote>
): BaseRepository(), PaymentRepository {

    private val paymentRequestMapper       = get<PaymentRequestMapper>()
    private val paymentMapper              = get<PaymentMapper>()
    private val paymentStatusRequestMapper = get<PaymentStatusRequestMapper>()
    private val paymentStatusMapper        = get<PaymentStatusMapper>()
    private val braintreeTokenMapper       = get<BraintreeTokenMapper>()

    private val paymentReceiver: PaymentInteractor by inject()

    override var selectedTransfer: Transfer? = null
    override var selectedOffer: OfferItem? = null

    override suspend fun getPayment(paymentRequest: PaymentRequest): Result<Payment> =
        retrieveRemoteModel<PaymentEntity, Payment>(paymentMapper, Payment("", null, null, null)) {
            factory.retrieveRemoteDataStore().createPayment(paymentRequestMapper.toEntity(paymentRequest))
        }

    override suspend fun changeStatusPayment(paymentStatusRequest: PaymentStatusRequest): Result<PaymentStatus> =
        retrieveRemoteModel<PaymentStatusEntity, PaymentStatus>(paymentStatusMapper, PaymentStatus(0, "")) {
            factory.retrieveRemoteDataStore().changeStatusPayment(paymentStatusRequestMapper.toEntity(paymentStatusRequest))
        }

    override suspend fun getBrainTreeToken(): Result<BraintreeToken> =
        retrieveRemoteModel(braintreeTokenMapper, BraintreeToken("", "")) {
            factory.retrieveRemoteDataStore().getBraintreeToken()
        }

    override suspend fun confirmPaypal(paymentId: Long, nonce: String): Result<PaymentStatus> =
        retrieveRemoteModel(paymentStatusMapper, PaymentStatus(0, "")) {
            factory.retrieveRemoteDataStore().confirmPaypal(paymentId, nonce)
        }

    internal fun onNewPaymentStatusEvent(isSuccess: Boolean) {
        paymentReceiver.onNewPaymentStatusEvent(isSuccess)
    }
}
