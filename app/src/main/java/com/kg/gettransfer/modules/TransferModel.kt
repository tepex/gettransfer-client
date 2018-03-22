package com.kg.gettransfer.modules


import com.jakewharton.rxrelay2.BehaviorRelay
import com.kg.gettransfer.modules.http.HttpApi
import com.kg.gettransfer.modules.http.json.Payment
import com.kg.gettransfer.realm.Transfer
import com.kg.gettransfer.realm.getTransfer
import com.kg.gettransfer.realm.getTransferAsync
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.realm.Realm
import io.realm.RealmResults
import org.koin.standalone.KoinComponent
import java.util.*
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

    private var managedTransfer: Transfer? = null

    private val transferChangeListener: (RealmResults<Transfer>) -> Unit = { result ->
        log.info("getTransferFromRealmAsync() changed")
        if (result.size > 0) {
            if (result.isLoaded) {
                managedTransfer?.removeAllChangeListeners()
                val managedTransfer = result[0]
                if (managedTransfer?.isLoaded == true && managedTransfer.isValid) {
//                    managedTransfer.addChangeListener<Transfer> { t ->
//                        transfer = if (t.isValid) realm.copyFromRealm(t) else Transfer()
//                    }
                    transfer = realm.copyFromRealm(managedTransfer)
                }
            }
        } else {
            err("No transfer with id: $id")
            busy = false
        }
    }

    private var transferRealmResults: RealmResults<Transfer>? = null


    fun updateIfOld() {
        if (transfer?.justUpdated == false) update()
    }


    fun update() {
        if (busy || id < 0) return
        api.getTransfer(id).fastSubscribe {
            save(it?.transfer)
        }
    }


    fun cancel() {
        if (busy || id < 0) return
        api.postCancelTransfer(id).fastSubscribe {
            save(it?.transfer)
        }
    }


    fun restore() {
        if (busy || id < 0) return
        api.postRestoreTransfer(id).fastSubscribe {
            save(it?.transfer)
        }
    }


    // Non UI thread
    private fun save(newTransfer: Transfer?) {
        if (newTransfer != null && newTransfer.idValid) {
            val id = newTransfer.id

            val realm = Realm.getDefaultInstance()

            newTransfer.update()

            val oldTransfer = realm.getTransfer(id)
            if (oldTransfer != null) {
                newTransfer.offersUpdatedDate = oldTransfer.offersChangedDate
                newTransfer.offers = oldTransfer.offers
            }

            val offersChangedDate = newTransfer.offersChangedDate
            val offersOutdated = newTransfer.offersOutdated

            realm.executeTransaction {
                realm.copyToRealmOrUpdate(newTransfer)
            }

            realm.close()
        } else {
            err("Received transfer is null")
        }
    }


    fun payment(offerID: Int): Observable<com.kg.gettransfer.modules.http.json.Response<Payment>> {
        return api.payment(id, offerID, "platron", 30)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }


    fun close() {
        transferRealmResults?.removeAllChangeListeners()
        disposables.clear()
        realm.close()
    }
}