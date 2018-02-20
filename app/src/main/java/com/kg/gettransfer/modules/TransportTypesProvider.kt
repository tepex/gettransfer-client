package com.kg.gettransfer.modules


import com.kg.gettransfer.modules.http.HttpApi
import com.kg.gettransfer.realm.TransportType
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

    @Volatile
    private var loading = false


    fun get(): RealmResults<TransportType> {
        val types = realm.where(TransportType::class.java).findAllAsync()

        types.addChangeListener { t, changeSet ->
            if (types.size == 0) {
                load()
            }
        }

        return types
    }


    fun getNames(ids: List<Integer?>?): String {
        if (ids == null) return "-"

        var s = ""
        val types = realm.where(TransportType::class.java).findAll()

        types.asSequence()
                .filter { ids.contains(it.id as Integer) }
                .forEach { s += ", " + it.title }

        return if (s.isEmpty()) "-" else s.substring(2)
    }


    private fun load() {
        loading = true
        api.getTransportTypes()
                .subscribeOn(Schedulers.io())
                //.observeOn(AndroidSchedulers.mainThread())
                .observeOn(Schedulers.newThread())
                .subscribe({ r ->
                    log.info("getTransportTypes() responded")
                    if (r.success) {
                        log.info("getTransportTypes() success, n = " + r.data?.size.toString())

                        Realm.getDefaultInstance().executeTransaction { realm ->
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