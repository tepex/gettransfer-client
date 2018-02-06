package com.kg.gettransfer.modules


import android.util.Log
import com.jakewharton.rxrelay2.BehaviorRelay
import com.kg.gettransfer.RxBus
import com.kg.gettransfer.models.Location
import com.kg.gettransfer.models.Transfer
import com.kg.gettransfer.modules.network.Api
import com.kg.gettransfer.modules.network.NewTransfer
import com.kg.gettransfer.modules.network.NewTransferField
import com.kg.gettransfer.modules.network.PassengerProfile
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.realm.Realm


/**
 * Created by denisvakulenko on 30/01/2018.
 */


object TransfersModule {
    private val TAG = "TransfersModule"


    private val transferHardcode: NewTransfer by lazy {
        NewTransfer(
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

    private val realm: Realm by lazy {
        Realm.getDefaultInstance()
    }


    val loadingTransfers = BehaviorRelay.createDefault<Boolean>(false)


    init {
        RxBus.listen(CurrentUserModule.UserChanged::class.java)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    val transfers = realm.where(Transfer::class.java).findAll()
                    realm.executeTransaction {
                        transfers.deleteAllFromRealm()
                        Log.d(TAG, "realm.where(Transfer).deleteAll()")
                    }
                }
    }


    fun createTransfer(transfer: NewTransfer = transferHardcode) {
        Log.d(TAG, "createTransfer()")
        Api.api.postTransfer(NewTransferField(transfer))
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

    fun getTransfers() = realm.where(Transfer::class.java).findAll()


    fun updateTransfers() {
        Log.d(TAG, "updateTransfers()")
        if (loadingTransfers.value) return
        Log.d(TAG, "getTransfers() call")
        Api.api.getTransfers()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { loadingTransfers.accept(true) }
                .subscribe({
                    loadingTransfers.accept(false)
                    if (it.success) {
                        Log.d(TAG, "getTransfers() responded success, N = ${it.data?.transfers?.size}")

                        realm.executeTransaction { realm ->
                            realm.copyToRealmOrUpdate(it.data?.transfers)
                            Log.d(TAG, "getTransfers() saved to realm")
                        }
                    } else {
                        Log.d(TAG, "getTransfers() responded fail, result = ${it.result}")
                    }
                }, {
                    loadingTransfers.accept(false)
                    Log.d(TAG, "getTransfers() fail, ${it.message}")
                })
    }
}
