package com.kg.gettransfer.modules


import android.os.Looper
import android.util.Log
import com.jakewharton.rxrelay2.BehaviorRelay
import com.kg.gettransfer.modules.http.HttpApi
import com.kg.gettransfer.realm.Offer
import com.kg.gettransfer.realm.Transfer
import com.kg.gettransfer.realm.getTransfer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.realm.Realm
import io.realm.RealmResults
import org.koin.standalone.KoinComponent


/**
 * Created by denisvakulenko on 22/03/2018.
 */


// Not singleton
class OffersModel(
        private val realm: Realm,
        private val api: HttpApi)
    : AsyncModel(), KoinComponent {

    var transferID: Int = -1
        set(id) {
            if (id < 0) throw Exception("Invalid id")
            if (Thread.currentThread() != Looper.getMainLooper().thread) throw Exception("Non UI thread")

            if (::managedTransfer.isInitialized) managedTransfer.removeAllChangeListeners()

            managedTransfer = realm.getTransfer(id) ?: throw Exception("No transfer with id: $id")
            managedTransfer.addChangeListener<Transfer> { transfer, _ ->
                if (transfer.isValid) {
                    if (transfer.needAndCanUpdateOffers) update()
                }
            }

            field = id

            offers = getOffersAsyncRealmResult(managedTransfer)

            if (managedTransfer.needAndCanUpdateOffers) {
                update()
            }
        }

    fun addOnOffersUpdated(f: (RealmResults<Offer>) -> Unit): Disposable {
        val d = brOffers
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(f)
        disposables.add(d)
        return d
    }

    private lateinit var managedTransfer: Transfer

    private fun getOffersAsyncRealmResult(managedTransfer: Transfer): RealmResults<Offer> =
            managedTransfer.offers.where().findAllAsync()

    var offers: RealmResults<Offer>
        get() = brOffers.value
        private set(value) {
            brOffers.accept(value)
        }

    private val brOffers: BehaviorRelay<RealmResults<Offer>> = BehaviorRelay.create()

    fun update() {
        if (transferID < 0) return
        val id = transferID
        api.getOffers(id).fastSubscribe({
            val offers = it.offers

            val realm = Realm.getDefaultInstance()
            realm.executeTransaction {
                val offersRealm = realm.copyToRealmOrUpdate(offers)

                val transfer = realm.getTransfer(id)
                if (transfer == null) {
                    err("No transfer with id: $id")
                    return@executeTransaction
                }

                transfer.offers.clear()
                transfer.offers.addAll(offersRealm)

                if (transfer.offersCount != offers.count()) {
                    Log.e("OffersModel", "transfer.offersCount != offers.count()")
                    transfer.offersCount = offers.count()
                }

                transfer.offersUpdatedDate = System.currentTimeMillis()
                transfer.offersTriedToUpdateDate = System.currentTimeMillis()
            }
            realm.close()
        }, {
            val realm = Realm.getDefaultInstance()
            realm.executeTransaction {
                val transfer = realm.getTransfer(id)
                if (transfer == null) {
                    err("No transfer with id: $id")
                    return@executeTransaction
                }
                transfer.offersTriedToUpdateDate = System.currentTimeMillis()
            }
            realm.close()
            err(it.message)
        })
    }
}