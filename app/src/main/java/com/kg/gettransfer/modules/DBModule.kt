package com.kg.gettransfer.modules


import android.content.Context
import android.util.Log
import com.kg.gettransfer.RxBus
import io.realm.Realm
import io.realm.RealmConfiguration


/**
 * Created by denisvakulenko on 30/01/2018.
 */


object DBModule {
    fun create(applicationContext: Context): Realm {
        Realm.init(applicationContext)

        val config: RealmConfiguration = RealmConfiguration.Builder()
                .name("db")
                .schemaVersion(1)
                .deleteRealmIfMigrationNeeded()
                .build()

        Realm.setDefaultConfiguration(config)

        return Realm.getDefaultInstance()
    }
}
