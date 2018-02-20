package com.kg.gettransfer.transfer


import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.View
import com.kg.gettransfer.R
import com.kg.gettransfer.modules.Transfers
import com.kg.gettransfer.modules.TransportTypesProvider
import com.kg.gettransfer.realm.Transfer
import com.kg.gettransfer.realm.Utils
import com.kg.gettransfer.views.OffersAdapter
import kotlinx.android.synthetic.main.activity_transfer.*
import org.koin.android.ext.android.inject
import org.koin.standalone.KoinComponent


/**
 * Created by denisvakulenko on 16/02/2018.
 */


class TransferActivity : AppCompatActivity(), KoinComponent {
    private val transfers: Transfers by inject()
    private val transportTypes: TransportTypesProvider by inject()

    private var transfer: Transfer? = null
        set(newTransfer) {
            field = newTransfer
            if (newTransfer != null) updateUI(newTransfer)
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.i("TransferActivity", "onCreate()")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transfer)

        val id = intent.getIntExtra("id", -1)

        transfers.getAsync(id).addChangeListener { t ->
            Log.i("TransferActivity", "getAsync() changed")
            if (t.size > 0 && t.isLoaded) {
                val r = t[0]
                if (r?.isLoaded == true) {
                    transfer = r
                }
            }
        }

        transfers.updateOffers(id)

        val rv = rvOffers
        rv.layoutManager = LinearLayoutManager(applicationContext)
        rv.emptyView = tvNoOffers
    }

    private fun updateUI(transfer: Transfer) {
        tvFrom.text = transfer.from?.name
        tvTo.text = transfer.to?.name

        tvDate.text = Utils.dateToString(this, transfer.dateTo)

        tvPassengers.text = transfer.pax.toString()
        tvTypes.text = transportTypes.getNames(transfer.transportTypes)
        tvSign.text = transfer.nameSign
        tvChildSeats.text = transfer.childSeats
        tvComments.text = transfer.comments ?: "-"

        val offers = transfers.getOffers(transfer)!!
        rvOffers.adapter = OffersAdapter(offers, true)

        Log.i("TransferActivity", "UI updated")
    }

    fun back(v: View) {
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()

    }
}