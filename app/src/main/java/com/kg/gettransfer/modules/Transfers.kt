package com.kg.gettransfer.modules


import android.util.Log
import com.kg.gettransfer.models.Transfer
import com.kg.gettransfer.network.Api
import com.kg.gettransfer.network.Location
import com.kg.gettransfer.network.PassengerProfile
import com.kg.gettransfer.network.TransferFieldPOJO
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers


/**
 * Created by denisvakulenko on 30/01/2018.
 */


class Transfers {
    private val TAG = "Transfers"


    private val transferHardcode: Transfer by lazy {
        Transfer(
                19,
                33,
                Location("Moscow", 1.0, 1.0).toMap(),
                Location("Petersburg", 2.0, 2.0).toMap(),
                "2020/12/25",
                "15:00",
                intArrayOf(1),
                1,
                "Denis",
                PassengerProfile("d.vakulenko@key-g.com", "+79998887766").toMap(),
                -1)
    }


    fun createTransfer(transfer: Transfer = transferHardcode) {
        Log.d(TAG, "createTransfer()")
        Api.api.postTransfer(TransferFieldPOJO(transfer))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ r ->
                    if (r.success) {
                        Log.d(TAG, "createTransfer() responded success, id = ${r.data?.id}")
                        updateTransfers()
                    } else {
                        Log.d(TAG, "createTransfer() responded fail, result = ${r.result}")
                    }
                }, { error ->
                    Log.d(TAG, "createTransfer() fail, ${error.message}")
                })
    }


    fun updateTransfers() {
        Log.d(TAG, "getTransfers()")
        Api.api.getTransfers()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ r ->
                    if (r.success) {
                        Log.d(TAG, "getTransfers() responded success, N = ${r.data?.transfers?.size}")
                    } else {
                        Log.d(TAG, "getTransfers() responded fail, result = ${r.result}")
                    }
                }, { error ->
                    Log.d(TAG, "getTransfers() fail, ${error.message}")
                })
    }
}