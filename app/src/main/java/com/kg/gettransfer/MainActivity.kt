package com.kg.gettransfer


import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.kg.gettransfer.data.Api
import io.reactivex.disposables.Disposable
import io.realm.Realm
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import io.reactivex.*;
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers


class MainActivity : AppCompatActivity() {
    val api by lazy {
        Api.create()
    }
    var disposable: Disposable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onResume() {
        super.onResume()

        val realm = Realm.getDefaultInstance()

//
//        api.getTransportTypes()

        disposable =
                api.getTransportTypes()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                { r ->

                                    if ("success".equals(r.result)) {
                                        Log.d("MA", r.data?.size.toString())
                                    }
                                },
                                { }
//                                { result -> showResult(result.query.searchinfo.totalhits) },
//                                { error -> showError(error.message) }
                        )
    }


}
