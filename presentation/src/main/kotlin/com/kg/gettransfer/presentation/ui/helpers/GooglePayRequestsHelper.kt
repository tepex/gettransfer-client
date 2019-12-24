package com.kg.gettransfer.presentation.ui.helpers

import com.google.android.gms.wallet.WalletConstants
import com.kg.gettransfer.sys.presentation.ConfigsManager
import org.json.JSONArray
import org.json.JSONObject
import org.koin.core.KoinComponent
import org.koin.core.inject
import sys.domain.GooglePayCredentials

object GooglePayRequestsHelper : KoinComponent {

    private val configsManager: ConfigsManager by inject()

    suspend fun getEnvironment(): Int? {
        return when(configsManager.getConfigs().googlePayCredentials.environment) {
            GooglePayCredentials.ENVIRONMENT.PRODUCTION -> WalletConstants.ENVIRONMENT_PRODUCTION
            GooglePayCredentials.ENVIRONMENT.TEST       -> WalletConstants.ENVIRONMENT_TEST
            else                                        -> null
        }
    }

    private fun getBaseRequest() =
        JSONObject().apply {
            put("apiVersion", 2)
            put("apiVersionMinor", 0)
        }

    private fun getTokenizationSpecification(gateway: String, gatewayMerchantId: String) =
        JSONObject().apply {
            put("type", "PAYMENT_GATEWAY")
            put(
                "parameters",
                JSONObject().apply {
                    put("gateway", gateway)
                    put("gatewayMerchantId", gatewayMerchantId)
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

    private suspend fun getCardPaymentMethod(gateway: String, gatewayMerchantId: String) =
        getBaseCardPaymentMethod().apply {
            put("tokenizationSpecification", getTokenizationSpecification(gateway, gatewayMerchantId))
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

    private fun getTransactionInfo(price: Float, currency: String, countryCode: String) =
        JSONObject().apply {
            put("totalPrice", price.toString())
            put("totalPriceStatus", "FINAL")
            put("currencyCode", currency)
            put("countryCode", countryCode)
        }

    private suspend fun getMerchantInfo() =
        JSONObject().apply {
            put("merchantId", configsManager.getConfigs().googlePayCredentials.merchantId)
            put("merchantName", configsManager.getConfigs().googlePayCredentials.merchantName)
        }

    suspend fun getPaymentDataRequest(price: Float, currency: String, countryCode: String, gateway: String, gatewayMerchantId: String) =
        getBaseRequest().apply {
            put(
                "allowedPaymentMethods",
                JSONArray().apply {
                    put(getCardPaymentMethod(gateway, gatewayMerchantId))
                }
            )
            put("transactionInfo", getTransactionInfo(price, currency, countryCode))
            put("merchantInfo", getMerchantInfo())
        }
}
