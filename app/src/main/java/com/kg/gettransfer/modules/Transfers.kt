package com.kg.gettransfer.modules


import android.util.Log
import com.jakewharton.rxrelay2.BehaviorRelay
import com.kg.gettransfer.modules.http.HttpApi
import com.kg.gettransfer.modules.http.json.NewTransfer
import com.kg.gettransfer.modules.http.json.NewTransferField
import com.kg.gettransfer.modules.http.json.PassengerProfile
import com.kg.gettransfer.realm.Location
import com.kg.gettransfer.realm.Transfer
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.realm.Realm
import io.realm.RealmResults
import org.koin.standalone.KoinComponent


/**
 * Created by denisvakulenko on 30/01/2018.
 */


class Transfers(private val realm: Realm, private val api: HttpApi, currentAccount: CurrentAccount) : KoinComponent {
    private val TAG = "Transfers"

    val busy = BehaviorRelay.createDefault<Boolean>(false)!!


    init {
        currentAccount.loggedIn
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    if (it) deleteTransfers()
                }

        if (currentAccount.email == null) deleteTransfers()
    }


    private fun deleteTransfers() {
        realm.executeTransaction {
            val transfers = realm.where(Transfer::class.java).findAllAsync()
            transfers.deleteAllFromRealm()
            Log.d(TAG, "realm.where(Transfer).deleteAll()")
        }
    }


    fun createTransfer(transfer: NewTransfer): Observable<Transfer> =
            Observable.create<Transfer> {
                Log.d(TAG, "createTransfer()")
                api.postTransfer(NewTransferField(transfer))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({ r ->
                            if (r.success) {
                                Log.d(TAG, "createTransfer() responded success, id = ${r.data?.id}")
                                updateTransfers()
                                val t = Transfer()
                                t.id = r.data?.id ?: -1
                                it.onNext(t)
                            } else {
                                Log.d(TAG, "createTransfer() responded fail, result = ${r.result}")
                                it.onError(Exception(r.error?.message))
                            }
                        }, { error ->
                            Log.d(TAG, "createTransfer() fail, ${error.message}")
                            it.onError(error)
                        })
            }


    fun getAllAsync(): RealmResults<Transfer> =
            realm.where(Transfer::class.java).sort("dateTo").findAllAsync()


    fun getAsync(id: Int): RealmResults<Transfer> =
            realm.where(Transfer::class.java).equalTo("id", id).findAllAsync()


    fun updateTransfers() {
        Log.d(TAG, "updateTransfers()")
        if (busy.value) return
        Log.d(TAG, "getTransfers() call")
        api.getTransfers()
                .subscribeOn(Schedulers.io())
                .doOnSubscribe { busy.accept(true) }
                .observeOn(Schedulers.newThread())
                .subscribe({
                    busy.accept(false)
                    if (it.success) {
                        Log.d(TAG, "getTransfers() responded success, N = ${it.data?.transfers?.size}")

                        Realm.getDefaultInstance().executeTransaction { realm ->
                            realm.copyToRealmOrUpdate(it.data?.transfers!!)
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

    fun updateOffers(id: Int) {
//        Log.d(TAG, "updateTransfers()")
//        if (busy.value) return
//        Log.d(TAG, "getTransfers() call")
        api.getOffers(id)
                .subscribeOn(Schedulers.io())
//                .doOnSubscribe { busy.accept(true) }
                .observeOn(Schedulers.newThread())
                .subscribe({
                    busy.accept(false)
                    if (it.success) {
                        Log.d(TAG, "getTransfers() responded success, N = ${it.data?.size}")

                        Realm.getDefaultInstance().executeTransaction { realm ->
//                            realm.copyToRealmOrUpdate(it.data!!)
                            val transfer = realm.where(Transfer::class.java).equalTo("id", id).findFirst()
                                    ?: return@executeTransaction

                            transfer.offers = it.data

                            realm.copyToRealmOrUpdate(transfer)

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
