package com.kg.gettransfer.modules


import android.util.Log
import com.jakewharton.rxrelay2.BehaviorRelay
import com.kg.gettransfer.modules.http.HttpApi
import com.kg.gettransfer.modules.http.json.NewTransfer
import com.kg.gettransfer.modules.http.json.NewTransferField
import com.kg.gettransfer.modules.http.json.PassengerProfile
import com.kg.gettransfer.realm.Location
import com.kg.gettransfer.realm.Offer
import com.kg.gettransfer.realm.Transfer
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.realm.Realm
import io.realm.RealmResults
import org.koin.standalone.KoinComponent
import java.util.*


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
        val realm = Realm.getDefaultInstance()
        realm.executeTransaction {
            val transfers = realm.where(Transfer::class.java).findAllAsync()
            transfers.deleteAllFromRealm()
            Log.d(TAG, "realm.where(Transfer).deleteAll()")
        }
        realm.close()
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

                        val realm = Realm.getDefaultInstance()
                        realm.executeTransaction { realm ->
                            realm.copyToRealmOrUpdate(it.data?.transfers!!)
                            Log.d(TAG, "getTransfers() saved to realm")
                        }
                        realm.close()
                    } else {
                        Log.d(TAG, "getTransfers() responded fail, result = ${it.result}")
                    }
                }, {
                    busy.accept(false)
                    Log.d(TAG, "getTransfers() fail, ${it.message}")
                })
    }


    fun getOffers(t: Transfer): RealmResults<Offer>? =
//            realm.where(Offer::class.java).findAllAsync()
            t.offers.where().findAllAsync()


    fun updateOffers(id: Int) {
        api.getOffers(id)
                .subscribeOn(Schedulers.io())
//                .doOnSubscribe { busy.accept(true) }
                .observeOn(Schedulers.newThread())
                .subscribe({ response ->
                    busy.accept(false)
                    if (response.success) {
                        Log.d(TAG, "getOffers() responded success, N = ${response.data?.offers?.size}")

                        val offers = response.data?.offers ?: return@subscribe

                        val realm = Realm.getDefaultInstance()
                        realm.executeTransaction {
                            realm.copyToRealmOrUpdate(offers)

                            val transfer = realm
                                    .where(Transfer::class.java)
                                    .equalTo("id", id)
                                    .findFirst()
                                    ?: return@executeTransaction

                            transfer.load()

                            transfer.offers.clear()
                            transfer.offers.addAll(offers)

                            transfer.offersUpdatedAt = Date()

                            realm.insertOrUpdate(transfer)

                            Log.d(TAG, "getOffers() saved to realm")
                        }
                        realm.close()
                    } else {
                        Log.d(TAG, "getOffers() responded fail, result = ${response.result}")
                    }
                }, {
                    busy.accept(false)
                    Log.d(TAG, "getOffers() fail, ${it.message}")
                })
    }
}
