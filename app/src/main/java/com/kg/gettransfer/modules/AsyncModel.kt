package com.kg.gettransfer.modules


import android.content.Context
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.gson.Gson
import com.jakewharton.rxrelay2.BehaviorRelay
import com.jakewharton.rxrelay2.PublishRelay
import com.kg.gettransfer.modules.http.json.BaseResponse
import com.kg.gettransfer.modules.http.json.Error
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import retrofit2.Response


/**
 * Created by denisvakulenko on 28/02/2018.
 */


open class AsyncModel {
    fun CompositeDisposable.add(d: Disposable?) {
        if (d != null) add(d)
    }

    protected val disposables = CompositeDisposable()

    private val brBusy = BehaviorRelay.createDefault<Boolean>(false)!!
    private val prErrors = PublishRelay.create<Throwable>()!!


    var busy: Boolean
        get() = brBusy.value
        protected set(value) {
            brBusy.accept(value)
        }


    fun addOnBusyChanged(f: ((Boolean) -> Unit)): Disposable {
        return brBusy.subscribeUIThread(f)
    }

    fun addOnBusyProgressBar(pb: View, nonBusyVisibility: Int = View.INVISIBLE): Disposable {
        return brBusy.subscribeUIThread {
            pb.visibility = if (it) View.VISIBLE else nonBusyVisibility
        }
    }

    fun addOnError(f: (Throwable) -> Unit): Disposable {
        return prErrors.subscribeUIThread(f)
    }

    @Deprecated("Only for debug")
    fun toastOnError(context: Context): Disposable {
        return prErrors.subscribeUIThread {
            Log.e(this.javaClass.name, it.toString())
            try {
                Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
            }
        }
    }

    fun toastOnError(context: Context, msg: String): Disposable {
        return prErrors.subscribeUIThread {
            Log.e(this.javaClass.name, it.toString())
            try {
                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
            }
        }
    }


    protected fun err(e: Throwable) {
        prErrors.accept(e)
    }

    protected fun err(msg: String?) {
        prErrors.accept(Exception(msg))
    }

    protected fun <T : Any> err(response: BaseResponse<T>) {
        prErrors.accept(Exception(response.error?.message))
    }


    protected fun <T : Any>
            Observable<T>.subscribeUIThread(f: (data: T) -> Unit): Disposable {
        return subscribe(f, true)
    }

    protected fun <T : Any>
            Observable<T>.subscribe(f: (data: T) -> Unit,
                                    uiThread: Boolean): Disposable {
        val d = this
                .observeOn(if (uiThread) AndroidSchedulers.mainThread() else Schedulers.newThread())
                .subscribe(f, { e -> err(e) })
        disposables.add(d)
        return d
    }


    protected fun <K : Any, T : BaseResponse<K>>
            Observable<T>.fastSubscribe(f: (data: K) -> Unit): Disposable? {
        return fastSubscribe(f, false)
    }

    protected fun <K : Any, T : BaseResponse<K>>
            Observable<T>.fastSubscribe(f: (data: K) -> Unit,
                                        uiThread: Boolean): Disposable? {
        synchronized(busy) {
            if (!busy) busy = true
            else return null
        }
        val d = this
                .subscribeOn(Schedulers.io())
                .observeOn(if (uiThread) AndroidSchedulers.mainThread() else Schedulers.newThread())
                .doFinally { synchronized(busy) { busy = false } }
                .subscribe(
                        { response ->
                            if (response.success) {
                                val data = response.data
                                if (data != null) {
                                    f(data)
                                } else err("Response data is null")
                            } else err(response)
                        },
                        { e -> err(e) })
        disposables.add(d)
        return d
    }


    protected fun <K : Any, T : BaseResponse<K>>
            Observable<Response<T>>.fastSubscribe(f: (data: K) -> Unit,
                                                  fError: (data: Error) -> Unit): Disposable? {
        return fastSubscribe(f, fError, false)
    }

    protected fun <K : Any, T : BaseResponse<K>>
            Observable<Response<T>>.fastSubscribe(f: (data: K) -> Unit,
                                                  fError: (data: Error) -> Unit,
                                                  uiThread: Boolean): Disposable? {
        synchronized(busy) {
            if (!busy) busy = true
            else return null
        }
        val d = this
                .subscribeOn(Schedulers.io())
                .observeOn(if (uiThread) AndroidSchedulers.mainThread() else Schedulers.newThread())
                .doFinally { synchronized(busy) { busy = false } }
                .subscribe(
                        { response ->
                            if (response.isSuccessful) {
                                val data = response.body()?.data
                                if (data != null) {
                                    f(data)
                                } else err("Response data is null")
                            } else {
                                if (response.errorBody() != null) {
                                    val gson = Gson()
                                    val message = gson.fromJson(response.errorBody()?.charStream(), BaseResponse::class.java)
                                    fError(message.error!!)
                                } else {
                                    err("Null body")
                                }
                            }
                        },
                        { e -> err(e) })
        disposables.add(d)
        return d
    }


    open fun stop() {
        disposables.clear()
    }
}