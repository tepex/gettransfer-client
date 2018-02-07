package com.kg.gettransfer.cabinet

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import com.kg.gettransfer.R
import com.kg.gettransfer.modules.Transfers
import com.kg.gettransfer.views.TransfersAdapter
import org.koin.android.ext.android.inject

/**
 * Created by ivanpchelintsev on 04/02/2018.
 */

class TransfersListActivity : AppCompatActivity() {
    private val transfersProvider: Transfers by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cabinet)

        transfersProvider.updateTransfers()
        val transfers = transfersProvider.getTransfers()

        val adapter = TransfersAdapter(transfers, false)

        val layoutManager = LinearLayoutManager(applicationContext, LinearLayoutManager.VERTICAL, false)

        val rvTransfers = findViewById<RecyclerView>(R.id.transfersList)
        rvTransfers.adapter = adapter
        rvTransfers.layoutManager = layoutManager
    }
}