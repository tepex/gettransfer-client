package com.kg.gettransfer.presentation.ui.helpers

import com.google.android.gms.wallet.WalletConstants
import com.kg.gettransfer.sys.presentation.ConfigsManager
import org.json.JSONArray
import org.json.JSONObject
import org.koin.core.KoinComponent
import org.koin.core.inject

object GooglePayRequestsHelper : KoinComponent {

    private val configsManager: ConfigsManager by inject()

    suspend fun getEnvironment() =
        when(configsManager.getConfigs().googlePayCredentials.environment) {
            "production" -> WalletConstants.ENVIRONMENT_PRODUCTION
            else         -> WalletConstants.ENVIRONMENT_TEST
        }

    private fun getBaseRequest() =
        JSONObject().apply {
            put("apiVersion", 2)
            put("apiVersionMinor", 0)
        }

    private suspend fun getTokenizationSpecification() =
        JSONObject().apply {
            put("type", "PAYMENT_GATEWAY")
            put(
                "parameters",
                JSONObject().apply {
                    put("gateway", "checkoutltd")
                    put("gatewayMerchantId", configsManager.getConfigs().checkoutCredentials.publicKey)
                }
            )
        }

    private suspend fun getAllowedCardNetworks() =
        JSONArray(configsManager.getConfigs().googlePayCredentials.cardNetworks)

    private suspend fun getAllowedCardAuthMethods() =
        JSONArray(configsManager.getConfigs().googlePayCredentials.authMethods)

    private suspend fun getBaseCardPaymentMethod() =
        JSONObject().apply {
            put("type", "CARD")
            put(
               "parameters",
                JSONObject().apply {
                    put("allowedAuthMethods", getAllowedCardAuthMethods())
                    put("allowedCardNetworks", getAllowedCardNetworks())
                }
            )
        }

    private suspend fun getCardPaymentMethod() =
        getBaseCardPaymentMethod().apply {
            put("tokenizationSpecification", getTokenizationSpecification())
        }

    suspend fun getIsReadyToPayRequest() =
        getBaseRequest().apply {
            put(
                "allowedPaymentMethods",
                JSONArray().apply {
                    put(getBaseCardPaymentMethod())
                }
            )
            put("existingPaymentMethodRequired", true)
        }

    private fun getTransactionInfo(price: Float, currency: String) =
        JSONObject().apply {
            put("totalPrice", price.toString())
            put("totalPriceStatus", "FINAL")
            put("currencyCode", currency)
        }

    private suspend fun getMerchantInfo() =
        JSONObject().apply {
            put("merchantId", configsManager.getConfigs().googlePayCredentials.merchantId)
            put("merchantName", configsManager.getConfigs().googlePayCredentials.merchantName)
        }

    suspend fun getPaymentDataRequest(price: Float, currency: String) =
        getBaseRequest().apply {
            put(
                "allowedPaymentMethods",
                JSONArray().apply {
                    put(getCardPaymentMethod())
                }
            )
            put("transactionInfo", getTransactionInfo(price, currency))
            put("merchantInfo", getMerchantInfo())
        }
}