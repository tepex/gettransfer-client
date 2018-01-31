package com.kg.gettransfer.modules


import android.util.Log
import com.kg.gettransfer.data.Transfer
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
                Transfer(
                        19,
                        33,
                        Location("Moscow", 1.0, 1.0).toMap(),
                        Location("Petersburg", 2.0, 2.0).toMap(),
                        "2020/12/25",
                        "15:00",
                        intArrayOf(1),
                        1,
                        "Ivan",
                        PassengerProfile("d.vakulenko.123@key-g.com", "+79998887766").toMap(),
                        -1))
    }


    fun postTransfer(transfer: TransferFieldPOJO = transferHardcode) {
        Log.d(TAG, "postTransfer()")
        Api.api.postTransfer(transfer)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ r ->
                    if (r.success()) {
                        Log.d(TAG, "postTransfer() responded success, id = ${r.data?.id}")
                        updateTransfers()
                    } else {
                        Log.d(TAG, "postTransfer() responded fail, result = ${r.result}")
                    }
                }, { error ->
                    Log.d(TAG, "postTransfer() fail, ${error.message}")
                })
    }


    fun updateTransfers() {
        Log.d(TAG, "getTransfers()")
        Api.api.getTransfers()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ r ->
                    if (r.success()) {
                        Log.d(TAG, "getTransfers() responded success, N = ${r.data?.transfers?.size}")
                    } else {
                        Log.d(TAG, "getTransfers() responded fail, result = ${r.result}")
                    }
                }, { error ->
                    Log.d(TAG, "getTransfers() fail, ${error.message}")
                })
    }
}