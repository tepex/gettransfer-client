package com.kg.gettransfer


import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import com.kg.gettransfer.modules.DB
import com.kg.gettransfer.modules.Transfers
import com.kg.gettransfer.modules.TransportTypes
import com.kg.gettransfer.network.Api
import com.kg.gettransfer.network.Transfer
import com.kg.gettransfer.views.TransportTypesAdapter
import io.reactivex.disposables.Disposable
import io.realm.Realm


/**
 * Created by denisvakulenko on 25/01/2018.
 */


class MainActivity : AppCompatActivity() {
    private val TAG = "MainActivity"

//    private val api by lazy {
//        Api.api
//    }
//    private var disposable: Disposable? = null
    private var realm: Realm? = null

    private val transportTypes: TransportTypes = TransportTypes()
    private val transfers: Transfers = Transfers()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        realm = DB.create(applicationContext)

        initListTransportTypes()
    }


    private fun initListTransportTypes() {
        val types = transportTypes.get()

        val recyclerView = findViewById<RecyclerView>(R.id.rvTypes)

        val mAdapter = TransportTypesAdapter(types, true)

        val mLayoutManager = LinearLayoutManager(applicationContext)
        mLayoutManager.orientation = LinearLayoutManager.HORIZONTAL
        recyclerView.layoutManager = mLayoutManager
        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.adapter = mAdapter
    }


    fun fabClick(v: View) {
        transfers.postTransfer()
    }
}
