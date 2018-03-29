package com.kg.gettransfer.modules.http.json


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.kg.gettransfer.realm.AccountInfo
import com.kg.gettransfer.realm.Offer
import com.kg.gettransfer.realm.Transfer
import com.kg.gettransfer.realm.TransportType


/**
 * Created by denisvakulenko on 25/01/2018.
 */


open class BaseResponse<T> {
    @SerializedName("result")
    @Expose
    var result: String = ""

    val success: Boolean get() = "success" == result


    @SerializedName("data")
    @Expose
    var data: T? = null

    @SerializedName("error")
    @Expose
    var error: Error? = null


    override fun toString(): String {
        return if (success) "Success: " + data.toString() else "Error: " + error.toString()
    }
}


class Error(
        @SerializedName("type")
        @Expose
        var type: String = "",

        @SerializedName("message")
        @Expose
        var message: String? = null,

        @SerializedName("details")
        @Expose
        var details: String? = null
)


// --


class BooleanResponse : BaseResponse<String>() {
    val ok: Boolean get() = "ok" == data
}


// --


class NewTransferCreated {
    @SerializedName("id")
    @Expose
    var id: Int = 0
}

typealias NewTransferCreatedResponse = BaseResponse<NewTransferCreated>


// --


typealias TransportTypesResponse = BaseResponse<List<TransportType>>


// --


class TransfersField(@Expose
                     @SerializedName("transfers")
                     val transfers: List<Transfer>)

class TransferField(@Expose
                    @SerializedName("transfer")
                    val transfer: Transfer)

typealias TransfersResponse = BaseResponse<TransfersField>


// --


class OffersField(@Expose
                  @SerializedName("offers")
                  val offers: List<Offer>)

typealias OffersResponse = BaseResponse<OffersField>


// --


class AccessToken {
    @SerializedName("token")
    @Expose
    var accessToken: String = ""
}


// --


class AccountField(@Expose
                   @SerializedName("account")
                   val account: AccountInfo,

                   @Expose
                   @SerializedName("_method")
                   val method: String = "PUT")