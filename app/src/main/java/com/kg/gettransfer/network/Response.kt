package com.kg.gettransfer.network


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.kg.gettransfer.data.Transfer
import com.kg.gettransfer.data.TransportType


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

    fun success(): Boolean = "success" == result

//    TODO: Why wrong?
//    val success : Boolean = "success" == result
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