package com.kg.gettransfer.remote

import com.kg.gettransfer.data.PaymentRemote
import com.kg.gettransfer.data.RemoteException

import com.kg.gettransfer.data.model.PaymentEntity
import com.kg.gettransfer.data.model.PaymentRequestEntity
import com.kg.gettransfer.data.model.PaymentStatusEntity
import com.kg.gettransfer.data.model.PaymentStatusRequestEntity

import com.kg.gettransfer.remote.mapper.PaymentMapper
import com.kg.gettransfer.remote.mapper.PaymentRequestMapper
import com.kg.gettransfer.remote.mapper.PaymentStatusMapper
import com.kg.gettransfer.remote.mapper.PaymentStatusRequestMapper

import com.kg.gettransfer.remote.model.PaymentModel
import com.kg.gettransfer.remote.model.PaymentRequestModel
import com.kg.gettransfer.remote.model.PaymentStatusModel
import com.kg.gettransfer.remote.model.PaymentStatusRequestModel
import com.kg.gettransfer.remote.model.ResponseModel

class PaymentRemoteImpl(private val core: ApiCore,
                        private val paymentRequestMapper: PaymentRequestMapper,
                        private val paymentMapper: PaymentMapper,
                        private val paymentStatusRequestMapper: PaymentStatusRequestMapper,
                        private val paymentStatusMapper: PaymentStatusMapper): PaymentRemote {

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
    
    override suspend fun changeStatusPayment(paymentStatusRequest: PaymentStatusRequestEntity): PaymentStatusEntity {
        val response: ResponseModel<PaymentStatusModel> = tryChangeStatusPayment(paymentStatusRequestMapper.toRemote(paymentStatusRequest))
        return paymentStatusMapper.fromRemote(response.data!!)
    }
    
    private suspend fun tryChangeStatusPayment(paymentStatusRequest: PaymentStatusRequestModel): ResponseModel<PaymentStatusModel> {
        return try { core.api.changeStatusPayment(paymentStatusRequest).await() }
        catch(e: Exception) {
            if(e is RemoteException) throw e /* second invocation */
            val ae = core.remoteException(e)
            if(!ae.isInvalidToken()) throw ae

            try { core.updateAccessToken() } catch(e1: Exception) { throw core.remoteException(e1) }
            return try { core.api.createStatusPayment(paymentStatusRequest).await() } catch(e2: Exception) { throw core.remoteException(e2) }
        }
    }
}
