package com.kg.gettransfer.modules


import com.jakewharton.rxrelay2.BehaviorRelay
import com.kg.gettransfer.modules.http.HttpApi
import com.kg.gettransfer.modules.http.json.BaseResponse
import com.kg.gettransfer.modules.http.json.Payment
import com.kg.gettransfer.modules.http.json.TransferField
import com.kg.gettransfer.realm.Transfer
import com.kg.gettransfer.realm.getTransfer
import com.kg.gettransfer.realm.getTransferAsync
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.realm.Realm
import io.realm.RealmResults
import org.koin.standalone.KoinComponent


/**
 * Created by denisvakulenko on 26/02/2018.
 */


// Not singleton
class TransferModel(
        private val realm: Realm,
        private val api: HttpApi)
    : AsyncModel(), KoinComponent {

    var id: Int = -1
        set(value) {
            field = value

            transferRealmResults?.removeAllChangeListeners()

            val transferRealmResults = realm.getTransferAsync(value)
            transferRealmResults.addChangeListener(transferChangeListener)

            this.transferRealmResults = transferRealmResults
        }

    fun addOnTransferUpdated(f: (Transfer) -> Unit): Disposable {
        val d = brTransfer
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(f)
        disposables.add(d)
        return d
    }

    var transfer: Transfer?
        get() = brTransfer.value
        private set(value) {
            brTransfer.accept(value)
        }

    private val brTransfer: BehaviorRelay<Transfer> = BehaviorRelay.create()

    private val transferChangeListener: (RealmResults<Transfer>) -> Unit = { result ->
        if (result.size > 0) {
            if (result.isLoaded) {
                val managedTransfer = result[0]
                if (managedTransfer?.isLoaded == true && managedTransfer.isValid) {
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


    fun update() = invoke(api::getTransfer)

    fun cancel() = invoke(api::postCancelTransfer)

    fun restore() = invoke(api::postRestoreTransfer)


    fun invoke(function: (id: Int) -> Observable<BaseResponse<TransferField>>) {
        if (busy || id < 0) return
        function(id).fastSubscribe {
            save(it.transfer)
        }
    }


    // Non UI thread
    private fun save(newTransfer: Transfer?) {
        if (newTransfer != null && newTransfer.idValid) {
            val id = newTransfer.id

            val realm = Realm.getDefaultInstance()

            newTransfer.update()
            newTransfer.populateFromOldTransfer(realm.getTransfer(id))

            realm.executeTransaction {
                realm.copyToRealmOrUpdate(newTransfer)
            }

            realm.close()
        } else {
            err("Received transfer is null")
        }
    }


    fun payment(offerID: Int): Observable<com.kg.gettransfer.modules.http.json.BaseResponse<Payment>> {
        return api.payment(id, offerID, "platron", 30)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }


    override fun stop() {
        transferRealmResults?.removeAllChangeListeners()
        disposables.clear()
        realm.close()
        super.stop()
    }
}