package com.kg.gettransfer.modules


import android.view.View
import com.jakewharton.rxrelay2.BehaviorRelay
import com.jakewharton.rxrelay2.PublishRelay
import com.kg.gettransfer.modules.http.json.Response
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_account.*


/**
 * Created by denisvakulenko on 28/02/2018.
 */


open class AsyncModel {
    protected val disposables = CompositeDisposable()

    private val brBusy = BehaviorRelay.createDefault<Boolean>(false)!!
    private val prErrors = PublishRelay.create<Throwable>()!!


    public var busy: Boolean
        get() = brBusy.value
        protected set(value) {
            brBusy.accept(value)
        }


    public fun addOnBusyChanged(f: ((Boolean) -> Unit)): Disposable {
        val d = brBusy.observeOn(AndroidSchedulers.mainThread()).subscribe(f)
        disposables.add(d)
        return d
    }

    public fun addOnBusyProgressBar(pb: View, nonBusyVisibility: Int = View.GONE): Disposable {
        val d = brBusy.observeOn(AndroidSchedulers.mainThread()).subscribe{
            pb.visibility = if (it) View.VISIBLE else nonBusyVisibility
        }
        disposables.add(d)
        return d
    }

    public fun addOnError(f: ((Throwable) -> Unit)): Disposable {
        val d = prErrors.observeOn(AndroidSchedulers.mainThread()).subscribe(f)
        disposables.add(d)
        return d
    }


    protected fun err(e: Throwable) {
        prErrors.accept(e)
    }

    protected fun err(msg: String?) {
        prErrors.accept(Exception(msg))
    }

    protected fun <T : Any> err(response: Response<T>) {
        prErrors.accept(Exception(response.error?.message))
    }


    protected fun <K : Any, T : Response<K>>
            Observable<T>.fastSubscribe(f: ((data: K?) -> Unit)): Disposable {
        val d = this
                .subscribeOn(Schedulers.io()).doOnSubscribe { busy = true }
                .observeOn(Schedulers.newThread()).doFinally { busy = false }
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


    open fun stop() {
        disposables.clear()
    }
}