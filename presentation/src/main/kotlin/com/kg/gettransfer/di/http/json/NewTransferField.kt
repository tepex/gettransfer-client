package com.kg.gettransfer.module.http.json


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


/**
 * Created by denisvakulenko on 05/02/2018.
 */


class NewTransferField(@Expose
                       @SerializedName("transfer")
                       val transfer: NewTransfer)