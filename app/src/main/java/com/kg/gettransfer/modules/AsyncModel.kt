package com.kg.gettransfer.modules


import com.jakewharton.rxrelay2.BehaviorRelay
import com.jakewharton.rxrelay2.PublishRelay
import com.kg.gettransfer.modules.http.json.Response
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers


/**
 * Created by denisvakulenko on 28/02/2018.
 */


open class AsyncModel {
    protected val disposables = CompositeDisposable()

    private val busy = BehaviorRelay.createDefault<Boolean>(false)!!
    private val errors = PublishRelay.create<Throwable>()!!


    public val isBusy: Boolean get() = busy.value


    public fun addOnBusyChanged(f: ((Boolean) -> Unit)): Disposable {
        val d = busy.observeOn(AndroidSchedulers.mainThread()).subscribe(f)
        disposables.add(d)
        return d
    }

    public fun addOnError(f: ((Throwable) -> Unit)): Disposable {
        val d = errors.observeOn(AndroidSchedulers.mainThread()).subscribe(f)
        disposables.add(d)
        return d
    }


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


    protected fun <K : Any, T : Response<K>>
            Observable<T>.fastSubscribe(f: ((data: K?) -> Unit)): Disposable {
        val d = this
                .subscribeOn(Schedulers.io()).doOnSubscribe { setBusy(true) }
                .observeOn(Schedulers.newThread()).doFinally { setBusy(false) }
                .subscribe(
                        { response ->
                            if (response.success) {
                                f(response.data)
                            } else {
                                err(response)
                            }
                        },
                        { e ->
                            err(e)
                        })
        disposables.add(d)
        return d
    }
}