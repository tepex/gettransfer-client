package com.kg.gettransfer.network


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.kg.gettransfer.models.Transfer
import com.kg.gettransfer.models.TransportType


/**
 * Created by denisvakulenko on 25/01/2018.
 */


open class Response<T> {
    @SerializedName("result")
    @Expose
    var result: String = ""

    @SerializedName("data")
    @Expose
    var data: T? = null

    val success: Boolean
        get() = "success" == result
}


class BooleanResponse : Response<String>() {
    val ok: Boolean
        get() = "ok" == data
}


typealias NewTransferCreatedResponse = Response<NewTransferCreated>


typealias TransportTypesResponse = Response<List<TransportType>>


class TransferFieldPOJO(@Expose
                        @SerializedName("transfer")
                        val transfer: Transfer)

class TransfersFieldPOJO(@Expose
                         @SerializedName("transfers")
                         val transfers: List<Transfer>)


typealias TransfersResponse = Response<TransfersFieldPOJO>


class LoginPOJO(@Expose
                @SerializedName("email")
                val email: String,
                @Expose
                @SerializedName("password")
                val password: String)