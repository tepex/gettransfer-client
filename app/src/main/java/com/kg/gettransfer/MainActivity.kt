package com.kg.gettransfer


import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import com.kg.gettransfer.modules.DB
import com.kg.gettransfer.modules.Transfers
import com.kg.gettransfer.modules.TransportTypes
import com.kg.gettransfer.network.Api
import com.kg.gettransfer.views.TransportTypesAdapter
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

    val etEmail by lazy { findViewById<EditText>(R.id.etEmail) }
    val etPhone by lazy { findViewById<EditText>(R.id.etPhone) }
    val fab by lazy { findViewById<FloatingActionButton>(R.id.fabTransfer) }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        realm = DB.create(applicationContext)
        val api = Api.api

        initListTransportTypes()

//        transfers.updateTransfers()

        etEmail.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                fab.visibility = if (validateFields()) View.VISIBLE else View.GONE
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })
    }


    // TODO:
    private fun validateFields(): Boolean =
            etEmail.text.isNotEmpty() && etEmail.text.isNotEmpty()


    private fun initListTransportTypes() {
        val recyclerView = findViewById<RecyclerView>(R.id.rvTypes)

        val types = transportTypes.get()
        val adapter = TransportTypesAdapter(types, true)

        val layoutManager = LinearLayoutManager(applicationContext, LinearLayoutManager.HORIZONTAL, false)

        recyclerView.layoutManager = layoutManager
        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.adapter = adapter
    }


    fun fabClick(v: View) {
        transfers.postTransfer()
    }
}
