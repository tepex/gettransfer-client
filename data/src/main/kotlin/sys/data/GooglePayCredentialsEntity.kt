package sys.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import sys.domain.GooglePayCredentials

@Serializable
data class GooglePayCredentialsEntity(
    @SerialName(ENVIRONMENT)   val environment: String,
    @SerialName(MERCHANT_ID)   val merchantId: String,
    @SerialName(MERCHANT_NAME) val merchantName: String,
    @SerialName(CARD_NETWORKS) val cardNetworks: List<String>,
    @SerialName(AUTH_METHODS)  val authMethods: List<String>
) {
    companion object {
        const val ENVIRONMENT   = "environment"
        const val MERCHANT_ID   = "merchant_id"
        const val MERCHANT_NAME = "merchant_name"
        const val CARD_NETWORKS = "allowed_card_networks"
        const val AUTH_METHODS  = "allowed_auth_methods"
    }
}

fun GooglePayCredentialsEntity.map() =
    GooglePayCredentials(
        environment,
        merchantId,
        merchantName,
        cardNetworks,
        authMethods
    )
