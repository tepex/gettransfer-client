package com.kg.gettransfer.modules


import com.jakewharton.rxrelay2.BehaviorRelay
import com.kg.gettransfer.modules.http.HttpApi
import com.kg.gettransfer.realm.Offer
import com.kg.gettransfer.realm.Transfer
import com.kg.gettransfer.realm.getTransfer
import com.kg.gettransfer.realm.getTransferAsync
import io.reactivex.schedulers.Schedulers
import io.realm.Realm
import io.realm.RealmResults
import org.koin.standalone.KoinComponent
import java.util.logging.Logger


/**
 * Created by denisvakulenko on 26/02/2018.
 */


class TransferModel(
        private val realm: Realm,
        private val api: HttpApi)
    : AsyncModel(), KoinComponent {

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


    private val transferChangeListener: (RealmResults<Transfer>) -> Unit = { result ->
        log.info("getTransferFromRealmAsync() changed")
        if (result.size > 0) {
            if (result.isLoaded) {
                val resTransfer = result[0]
                if (resTransfer?.isLoaded == true) {
                    transfer.accept(resTransfer)
                }
            }
        } else {
            err("Lost transfer with id: " + id)
            setBusy(false)
        }
    }

    private var transferRealmResults: RealmResults<Transfer>? = null


    fun getOffersAsyncRealmResult(): RealmResults<Offer> =
            transfer.value.offers.where().findAllAsync()


    fun updateOffers() {
        if (isBusy || transfer.value == null) return
        api.getOffers(id)
                .subscribeOn(Schedulers.io())
                .doOnSubscribe { setBusy(true) }
                .observeOn(Schedulers.newThread())
                .doFinally { setBusy(false) }
                .subscribe(
                        { response ->
                            if (response.success) {
                                log.info("getOffersAsyncRealmResult() responded success, N = ${response.data?.offers?.size}")

                                val offers = response.data?.offers ?: return@subscribe

                                val realm = Realm.getDefaultInstance()
                                realm.executeTransaction {
                                    val offersRealm = realm.copyToRealmOrUpdate(offers)

                                    val transfer = realm.getTransfer(id)
                                    if (transfer == null) {
                                        err("Lost transfer with id: " + id)
                                        return@executeTransaction
                                    }

                                    transfer.offers.clear()
                                    transfer.offers.addAll(offersRealm)

                                    //transfer.offersUpdatedAt = Date()

                                    realm.insertOrUpdate(transfer)

                                    log.info("getOffersAsyncRealmResult() saved to realm")
                                }
                                realm.close()
                            } else {
                                log.info("getOffersAsyncRealmResult() responded fail, result = ${response.result}")
                                err(response)
                            }
                        },
                        { err(it) })
    }


    fun update() {
        if (isBusy || transfer.value == null) return
        api.getTransfer(id)
                .subscribeOn(Schedulers.io())
                .doOnSubscribe { setBusy(true) }
                .observeOn(Schedulers.newThread())
                .doFinally { setBusy(false) }
                .subscribe(
                        { response ->
                            if (response.success) {
                                val transfer = response.data
                                if (transfer != null) {
                                    transfer.updateIsActive()
                                    val realm = Realm.getDefaultInstance()
                                    realm.executeTransaction {
                                        realm.copyToRealmOrUpdate(transfer)
                                    }
                                    realm.close()
                                } else err("Null transfer")
                            } else err(response)
                        },
                        { err(it) })
    }


    fun cancel() {
        if (isBusy || transfer.value == null) return
        api.postCancelTransfer(id)
                .subscribeOn(Schedulers.io())
                .doOnSubscribe { setBusy(true) }
                .observeOn(Schedulers.newThread())
                .doFinally { setBusy(false) }
                .subscribe(
                        { response ->
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
                                err(response)
                            }
                        },
                        { err(it) })
    }


    fun restore() {
        if (isBusy || transfer.value == null) return
        api.postRestoreTransfer(id)
                .subscribeOn(Schedulers.io())
                .doOnSubscribe { setBusy(true) }
                .observeOn(Schedulers.newThread())
                .doFinally { setBusy(false) }
                .subscribe(
                        { response ->
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
                                err(response)
                            }
                        },
                        { err(it) })
    }


    fun close() {
        transferRealmResults?.removeAllChangeListeners()
        realm.close()
    }
}