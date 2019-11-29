package sys.cache

import androidx.room.ColumnInfo
import sys.data.CheckoutcomCredentialsEntity

data class CheckoutcomCredentialsModel(
    @ColumnInfo(name = CheckoutcomCredentialsEntity.ENVIRONMENT) val environment: String,
    @ColumnInfo(name = CheckoutcomCredentialsEntity.PUBLIC_KEY) val publicKey: String
)

fun CheckoutcomCredentialsModel.map() =
    CheckoutcomCredentialsEntity(
        environment,
        publicKey
    )

fun CheckoutcomCredentialsEntity.map() =
    CheckoutcomCredentialsModel(
        environment,
        publicKey
    )