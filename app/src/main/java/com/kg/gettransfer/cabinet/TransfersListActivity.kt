package com.kg.gettransfer.cabinet

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import com.kg.gettransfer.R
import com.kg.gettransfer.modules.TransfersModule
import com.kg.gettransfer.views.TransfersAdapter

/**
 * Created by ivanpchelintsev on 04/02/2018.
 */

class TransfersListActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cabinet)

        TransfersModule.updateTransfers()
        val transfers = TransfersModule.getTransfers()

        val adapter = TransfersAdapter(transfers, false)

        val layoutManager = LinearLayoutManager(applicationContext, LinearLayoutManager.VERTICAL, false)

        val rvTransfers = findViewById<RecyclerView>(R.id.transfersList)
        rvTransfers.adapter = adapter
        rvTransfers.layoutManager = layoutManager
    }
}