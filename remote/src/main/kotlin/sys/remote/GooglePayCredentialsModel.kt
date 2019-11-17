package sys.remote

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import sys.data.GooglePayCredentialsEntity

data class GooglePayCredentialsModel(
    @SerializedName(GooglePayCredentialsEntity.ENVIRONMENT) @Expose val environment: String,
    @SerializedName(GooglePayCredentialsEntity.MERCHANT_ID) @Expose val merchantId: String,
    @SerializedName(GooglePayCredentialsEntity.MERCHANT_NAME) @Expose val merchantName: String,
    @SerializedName(GooglePayCredentialsEntity.CARD_NETWORKS) @Expose val cardNetworks: List<String>,
    @SerializedName(GooglePayCredentialsEntity.AUTH_METHODS) @Expose val authMethods: List<String>
)

fun GooglePayCredentialsModel.map() =
    GooglePayCredentialsEntity(
        environment,
        merchantId,
        merchantName,
        cardNetworks,
        authMethods
    )