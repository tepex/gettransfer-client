package com.kg.gettransfer.modules

import android.util.Log
import com.kg.gettransfer.network.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers


/**
 * Created by denisvakulenko on 30/01/2018.
 */


class Transfers {
    private val TAG = "Transfers"


    private val transferHardcode: TransferFieldPOJO by lazy {
        TransferFieldPOJO(
                TransferPOJO(
                        19,
                        33,
                        Location("Moscow", 1, 1).toMap(),
                        Location("Petersburg", 2, 2).toMap(),
                        "2020/12/25",
                        "15:00",
                        "1",
                        1,
                        "Ivan",
                        PassengerProfile("ivan@key-g.com", "+79998887766").toMap()))
    }


    fun postTransfer(transfer: TransferFieldPOJO = transferHardcode) {
        Log.d(TAG, "postTransfer()")
        Api.api.postTransfer(transfer)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ r ->
                    if (r.success()) {
                        Log.d(TAG, "postTransfer() success, id = ${r.data?.id}")
                    } else {
                        Log.d(TAG, "postTransfer() fail, result = ${r.result}")
                    }
                }, { error ->
                    Log.d(TAG, "postTransfer() fail, ${error.message}")
                })
    }
}