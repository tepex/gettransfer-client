package com.kg.gettransfer.transfer


import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.kg.gettransfer.R
import com.kg.gettransfer.modules.Transfers
import com.kg.gettransfer.realm.Transfer
import kotlinx.android.synthetic.main.activity_transfer.*
import org.koin.android.ext.android.inject
import org.koin.standalone.KoinComponent


/**
 * Created by denisvakulenko on 16/02/2018.
 */


class TransferActivity : AppCompatActivity(), KoinComponent {
    private val transfers: Transfers by inject()

    private var transfer: Transfer? = null
        set(value) {
            field = value
            if (value != null) updateUI(value)
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transfer)

        val id = intent.getIntExtra("id", -1)

        transfer = transfers.get(id)[0]!!
    }

    private fun updateUI(transfer: Transfer) {
        tvTitle.text = transfer.from?.name + " - " + transfer.to?.name

        tvDate.text = transfer.dateTo

        tvPassengers.text = transfer.pax.toString()
        tvSign.text = transfer.nameSign
    }
}