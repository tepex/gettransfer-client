package com.kg.gettransfer.modules


import com.jakewharton.rxrelay2.BehaviorRelay
import com.kg.gettransfer.modules.http.HttpApi
import com.kg.gettransfer.modules.http.json.Response
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
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

    private val brInfo: BehaviorRelay<Response<String>> = BehaviorRelay.create()

    var info: Response<String>?
        get() = brInfo.value
        private set(value) {
            brInfo.accept(value)
        }

    fun addOnInfoUpdated(f: ((Response<String>) -> Unit)): Disposable =
            brInfo.observeOn(AndroidSchedulers.mainThread()).subscribe(f)

    fun update() {
        val code = code
        if (code == null || code.isEmpty()) {
            busy = false
            info = Response()
        } else {
            disposables.add(
                    api.checkPromo(code)
                            .subscribeOn(Schedulers.io())
                            .doOnSubscribe { busy = true }
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(
                                    { response ->
                                        if (this.code == code) {
                                            busy = false
                                            info = response
                                        }
                                    },
                                    {
                                        if (this.code == code) {
                                            busy = false
                                            info = Response()
                                            err(it)
                                        }
                                    }))
        }
    }
}