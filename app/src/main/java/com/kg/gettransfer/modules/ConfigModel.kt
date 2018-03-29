package com.kg.gettransfer.modules


import android.util.Log
import com.jakewharton.rxrelay2.BehaviorRelay
import com.kg.gettransfer.modules.http.HttpApi
import com.kg.gettransfer.realm.Config
import com.kg.gettransfer.realm.copyToRealmOrUpdate
import io.reactivex.disposables.Disposable
import io.realm.Realm
import org.koin.standalone.KoinComponent


/**
 * Created by denisvakulenko on 07/03/2018.
 */


class ConfigModel(private val api: HttpApi)
    : AsyncModel(), KoinComponent {

    private val brConfig: BehaviorRelay<Config> = BehaviorRelay.create()

    init {
        val realm = Realm.getDefaultInstance()
        val configAsync = realm.where(Config::class.java).findFirstAsync()
        configAsync.addChangeListener<Config> { config ->
            configAsync.removeAllChangeListeners()
            if (config.isValid) brConfig.accept(realm.copyFromRealm(config))
            else update()
        }

        addOnError { Log.e("ConfigModel", it.toString()) }
    }

    fun addOnConfigUpdated(f: (Config) -> Unit): Disposable = brConfig.subscribeUIThread(f)

    val config: Config?
        get() = brConfig.value

    fun updateIfNull() {
        if (config == null) update()
    }

    fun update() {
        if (busy) return
        api.getConfig().fastSubscribe {
            it.copyToRealmOrUpdate()
            brConfig.accept(it)
        }
    }
}