package com.kg.gettransfer.modules


import android.util.Log
import com.kg.gettransfer.data.TransportType
import com.kg.gettransfer.network.Api
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.realm.Realm
import io.realm.RealmResults


/**
 * Created by denisvakulenko on 29/01/2018.
 */


class TransportTypes {
    private val TAG = "TransportTypes"


    private val api by lazy {
        Api.api
    }
    private val realm: Realm by lazy {
        Realm.getDefaultInstance()
    }
    var loading = false


    fun get(): RealmResults<TransportType> {
        val types = realm.where(TransportType::class.java).findAll()

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
                    Log.d(TAG, "getTransportTypes() responded")
                    if (r.success()) {
                        Log.d(TAG, "getTransportTypes() success, n = " + r.data?.size.toString())

                        realm.executeTransaction { realm ->
                            realm.copyToRealmOrUpdate(r.data)
                            Log.d(TAG, "Saved to realm")
                        }
                    } else {
                        Log.d(TAG, "getTransportTypes() failed, result = ${r.result}")
                    }
                }, { error ->
                    Log.d(TAG, "getTransportTypes() failed, ${error.message}")
                })
    }
}