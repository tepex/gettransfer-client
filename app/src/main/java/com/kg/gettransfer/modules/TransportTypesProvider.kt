package com.kg.gettransfer.modules


import com.kg.gettransfer.realm.TransportType
import com.kg.gettransfer.modules.http.HttpApi
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.realm.Realm
import io.realm.RealmResults
import org.koin.standalone.KoinComponent
import java.util.logging.Logger


/**
 * Created by denisvakulenko on 29/01/2018.
 */


class TransportTypesProvider(val realm: Realm, val api: HttpApi) : KoinComponent {
    private val log = Logger.getLogger("TransportTypesProvider")

    private var loading = false


    fun get(): RealmResults<TransportType> {
        val types = realm.where(TransportType::class.java).findAllAsync()

        if (types.size == 0) {
            load()
        }

        return types
    }


    private fun load() {
        loading = true
        api.getTransportTypes()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ r ->
                    log.info("getTransportTypes() responded")
                    if (r.success) {
                        log.info("getTransportTypes() success, n = " + r.data?.size.toString())

                        realm.executeTransaction { realm ->
                            realm.where(TransportType::class.java)
                                    .findAll()
                                    .deleteAllFromRealm()

                            realm.copyToRealmOrUpdate(r.data)

                            log.info("Saved to realm")
                        }
                    } else {
                        log.info("getTransportTypes() failed, result = ${r.result}")
                    }
                }, { error ->
                    log.info("getTransportTypes() failed, ${error.message}")
                })
    }
}