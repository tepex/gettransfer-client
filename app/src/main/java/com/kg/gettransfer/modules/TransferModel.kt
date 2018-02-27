package com.kg.gettransfer.modules


import android.util.Log
import com.jakewharton.rxrelay2.BehaviorRelay
import com.jakewharton.rxrelay2.PublishRelay
import com.kg.gettransfer.modules.http.HttpApi
import com.kg.gettransfer.realm.Offer
import com.kg.gettransfer.realm.Transfer
import com.kg.gettransfer.realm.getTransfer
import com.kg.gettransfer.realm.getTransferAsync
import io.reactivex.schedulers.Schedulers
import io.realm.Realm
import io.realm.RealmResults
import org.koin.standalone.KoinComponent
import java.util.*
import java.util.logging.Logger


/**
 * Created by denisvakulenko on 26/02/2018.
 */


class TransferModel(
        private val realm: Realm,
        private val api: HttpApi) : KoinComponent {

    private val log = Logger.getLogger("TransferModel")

    var id: Int = -1
        set(value) {
            field = value

            transferRealmResults?.removeAllChangeListeners()

            val transferRealmResults = realm.getTransferAsync(value)
            transferRealmResults.addChangeListener(transferChangeListener)

            this.transferRealmResults = transferRealmResults
        }

    val transfer: BehaviorRelay<Transfer> = BehaviorRelay.create()
    val busy = BehaviorRelay.createDefault<Boolean>(false)!!
    val errors = PublishRelay.create<Throwable>()!!


    private fun err(e: Throwable) {
        errors.accept(e)
    }

    private fun err(msg: String?) {
        errors.accept(Exception(msg))
    }


    private val transferChangeListener: (RealmResults<Transfer>) -> Unit = { result ->
        Log.i("TransferActivity", "getTransferFromRealmAsync() changed")
        if (result.size > 0) {
            if (result.isLoaded) {
                val resTransfer = result[0]
                if (resTransfer?.isLoaded == true) {
                    transfer.accept(resTransfer)
                }
            }
        } else {
            err("Lost transfer with id: " + id)
            busy.accept(false)
        }
    }

    private var transferRealmResults: RealmResults<Transfer>? = null


    fun getOffers(): RealmResults<Offer> =
            transfer.value.offers.where().findAllAsync()


    fun updateOffers() {
        if (busy.value || transfer.value == null) return
        api.getOffers(id)
                .subscribeOn(Schedulers.io())
                .doOnSubscribe { busy.accept(true) }
                .observeOn(Schedulers.newThread())
                .subscribe({ response ->
                    if (response.success) {
                        log.info("getOffers() responded success, N = ${response.data?.offers?.size}")

                        val offers = response.data?.offers ?: return@subscribe

                        val realm = Realm.getDefaultInstance()
                        realm.executeTransaction {
                            val offers = realm.copyToRealmOrUpdate(offers)

                            val transfer = realm.getTransfer(id)

                            if (transfer == null) {
                                err("Lost transfer with id: " + id)
                                return@executeTransaction
                            }

                            transfer.offers.clear()
                            transfer.offers.addAll(offers)

                            //transfer.offersUpdatedAt = Date()

                            realm.insertOrUpdate(transfer)

                            log.info("getOffers() saved to realm")
                        }
                        realm.close()
                    } else {
                        log.info("getOffers() responded fail, result = ${response.result}")
                        err(response.error?.message)
                    }
                    busy.accept(false)
                }, {
                    err(it)
                    busy.accept(false)
                })
    }


    fun cancelTransfer() {
        if (busy.value || transfer.value == null) return
        api.postCancelTransfer(id)
                .subscribeOn(Schedulers.io())
                .doOnSubscribe { busy.accept(true) }
                .observeOn(Schedulers.newThread())
                .subscribe({ response ->
                    if (response.success) {
//                        transfer = response.data

                        val realm = Realm.getDefaultInstance()
                        realm.executeTransaction {
                            val transfer = realm.getTransfer(id)

                            if (transfer == null) {
                                err(Exception("Lost transfer with id: " + id))
                                return@executeTransaction
                            }

                            transfer.status = "canceled"
                            transfer.updateIsActive()
                        }
                        realm.close()
                    } else {
                        err(response.error?.message)
                    }
                    busy.accept(false)
                }, {
                    err(it)
                    busy.accept(false)
                })
    }


    fun restoreTransfer() {
        if (busy.value || transfer.value == null) return
        api.postRestoreTransfer(id)
                .subscribeOn(Schedulers.io())
                .doOnSubscribe { busy.accept(true) }
                .observeOn(Schedulers.newThread())
                .subscribe({ response ->
                    if (response.success) {
//                        transfer = response.data

                        val realm = Realm.getDefaultInstance()
                        realm.executeTransaction {
                            val transfer = realm.getTransfer(id)

                            if (transfer == null) {
                                err("Lost transfer with id: " + id)
                                return@executeTransaction
                            }

                            transfer.status = "new"
                            transfer.updateIsActive()
                        }
                        realm.close()
                    } else {
                        err(response.error?.message)
                    }
                    busy.accept(false)
                }, {
                    err(it)
                    busy.accept(false)
                })
    }


    fun close() {
        transferRealmResults?.removeAllChangeListeners()
        realm.close()
    }
}