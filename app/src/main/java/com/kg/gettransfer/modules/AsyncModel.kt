package com.kg.gettransfer.modules


import com.jakewharton.rxrelay2.BehaviorRelay
import com.jakewharton.rxrelay2.PublishRelay
import com.kg.gettransfer.modules.http.json.Response
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable


/**
 * Created by denisvakulenko on 28/02/2018.
 */


open class AsyncModel {
    private val busy = BehaviorRelay.createDefault<Boolean>(false)!!
    private val errors = PublishRelay.create<Throwable>()!!


    public val isBusy: Boolean get() = busy.value


    public fun addOnBusyChanged(f: ((Boolean) -> Unit)): Disposable =
            busy.observeOn(AndroidSchedulers.mainThread()).subscribe(f)

    public fun addOnError(f: ((Throwable) -> Unit)): Disposable =
            errors.observeOn(AndroidSchedulers.mainThread()).subscribe(f)


    protected fun setBusy(busy: Boolean) {
        this.busy.accept(busy)
    }


    protected fun err(e: Throwable) {
        errors.accept(e)
    }

    protected fun err(msg: String?) {
        errors.accept(Exception(msg))
    }

    protected fun <T : Any> err(response: Response<T>) {
        errors.accept(Exception(response.error?.message))
    }
}