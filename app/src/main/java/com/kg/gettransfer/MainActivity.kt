package com.kg.gettransfer


import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.TextView
import com.kg.gettransfer.data.Api
import com.kg.gettransfer.data.TransportType
import io.reactivex.disposables.Disposable
import io.realm.Realm
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import io.reactivex.*;
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.realm.RealmList
import io.realm.RealmResults


class MainActivity : AppCompatActivity() {
    private val TAG = "MainActivity"

    private val api by lazy {
        Api.create()
    }
    private var disposable: Disposable? = null
    private val realm: Realm by lazy {
        Realm.getDefaultInstance()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Realm.init(applicationContext)
        realm

        initListTransportTypes()
    }

    override fun onResume() {
        super.onResume()

        Log.d(TAG, "Resume()")

        // Update transport types
        disposable =
                api.getTransportTypes()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({ r ->
                            Log.d(TAG, "Responded getTransportTypes()")
                            if (r.success()) {
                                Log.d(TAG, "Success getTransportTypes(), n = " + r.data?.size.toString())

                                realm.executeTransaction { realm ->
                                    realm.copyToRealmOrUpdate(r.data)
                                    Log.d(TAG, "Saved to realm")
                                }
                            } else {
                                Log.d(TAG, "Failed getTransportTypes() result = ${r.result}")
                            }
                        }, { error ->
                            Log.d(TAG, "Failed getTransportTypes() ${error.message}")
                        })
    }

    private fun initListTransportTypes() {
        val lv = findViewById<LinearLayout>(R.id.llTransportTypes)

        val types = realm.where(TransportType::class.java).findAll()

        types.addChangeListener { newTypes ->
            lv.removeAllViews()
            fillListTransportTypes(newTypes)
        }

        fillListTransportTypes(types)
    }

    private fun fillListTransportTypes(l: RealmResults<TransportType>) {
        val lv = findViewById<LinearLayout>(R.id.llTransportTypes)
        for (type in l) {
            val textView = TextView(this)
            textView.text = type.title
            lv.addView(textView)
        }
    }
}
