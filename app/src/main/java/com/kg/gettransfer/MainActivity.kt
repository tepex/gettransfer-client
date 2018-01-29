package com.kg.gettransfer


import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import com.kg.gettransfer.modules.TransportTypes
import com.kg.gettransfer.network.*
import com.kg.gettransfer.views.TransportTypesAdapter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.realm.Realm
import io.realm.RealmConfiguration


class MainActivity : AppCompatActivity() {
    private val TAG = "MainActivity"

    private val api by lazy {
        Api.create()
    }
    private var disposable: Disposable? = null
    private var realm: Realm? = null

    private val transportTypes: TransportTypes = TransportTypes()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Realm.init(applicationContext)
        val config: RealmConfiguration = RealmConfiguration.Builder()
                .name("db")
                .schemaVersion(1)
                .deleteRealmIfMigrationNeeded()
                .build()
        Realm.setDefaultConfiguration(config)
        realm = Realm.getDefaultInstance()

        initListTransportTypes()
    }

    private fun initListTransportTypes() {
        val types = transportTypes.get()

        val recyclerView = findViewById<RecyclerView>(R.id.rvTypes)

        val mAdapter = TransportTypesAdapter(this, types, false, false)

        val mLayoutManager = LinearLayoutManager(applicationContext)
        mLayoutManager.orientation = LinearLayoutManager.HORIZONTAL
        recyclerView.layoutManager = mLayoutManager
        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.adapter = mAdapter
    }

    fun fabClick(v: View) {
        Log.d(TAG, "getAccessToken()")
        api.getAccessToken(Api.API_KEY)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ r ->
                    Log.d(TAG, "Responded getAccessToken()")
                    if (r.success()) {
                        Log.d(TAG, "Success getAccessToken(), accessToken = " + r.data!!.token)
                        getTransfers(r.data!!.token)
                    } else {
                        Log.d(TAG, "Failed getAccessToken() result = ${r.result}")
                    }
                }, { error ->
                    Log.d(TAG, "Failed getAccessToken() ${error.message}")
                })
    }

    fun getTransfers(token: String) {
        Log.d(TAG, "getTransfers()")
        api.getTransfers(
                token,
                TransferPOJO(
                        19,
                        33,
                        Location("Moscow", 1, 1).toMap(),
                        Location("Petersburg", 2, 2).toMap(),
                        "2020/12/25",
                        "15:00",
                        "1",
                        1,
                        "Ivan",
                        PassengerProfile("ivan@key-g.com", "+79998887766").toMap())
        ).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ r ->
                    Log.d(TAG, "Responded getTransfers()")
                    if (r.success()) {
                        Log.d(TAG, "Success getTransfers(), id = " + r.data?.id)
                    } else {
                        Log.d(TAG, "Failed getTransfers() result = ${r.result}")
                    }
                }, { error ->
                    Log.d(TAG, "Failed getTransfers() ${error.message}")
                })
    }
}
