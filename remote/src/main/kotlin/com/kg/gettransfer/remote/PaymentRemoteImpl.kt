package com.kg.gettransfer.remote

import com.kg.gettransfer.data.PaymentRemote
import com.kg.gettransfer.data.RemoteException

import com.kg.gettransfer.data.model.PaymentEntity
import com.kg.gettransfer.data.model.PaymentRequestEntity

import com.kg.gettransfer.remote.mapper.PaymentMapper
import com.kg.gettransfer.remote.mapper.PaymentRequestMapper

import com.kg.gettransfer.remote.model.PaymentModel
import com.kg.gettransfer.remote.model.PaymentRequestModel
import com.kg.gettransfer.remote.model.ResponseModel

class PaymentRemoteImpl(private val core: ApiCore,
                        private val paymentMapper: PaymentMapper,
                        private val paymentRequestMapper: PaymentRequestMapper): PaymentRemote {

/*
    override suspend fun createPayment(transferId: Long, offerId: Long?, gatewayId: String, percentage: Int): PaymentEntity {
        val response: ResponseModel<PaymentModel> = core.tryTwice { core.api.createNewPayment(CreatePaymentEntity(transferId, offerId, gatewayId, percentage)) }
        val payment: ApiPaymentResult = response.data!!
        return Payment(payment.type, payment.url)
    }
    */

    override suspend fun createPayment(paymentRequest: PaymentRequestEntity): PaymentEntity {
        val response: ResponseModel<PaymentModel> = tryCreatePayment(paymentRequestMapper.toRemote(paymentRequest))
        return paymentMapper.fromRemote(response.data!!)
    }
    
    private suspend fun tryCreatePayment(paymentRequest: PaymentRequestModel): ResponseModel<PaymentModel> {
        return try { core.api.createNewPayment(paymentRequest).await() }
        catch(e: Exception) {
            if(e is RemoteException) throw e /* second invocation */
            val ae = core.remoteException(e)
            if(!ae.isInvalidToken()) throw ae

            try { core.updateAccessToken() } catch(e1: Exception) { throw core.remoteException(e1) }
            return try { core.api.createNewPayment(paymentRequest).await() } catch(e2: Exception) { throw core.remoteException(e2) }
        }
    }
}
