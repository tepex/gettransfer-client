package sys.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import sys.domain.GooglePayCredentials
import java.util.Locale

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
        environment.map(),
        merchantId,
        merchantName,
        cardNetworks,
        authMethods
    )

fun String.map(): GooglePayCredentials.ENVIRONMENT =
    try {
        enumValueOf<GooglePayCredentials.ENVIRONMENT>(toUpperCase(Locale.US))
    } catch (e: IllegalArgumentException) { GooglePayCredentials.ENVIRONMENT.UNKNOWN }
