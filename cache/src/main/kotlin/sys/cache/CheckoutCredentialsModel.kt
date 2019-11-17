package sys.cache

import androidx.room.ColumnInfo
import sys.data.CheckoutCredentialsEntity

data class CheckoutCredentialsModel(
    @ColumnInfo(name = CheckoutCredentialsEntity.PUBLIC_KEY) val publicKey: String
)

fun CheckoutCredentialsModel.map() =
    CheckoutCredentialsEntity(
        publicKey
    )

fun CheckoutCredentialsEntity.map() =
    CheckoutCredentialsModel(
        publicKey
    )