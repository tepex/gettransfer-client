package com.kg.gettransfer.remote

import com.kg.gettransfer.data.PaymentRemote

import com.kg.gettransfer.data.model.PaymentEntity
import com.kg.gettransfer.data.model.PaymentRequestEntity
import com.kg.gettransfer.data.model.PaymentStatusEntity
import com.kg.gettransfer.data.model.PaymentStatusRequestEntity

import com.kg.gettransfer.remote.mapper.PaymentMapper
import com.kg.gettransfer.remote.mapper.PaymentRequestMapper
import com.kg.gettransfer.remote.mapper.PaymentStatusMapper
import com.kg.gettransfer.remote.mapper.PaymentStatusRequestMapper

import com.kg.gettransfer.remote.model.PaymentModel
import com.kg.gettransfer.remote.model.PaymentStatusRequestModel
import com.kg.gettransfer.remote.model.PaymentStatusWrapperModel
import com.kg.gettransfer.remote.model.ResponseModel

import org.koin.standalone.get

class PaymentRemoteImpl : PaymentRemote {
    private val core                       = get<ApiCore>()
    private val paymentRequestMapper       = get<PaymentRequestMapper>()
    private val paymentMapper              = get<PaymentMapper>()
    private val paymentStatusRequestMapper = get<PaymentStatusRequestMapper>()
    private val paymentStatusMapper        = get<PaymentStatusMapper>()
    private val braintreeTokenMapper       = get<BraintreeTokenMapper>()

    override suspend fun createPayment(paymentRequest: PaymentRequestEntity): PaymentEntity {
        //val response: ResponseModel<PaymentModel> = tryCreatePayment(paymentRequestMapper.toRemote(paymentRequest))
        val response: ResponseModel<PaymentModel> = core.tryTwice { core.api.createNewPayment(paymentRequestMapper.toRemote(paymentRequest)) }
        return paymentMapper.fromRemote(response.data!!)
    }

    /*private suspend fun tryCreatePayment(paymentRequest: PaymentRequestModel): ResponseModel<PaymentModel> {
        return try { core.api.createNewPayment(paymentRequest).await() }
        catch (e: Exception) {
            if (e is RemoteException) throw e *//* second invocation *//*
            val ae = core.remoteException(e)
            if (!ae.isInvalidToken()) throw ae

            try { core.updateAccessToken() } catch (e1: Exception) { throw core.remoteException(e1) }
            return try { core.api.createNewPayment(paymentRequest).await() } catch (e2: Exception) { throw core.remoteException(e2) }
        }
    }*/

    override suspend fun changeStatusPayment(paymentStatusRequest: PaymentStatusRequestEntity): PaymentStatusEntity {
        /*val response: ResponseModel<PaymentStatusWrapperModel> =
            tryChangeStatusPayment(paymentStatusRequest.success, paymentStatusRequestMapper.toRemote(paymentStatusRequest))*/
        val status = if (paymentStatusRequest.success) PaymentStatusRequestModel.STATUS_SUCCESSFUL else PaymentStatusRequestModel.STATUS_FAILED
        val paymentStatusRequestRemote = paymentStatusRequestMapper.toRemote(paymentStatusRequest)
        val response: ResponseModel<PaymentStatusWrapperModel> = core.tryTwice {
            core.api.changePaymentStatus(status, paymentStatusRequestRemote.pgOrderId!!, paymentStatusRequestRemote.withoutRedirect!!)
        }
        return paymentStatusMapper.fromRemote(response.data!!.payment)
    }

    override suspend fun getBraintreeToken(): BraintreeTokenEntity {
        val responce: ResponseModel<BraintreeTokenModel> = core.tryTwice { core.api.getBraintreeToken() }
        return braintreeTokenMapper.fromRemote(responce.data!!)
    }

    override suspend fun confirmPaypal(paymentId: Long, nonce: String): PaymentStatusEntity {
        val response: ResponseModel<PaymentStatusWrapperModel> = core.tryTwice {
            core.api.confirmPaypal(paymentId, nonce)
        }
        return paymentStatusMapper.fromRemote(response.data!!.payment)
    }

    /*private suspend fun tryChangeStatusPayment(success: Boolean, paymentStatusRequest: PaymentStatusRequestModel): ResponseModel<PaymentStatusWrapperModel> {
        val status = if (success) PaymentStatusRequestModel.STATUS_SUCCESSFUL else PaymentStatusRequestModel.STATUS_FAILED
        return try {
            core.api.changePaymentStatus(status,
                    paymentStatusRequest.pgOrderId!!,
                    paymentStatusRequest.withoutRedirect!!).await()
        } catch (e: Exception) {
            if (e is RemoteException) throw e *//* second invocation *//*
            val ae = core.remoteException(e)
            if (!ae.isInvalidToken()) throw ae

            try { core.updateAccessToken() } catch (e1: Exception) { throw core.remoteException(e1) }
            return try { core.api.changePaymentStatus(status,
                    paymentStatusRequest.pgOrderId!!,
                    paymentStatusRequest.withoutRedirect!!).await() } catch (e2: Exception) { throw core.remoteException(e2) }
        }
    }*/
}
