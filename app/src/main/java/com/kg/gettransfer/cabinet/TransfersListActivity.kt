package com.kg.gettransfer.cabinet

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.kg.gettransfer.R
import com.kg.gettransfer.createtransfer.CreateTransferActivity
import com.kg.gettransfer.modules.Transfers
import com.kg.gettransfer.views.TransfersAdapter
import kotlinx.android.synthetic.main.activity_transferslist.*
import org.koin.android.ext.android.inject

/**
 * Created by ivanpchelintsev on 04/02/2018.
 */

class TransfersListActivity : AppCompatActivity() {
    private val transfersProvider: Transfers by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transferslist)

        transfersProvider.updateTransfers()
        val transfers = transfersProvider.getTransfers()

        val adapter = TransfersAdapter(transfers, true)

        val layoutManager = LinearLayoutManager(applicationContext, LinearLayoutManager.VERTICAL, false)

        val rvTransfers = transfersList
        rvTransfers.adapter = adapter
        rvTransfers.layoutManager = layoutManager
        rvTransfers.emptyView = emptyTransfersLayout
    }

    fun createTransfer(v: View?) {
        val intent = Intent(this, CreateTransferActivity::class.java)
        startActivityForResult(intent, 2)
    }
}