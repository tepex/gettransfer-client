package com.kg.gettransfer.modules


import com.jakewharton.rxrelay2.BehaviorRelay
import com.kg.gettransfer.modules.http.HttpApi
import com.kg.gettransfer.modules.http.json.Config
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import org.koin.standalone.KoinComponent


/**
 * Created by denisvakulenko on 07/03/2018.
 */


class ConfigModel(
        private val api: HttpApi)
    : AsyncModel(), KoinComponent {

    init {
        update()
    }

    private val brConfig: BehaviorRelay<Config> = BehaviorRelay.create()

    fun addOnConfigUpdated(f: (Config) -> Unit): Disposable =
            brConfig.observeOn(AndroidSchedulers.mainThread()).subscribe(f)

    val config: Config?
        get() = brConfig.value

    fun updateIfNull() {
        if (config == null) update()
    }

    fun update() {
        if (busy) return
        disposables.add(
                api.getConfig().fastSubscribe {
                    //it.copyToRealmOrUpdate()
                    brConfig.accept(it)
                })
    }
}