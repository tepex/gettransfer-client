package sys.remote

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import sys.data.CheckoutCredentialsEntity

data class CheckoutCredentialsModel(
    @SerializedName(CheckoutCredentialsEntity.PUBLIC_KEY) @Expose val publicKey: String
)

fun CheckoutCredentialsModel.map() =
    CheckoutCredentialsEntity(
        publicKey
    )