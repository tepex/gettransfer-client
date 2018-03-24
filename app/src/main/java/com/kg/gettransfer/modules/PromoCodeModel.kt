package com.kg.gettransfer.modules


import com.jakewharton.rxrelay2.BehaviorRelay
import com.kg.gettransfer.modules.http.HttpApi
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import org.koin.standalone.KoinComponent


/**
 * Created by denisvakulenko on 12/03/2018.
 */


class PromoCodeModel(private val api: HttpApi) : AsyncModel(), KoinComponent {
    var code: String? = null
        set(value) {
            field = value
            update()
        }

    private val brInfo: BehaviorRelay<String> = BehaviorRelay.create()

    var info: String?
        get() = brInfo.value
        private set(value) {
            brInfo.accept(value)
        }

    fun addOnInfoUpdated(f: ((String) -> Unit)): Disposable =
            brInfo.observeOn(AndroidSchedulers.mainThread()).subscribe(f)

    fun update() {
        val code = code
        if (code?.isNotEmpty() == true) {
            api.checkPromo(code).fastSubscribe(
                    {
                        if (this.code == code) {
                            info = it
                        }
                    },
                    {
                        if (this.code == code) {
                            busy = false
                            info = String()
                            err(it.details)
                        }
                    })
        } else {
            busy = false
            info = String()
        }
    }
}