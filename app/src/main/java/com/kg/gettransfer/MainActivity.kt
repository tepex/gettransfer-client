package com.kg.gettransfer


import android.graphics.drawable.GradientDrawable
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.kg.gettransfer.network.Api
import com.kg.gettransfer.modules.TransportTypes
import io.reactivex.disposables.Disposable
import io.realm.Realm
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import com.kg.gettransfer.views.TransportTypesAdapter
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

    public fun fabClick(v: View) {

    }
}
