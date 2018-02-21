package com.kg.gettransfer.modules


import com.jakewharton.rxrelay2.BehaviorRelay
import com.kg.gettransfer.modules.http.HttpApi
import com.kg.gettransfer.modules.http.json.NewTransfer
import com.kg.gettransfer.modules.http.json.NewTransferField
import com.kg.gettransfer.realm.Offer
import com.kg.gettransfer.realm.Transfer
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.realm.Realm
import io.realm.RealmResults
import org.koin.standalone.KoinComponent
import java.util.*
import java.util.logging.Logger


/**
 * Created by denisvakulenko on 30/01/2018.
 */


class Transfers(private val realm: Realm, private val api: HttpApi, currentAccount: CurrentAccount) : KoinComponent {
    private val log = Logger.getLogger("Transfers")

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
            val transfers = realm.where(Transfer::class.java).findAll()
            transfers.deleteAllFromRealm()
            log.info("realm.where(Transfer).deleteAll()")
        }
        realm.close()
    }


    fun createTransfer(transfer: NewTransfer): Observable<Transfer> =
            Observable.create<Transfer> {
                log.info("createTransfer()")
                api.postTransfer(NewTransferField(transfer))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({ r ->
                            if (r.success) {
                                log.info("createTransfer() responded success, id = ${r.data?.id}")
                                updateTransfers()
                                val t = Transfer()
                                t.id = r.data?.id ?: -1
                                it.onNext(t)
                            } else {
                                log.info("createTransfer() responded fail, result = ${r.result}")
                                it.onError(Exception(r.error?.message))
                            }
                        }, { error ->
                            log.info("createTransfer() fail, ${error.message}")
                            it.onError(error)
                        })
            }


    fun getAllAsync(): RealmResults<Transfer> =
            realm.where(Transfer::class.java).sort("dateTo").findAllAsync()


    fun getAsync(id: Int): RealmResults<Transfer> =
            realm.where(Transfer::class.java).equalTo("id", id).findAllAsync()


    fun updateTransfers() {
        log.info("updateTransfers()")
        if (busy.value) return
        log.info("getTransfers() call")
        api.getTransfers()
                .subscribeOn(Schedulers.io())
                .doOnSubscribe { busy.accept(true) }
                .observeOn(Schedulers.newThread())
                .subscribe({ response ->
                    busy.accept(false)
                    if (response.success) {
                        log.info("getTransfers() responded success, N = ${response.data?.transfers?.size}")
                        val transfers = response.data?.transfers ?: return@subscribe

                        val realm = Realm.getDefaultInstance()
                        realm.executeTransaction {
                            val transfers = realm.copyToRealmOrUpdate(transfers)
                            val ids = transfers
                                    .map { it.id }
                                    .toIntArray().toTypedArray()
                            val transfersToDelete = realm
                                    .where(Transfer::class.java)
                                    .not()
                                    .`in`("id", ids)
                                    .findAll()
                            transfersToDelete.deleteAllFromRealm()
                            log.info("getTransfers() saved to realm")
                        }
                        realm.close()
                    } else {
                        log.info("getTransfers() responded fail, result = ${response.result}")
                    }
                }, {
                    busy.accept(false)
                    log.info("getTransfers() fail, ${it.message}")
                })
    }


    fun getOffers(t: Transfer): RealmResults<Offer>? =
            t.offers.where().findAllAsync()


    fun updateOffers(id: Int) {
        api.getOffers(id)
                .subscribeOn(Schedulers.io())
//                .doOnSubscribe { busy.accept(true) }
                .observeOn(Schedulers.newThread())
                .subscribe({ response ->
                    busy.accept(false)
                    if (response.success) {
                        log.info("getOffers() responded success, N = ${response.data?.offers?.size}")

                        val offers = response.data?.offers ?: return@subscribe

                        val realm = Realm.getDefaultInstance()
                        realm.executeTransaction {
                            val offers = realm.copyToRealmOrUpdate(offers)

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

                            log.info("getOffers() saved to realm")
                        }
                        realm.close()
                    } else {
                        log.info("getOffers() responded fail, result = ${response.result}")
                    }
                }, {
                    busy.accept(false)
                    log.info("getOffers() fail, ${it.message}")
                })
    }


    fun close() {
        realm.close()
    }
}
