package sys.remote

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import sys.data.CheckoutcomCredentialsEntity

data class CheckoutcomCredentialsModel(
    @SerializedName(CheckoutcomCredentialsEntity.ENVIRONMENT) @Expose val environment: String,
    @SerializedName(CheckoutcomCredentialsEntity.PUBLIC_KEY)  @Expose val publicKey: String
)

fun CheckoutcomCredentialsModel.map() =
    CheckoutcomCredentialsEntity(
        environment,
        publicKey
    )