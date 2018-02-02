package com.kg.gettransfer.modules


import android.util.Log
import com.kg.gettransfer.models.Location
import com.kg.gettransfer.models.PassengerProfile
import com.kg.gettransfer.network.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers


/**
 * Created by denisvakulenko on 30/01/2018.
 */


class Transfers {
    private val TAG = "Transfers"


    private val transferHardcode: TransferPOJO by lazy {
        TransferPOJO(
                19,
                33,
                Location("Moscow", 1.0, 1.0),
                Location("Petersburg", 2.0, 2.0),
                "2020/12/25",
                "15:00",
                intArrayOf(1),
                1,
                "Denis",
                PassengerProfile("d.vakulenko@key-g.com", "+79998887766"))
    }


    fun createTransfer(transfer: TransferPOJO = transferHardcode) {
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