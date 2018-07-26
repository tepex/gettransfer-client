package com.kg.gettransfer.module


import android.content.Context
import io.realm.Realm
import io.realm.RealmConfiguration
import org.koin.standalone.KoinComponent


/**
 * Created by denisvakulenko on 30/01/2018.
 */


class DBProvider(applicationContext: Context) : KoinComponent {
    init {
        Realm.init(applicationContext)
        val config: RealmConfiguration = RealmConfiguration.Builder()
                .name("db")
                .schemaVersion(1)
                .deleteRealmIfMigrationNeeded()
                .build()
        Realm.setDefaultConfiguration(config)
    }

    fun create(): Realm {
        return Realm.getDefaultInstance()
    }
}
