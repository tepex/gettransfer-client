package sys.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import sys.domain.CheckoutCredentials

@Serializable
data class CheckoutCredentialsEntity(
    @SerialName(ENVIRONMENT) val environment: String,
    @SerialName(PUBLIC_KEY)  val publicKey: String
) {
    companion object {
        const val ENVIRONMENT = "environment"
        const val PUBLIC_KEY = "public_key"
    }
}

fun CheckoutCredentialsEntity.map() =
    CheckoutCredentials(
        environment,
        publicKey
    )