package com.kg.gettransfer.presentation.ui.helpers

import com.google.android.gms.wallet.WalletConstants
import com.kg.gettransfer.sys.presentation.ConfigsManager
import org.json.JSONArray
import org.json.JSONObject
import org.koin.core.KoinComponent
import org.koin.core.inject

object GooglePayRequestsHelper : KoinComponent {

    private val configsManager: ConfigsManager by inject()

    fun getEnvironment() =
        when(configsManager.configs.googlePayCredentials.environment) {
            "production" -> WalletConstants.ENVIRONMENT_PRODUCTION
            else         -> WalletConstants.ENVIRONMENT_TEST
        }

    private fun getBaseRequest() =
        JSONObject().apply {
            put("apiVersion", 2)
            put("apiVersionMinor", 0)
        }

    private fun getTokenizationSpecification() =
        JSONObject().apply {
            put("type", "PAYMENT_GATEWAY")
            put(
                "parameters",
                JSONObject().apply {
                    put("gateway", "checkoutltd")
                    put("gatewayMerchantId", configsManager.configs.checkoutCredentials.publicKey)
                }
            )
        }

    private fun getAllowedCardNetworks() =
        JSONArray(configsManager.configs.googlePayCredentials.cardNetworks)

    private fun getAllowedCardAuthMethods() =
        JSONArray(configsManager.configs.googlePayCredentials.authMethods)

    private fun getBaseCardPaymentMethod() =
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

    private fun getCardPaymentMethod() =
        getBaseCardPaymentMethod().apply {
            put("tokenizationSpecification", getTokenizationSpecification())
        }

    fun getIsReadyToPayRequest() =
        getBaseRequest().apply {
            put(
                "allowedPaymentMethods",
                JSONArray().apply {
                    put(getBaseCardPaymentMethod())
                }
            )
            put("existingPaymentMethodRequired", true)
        }

    private fun getTransactionInfo(price: String, currency: String) =
        JSONObject().apply {
            put("totalPrice", price)
            put("totalPriceStatus", "FINAL")
            put("currencyCode", currency)
        }

    private fun getMerchantInfo() =
        JSONObject().apply {
            put("merchantId", configsManager.configs.googlePayCredentials.merchantId)
            put("merchantName", configsManager.configs.googlePayCredentials.merchantName)
        }

    fun getPaymentDataRequest(price: String, currency: String) =
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