package com.kg.gettransfer.remote.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.kg.gettransfer.data.model.PaymentEntity
import com.kg.gettransfer.data.model.ParamsEntity
import com.kg.gettransfer.data.model.PlatronPaymentEntity
import com.kg.gettransfer.data.model.CheckoutcomPaymentEntity
import com.kg.gettransfer.data.model.PaypalPaymentEntity
import com.kg.gettransfer.data.model.BraintreePaymentEntity
import com.kg.gettransfer.data.model.BraintreeParamsEntity
import com.kg.gettransfer.data.model.GooglePayPaymentEntity
import com.kg.gettransfer.data.model.GooglePayParamsEntity

data class PlatronPaymentModel(
    @SerializedName(PaymentEntity.TYPE) @Expose val type: String,
    @SerializedName(PaymentEntity.URL)  @Expose val url: String
)

data class CheckoutcomPaymentModel(
    @SerializedName(PaymentEntity.TYPE)       @Expose val type: String,
    @SerializedName(PaymentEntity.URL)        @Expose val url: String,
    @SerializedName(PaymentEntity.PAYMENT_ID) @Expose val paymentId: Long,
    @SerializedName(PaymentEntity.AMOUNT_FORMATTED) @Expose val amountFormatted: String
)

data class PaypalPaymentModel(
    @SerializedName(PaymentEntity.TYPE)   @Expose val type: String,
    @SerializedName(PaymentEntity.PARAMS) @Expose val params: PaypalParams
)

data class BraintreePaymentModel(
    @SerializedName(PaymentEntity.TYPE)   @Expose val type: String,
    @SerializedName(PaymentEntity.PARAMS) @Expose val params: BraintreeParams
)

data class GooglePayPaymentModel(
    @SerializedName(PaymentEntity.TYPE)   @Expose val type: String,
    @SerializedName(PaymentEntity.PARAMS) @Expose val params: GooglePayParams
)

data class PaypalParams(
    @SerializedName(ParamsEntity.AMOUNT)   @Expose val amount: Float,
    @SerializedName(ParamsEntity.CURRENCY) @Expose val currency: String
)

data class BraintreeParams(
    @SerializedName(ParamsEntity.AMOUNT)     @Expose val amount: Float,
    @SerializedName(ParamsEntity.CURRENCY)   @Expose val currency: String,
    @SerializedName(ParamsEntity.PAYMENT_ID) @Expose val paymentId: Long
)

data class GooglePayParams(
    @SerializedName(ParamsEntity.AMOUNT)       @Expose val amount: Float,
    @SerializedName(ParamsEntity.CURRENCY)     @Expose val currency: String,
    @SerializedName(ParamsEntity.COUNTRY_CODE) @Expose val countryCode: String,
    @SerializedName(ParamsEntity.PAYMENT_ID)   @Expose val paymentId: Long,
    @SerializedName(ParamsEntity.GATEWAY)      @Expose val gateway: String,
    @SerializedName(ParamsEntity.GATEWAY_MERCHANT_ID) @Expose val gatewayMerchantId: String
)

fun PlatronPaymentModel.map() =
    PlatronPaymentEntity(
        type,
        url
    )

fun CheckoutcomPaymentModel.map() =
    CheckoutcomPaymentEntity(
        type,
        url,
        paymentId,
        amountFormatted
    )

fun PaypalPaymentModel.map() =
    PaypalPaymentEntity(
        type,
        params.map()
    )

fun BraintreePaymentModel.map() =
    BraintreePaymentEntity(
        type,
        params.map()
    )

fun GooglePayPaymentModel.map() =
    GooglePayPaymentEntity(
        type,
        params.map()
    )

fun PaypalParams.map() =
    ParamsEntity(
        amount,
        currency
    )

fun BraintreeParams.map() =
    BraintreeParamsEntity(
        amount,
        currency,
        paymentId
    )

fun GooglePayParams.map() =
    GooglePayParamsEntity(
        amount,
        currency,
        countryCode,
        paymentId,
        gateway,
        gatewayMerchantId
    )
