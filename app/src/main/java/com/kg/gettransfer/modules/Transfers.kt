package com.kg.gettransfer.modules


import android.util.Log
import com.jakewharton.rxrelay2.BehaviorRelay
import com.kg.gettransfer.models.Location
import com.kg.gettransfer.models.Transfer
import com.kg.gettransfer.modules.network.HttpApi
import com.kg.gettransfer.modules.network.json.NewTransfer
import com.kg.gettransfer.modules.network.json.NewTransferField
import com.kg.gettransfer.modules.network.json.PassengerProfile
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.realm.Realm
import org.koin.standalone.KoinComponent


/**
 * Created by denisvakulenko on 30/01/2018.
 */


class Transfers(val realm: Realm, val api: HttpApi, val currentUser: CurrentUser) : KoinComponent {
    private val TAG = "Transfers"


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

    val busy = BehaviorRelay.createDefault<Boolean>(false)


    init {
        currentUser.state
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
        api.postTransfer(NewTransferField(transfer))
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
        if (busy.value) return
        Log.d(TAG, "getTransfers() call")
        api.getTransfers()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { busy.accept(true) }
                .subscribe({
                    busy.accept(false)
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
                    busy.accept(false)
                    Log.d(TAG, "getTransfers() fail, ${it.message}")
                })
    }
}
