package com.kg.gettransfer.modules


import android.os.Looper
import com.jakewharton.rxrelay2.BehaviorRelay
import com.kg.gettransfer.modules.http.HttpApi
import com.kg.gettransfer.realm.Offer
import com.kg.gettransfer.realm.Transfer
import com.kg.gettransfer.realm.getTransfer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.realm.Realm
import io.realm.RealmResults
import org.koin.standalone.KoinComponent
import java.util.*


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
            if (Thread.currentThread() != Looper.getMainLooper().thread) {
                throw Exception("Non UI thread")
            }

            val transfer = realm.getTransfer(id) ?: throw Exception("No transfer with id: $id")

            field = id

            offers = getOffersAsyncRealmResult(transfer)

            if (transfer.offersOutdated) update()
        }

    var offers: RealmResults<Offer>
        get() = brOffers.value
        private set(value) {
            brOffers.accept(value)
        }

    fun addOnOffersUpdated(f: ((RealmResults<Offer>) -> Unit)) =
            disposables.add(
                    brOffers.observeOn(AndroidSchedulers.mainThread()).subscribe(f))

    private fun getOffersAsyncRealmResult(managedTransfer: Transfer): RealmResults<Offer> =
            managedTransfer.offers.where().findAllAsync()

    private val brOffers: BehaviorRelay<RealmResults<Offer>> = BehaviorRelay.create()

    fun update() {
        if (busy || transferID < 0) return
        val id = transferID
        api.getOffers(id).fastSubscribe { data ->
            val offers = data?.offers ?: return@fastSubscribe

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

                transfer.offersUpdatedDate = Date()

                realm.insertOrUpdate(transfer)
            }
            realm.close()
        }
    }
}