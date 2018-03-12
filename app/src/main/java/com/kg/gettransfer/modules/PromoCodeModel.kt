package com.kg.gettransfer.modules


import com.jakewharton.rxrelay2.BehaviorRelay
import com.kg.gettransfer.modules.http.HttpApi
import com.kg.gettransfer.modules.http.json.Response
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import org.koin.standalone.KoinComponent
import java.util.logging.Logger


/**
 * Created by denisvakulenko on 12/03/2018.
 */


class PromoCodeModel(private val api: HttpApi)
    : AsyncModel(), KoinComponent {

    private val log = Logger.getLogger("PromoCodeModel")

    var code: String? = null
        set(value) {
            field = value
            update()
        }
    private val info: BehaviorRelay<Response<String>> = BehaviorRelay.create()

    fun addOnInfoUpdated(f: ((Response<String>) -> Unit)): Disposable =
            info.observeOn(AndroidSchedulers.mainThread()).subscribe(f)

    fun update() {
        val code = code
        if (code == null) {
            info.accept(Response())
        } else {
            info.accept(Response())
            disposables.add(
                    api.checkPromo(code)
                            .subscribeOn(Schedulers.io())
                            .doOnSubscribe { setBusy(true) }
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(
                                    { response ->
                                        log.info(response.toString())
                                        if (this.code == code) {
                                            setBusy(false)
                                            info.accept(response)
                                        }
                                    },
                                    {
                                        log.info(it.toString())
                                        if (this.code == code) {
                                            setBusy(false)
                                            err(it)
                                        }
                                    }))
        }
    }
}