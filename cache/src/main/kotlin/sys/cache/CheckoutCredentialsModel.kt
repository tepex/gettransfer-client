package sys.cache

import androidx.room.ColumnInfo
import sys.data.CheckoutCredentialsEntity

data class CheckoutCredentialsModel(
    @ColumnInfo(name = CheckoutCredentialsEntity.ENVIRONMENT) val environment: String,
    @ColumnInfo(name = CheckoutCredentialsEntity.PUBLIC_KEY) val publicKey: String
)

fun CheckoutCredentialsModel.map() =
    CheckoutCredentialsEntity(
        environment,
        publicKey
    )

fun CheckoutCredentialsEntity.map() =
    CheckoutCredentialsModel(
        environment,
        publicKey
    )