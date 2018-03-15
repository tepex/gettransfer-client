package com.kg.gettransfer.modules


import com.jakewharton.rxrelay2.BehaviorRelay
import com.kg.gettransfer.modules.http.HttpApi
import com.kg.gettransfer.modules.http.json.Payment
import com.kg.gettransfer.realm.Offer
import com.kg.gettransfer.realm.Transfer
import com.kg.gettransfer.realm.getTransfer
import com.kg.gettransfer.realm.getTransferAsync
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.realm.Realm
import io.realm.RealmResults
import org.koin.standalone.KoinComponent
import java.util.logging.Logger


/**
 * Created by denisvakulenko on 26/02/2018.
 */


// Not singleton
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

    fun addOnTransferUpdated(f: ((Transfer) -> Unit)) =
            disposables.add(
                    brTransfer.observeOn(AndroidSchedulers.mainThread()).subscribe(f))

    var transfer: Transfer?
        get() = brTransfer.value
        private set(value) {
            brTransfer.accept(value)
        }


    private val brTransfer: BehaviorRelay<Transfer> = BehaviorRelay.create()

    private val transferChangeListener: (RealmResults<Transfer>) -> Unit = { result ->
        log.info("getTransferFromRealmAsync() changed")
        if (result.size > 0) {
            if (result.isLoaded) {
                val resTransfer = result[0]
                if (resTransfer?.isLoaded == true) {
                    transfer = resTransfer
                }
            }
        } else {
            err("No transfer with id: $id")
            busy = false
        }
    }

    private var transferRealmResults: RealmResults<Transfer>? = null


    fun getOffersAsyncRealmResult(): RealmResults<Offer> =
            transfer!!.offers.where().findAllAsync()


    fun updateOffers() {
        if (busy || transfer == null) return
        api.getOffers(id).fastSubscribe { data ->
            log.info("getOffers() Success, offers N = ${data?.offers?.size}")

            val offers = data?.offers ?: return@fastSubscribe

            val realm = Realm.getDefaultInstance()
            realm.executeTransaction {
                val offersRealm = realm.copyToRealmOrUpdate(offers)

                val transfer = realm.getTransfer(id)
                if (transfer == null) {
                    err("No transfer with id: " + id)
                    return@executeTransaction
                }

                transfer.offers.clear()
                transfer.offers.addAll(offersRealm)

                //transfer.offersUpdatedAt = Date()

                realm.insertOrUpdate(transfer)

                log.info("getOffers() Offers saved to realm")
            }
            realm.close()
        }
    }


    fun update() {
        if (busy || transfer == null) return
        api.getTransfer(id).fastSubscribe { newTransfer ->
            if (newTransfer != null) {
                newTransfer.updateIsActive()

                val realm = Realm.getDefaultInstance()
                realm.executeTransaction {
                    realm.copyToRealmOrUpdate(newTransfer)
                }
                realm.close()
            } else {
                err("Received transfer is null")
            }
        }
    }


    fun cancel() {
        if (busy || transfer == null) return
        api.postCancelTransfer(id).fastSubscribe {
            val realm = Realm.getDefaultInstance()
            realm.executeTransaction {
                val transfer = realm.getTransfer(id)

                if (transfer == null) {
                    err(Exception("No transfer with id: " + id))
                    return@executeTransaction
                }

                transfer.status = "canceled"
                transfer.updateIsActive()
            }
            realm.close()
        }
    }


    fun restore() {
        if (busy || transfer == null) return
        api.postRestoreTransfer(id).fastSubscribe {
            val realm = Realm.getDefaultInstance()
            realm.executeTransaction {
                val transfer = realm.getTransfer(id)

                if (transfer == null) {
                    err("No transfer with id: " + id)
                    return@executeTransaction
                }

                transfer.status = "new"
                transfer.updateIsActive()
            }
            realm.close()
        }
    }


    fun payment(offerID: Int): Observable<com.kg.gettransfer.modules.http.json.Response<Payment>> {
        return api.payment(id, offerID, "platron", 30)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.newThread())
    }


    fun close() {
        transferRealmResults?.removeAllChangeListeners()
        disposables.clear()
        realm.close()
    }
}