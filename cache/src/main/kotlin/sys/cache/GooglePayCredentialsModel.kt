package sys.cache

import androidx.room.ColumnInfo
import com.kg.gettransfer.cache.model.StringList
import sys.data.GooglePayCredentialsEntity

data class GooglePayCredentialsModel(
    @ColumnInfo(name = GooglePayCredentialsEntity.ENVIRONMENT)   val environment: String,
    @ColumnInfo(name = GooglePayCredentialsEntity.MERCHANT_ID)   val merchantId: String,
    @ColumnInfo(name = GooglePayCredentialsEntity.MERCHANT_NAME) val merchantName: String,
    @ColumnInfo(name = GooglePayCredentialsEntity.CARD_NETWORKS) val cardNetworks: StringList,
    @ColumnInfo(name = GooglePayCredentialsEntity.AUTH_METHODS)  val authMethods: StringList
)

fun GooglePayCredentialsModel.map() =
    GooglePayCredentialsEntity(
        environment,
        merchantId,
        merchantName,
        cardNetworks.list,
        authMethods.list
    )

fun GooglePayCredentialsEntity.map() =
    GooglePayCredentialsModel(
        environment,
        merchantId,
        merchantName,
        StringList(cardNetworks),
        StringList(authMethods)
    )
