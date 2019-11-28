package sys.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import sys.domain.CheckoutcomCredentials

@Serializable
data class CheckoutcomCredentialsEntity(
    @SerialName(ENVIRONMENT) val environment: String,
    @SerialName(PUBLIC_KEY)  val publicKey: String
) {
    companion object {
        const val ENVIRONMENT = "environment"
        const val PUBLIC_KEY = "public_key"
    }
}

fun CheckoutcomCredentialsEntity.map() =
    CheckoutcomCredentials(
        environment,
        publicKey
    )