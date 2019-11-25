package sys.remote

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import sys.data.CheckoutCredentialsEntity

data class CheckoutCredentialsModel(
    @SerializedName(CheckoutCredentialsEntity.ENVIRONMENT) @Expose val environment: String,
    @SerializedName(CheckoutCredentialsEntity.PUBLIC_KEY)  @Expose val publicKey: String
)

fun CheckoutCredentialsModel.map() =
    CheckoutCredentialsEntity(
        environment,
        publicKey
    )