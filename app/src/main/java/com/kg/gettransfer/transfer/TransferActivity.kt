package com.kg.gettransfer.transfer


import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.format.DateUtils.*
import android.view.View
import com.kg.gettransfer.R
import com.kg.gettransfer.modules.Transfers
import com.kg.gettransfer.modules.TransportTypesProvider
import com.kg.gettransfer.realm.Transfer
import com.kg.gettransfer.realm.Utils
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

    @SuppressLint("SetTextI18n")
    private fun updateUI(transfer: Transfer) {
        tvFrom.text = transfer.from?.name
        tvTo.text = transfer.to?.name

        val date = Utils.dateFromString(transfer.dateTo)!!
        tvDate.text = formatDateTime(
                this,
                date.time,
                FORMAT_SHOW_TIME).toString() +
                "   " +
                formatDateTime(
                        this,
                        date.time,
                        FORMAT_SHOW_DATE or FORMAT_ABBREV_MONTH or FORMAT_SHOW_YEAR).toString()

        tvPassengers.text = transfer.pax.toString()
        tvTypes.text = transportTypes.getNames(transfer.transportTypes)
        tvSign.text = transfer.nameSign
        tvChildSeats.text = transfer.childSeats
        tvComments.text = transfer.comments ?: "-"
    }

    fun back(v: View) {
        finish()
    }
}