package com.kg.gettransfer.cabinet

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.kg.gettransfer.R
import com.kg.gettransfer.createtransfer.CreateTransferActivity
import com.kg.gettransfer.login.LoginActivity
import com.kg.gettransfer.modules.CurrentAccount
import com.kg.gettransfer.modules.Transfers
import com.kg.gettransfer.views.TransfersAdapter
import kotlinx.android.synthetic.main.activity_transferslist.*
import org.koin.android.ext.android.inject

/**
 * Created by ivanpchelintsev on 04/02/2018.
 */

class TransfersListActivity : AppCompatActivity() {
    private val transfersProvider: Transfers by inject()
    private val currentAccount: CurrentAccount by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transferslist)

        initRecyclerView()
    }

    override fun onResume() {
        super.onResume()

        if (currentAccount.isLoggedIn) {
            showTransfers()
        } else {
            showLoggedOut()
        }
    }

    private fun showTransfers() {
        swipeRefreshLayout.visibility = View.VISIBLE
        clLoggedOut.visibility = View.GONE
        transfersProvider.updateTransfers()
    }

    private fun showLoggedOut() {
        swipeRefreshLayout.visibility = View.GONE
        clLoggedOut.visibility = View.VISIBLE
    }

    private fun initRecyclerView() {
        val transfers = transfersProvider.getTransfers()

        val adapter = TransfersAdapter(transfers, true)

        val layoutManager = LinearLayoutManager(applicationContext, LinearLayoutManager.VERTICAL, false)

        val rvTransfers = rvTransfers
        rvTransfers.adapter = adapter
        rvTransfers.layoutManager = layoutManager
        rvTransfers.emptyView = clEmptyTransfers

        swipeRefreshLayout.setOnRefreshListener { transfersProvider.updateTransfers() }
        transfersProvider.busy.subscribe { swipeRefreshLayout.isRefreshing = it }
    }

    fun createTransfer(v: View?) {
        val intent = Intent(this, CreateTransferActivity::class.java)
        startActivity(intent)
    }

    fun logIn(v: View?) {
        val intent = Intent(this, LoginActivity::class.java)
        startActivityForResult(intent, 2)
    }
}