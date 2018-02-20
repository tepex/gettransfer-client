package com.kg.gettransfer.transfers


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
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.activity_transfers.*
import org.koin.android.ext.android.inject


/**
 * Created by ivanpchelintsev on 04/02/2018.
 */


class TransfersActivity : AppCompatActivity() {
    private val currentAccount: CurrentAccount by inject()
    private val transfers: Transfers by inject()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transfers)

        initRecyclerView()
    }

    private fun initRecyclerView() {
        val rvTransfers = rvTransfers
        rvTransfers.adapter = TransfersAdapter(transfers.getAllAsync(), true)
        rvTransfers.layoutManager = LinearLayoutManager(applicationContext)
        rvTransfers.emptyView = clEmptyTransfers

        swipeRefreshLayout.setOnRefreshListener { transfers.updateTransfers() }
        transfers.busy
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { swipeRefreshLayout.isRefreshing = it }
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
        transfers.updateTransfers()
    }

    private fun showLoggedOut() {
        swipeRefreshLayout.visibility = View.GONE
        clLoggedOut.visibility = View.VISIBLE
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