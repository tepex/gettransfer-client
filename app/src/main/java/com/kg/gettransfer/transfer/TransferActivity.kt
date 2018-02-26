package com.kg.gettransfer.transfer


import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.View
import com.kg.gettransfer.R
import com.kg.gettransfer.modules.TransferModel
import com.kg.gettransfer.modules.TransportTypesProvider
import com.kg.gettransfer.realm.Offer
import com.kg.gettransfer.realm.Transfer
import com.kg.gettransfer.realm.Utils
import com.kg.gettransfer.views.OffersAdapter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.realm.RealmResults
import kotlinx.android.synthetic.main.activity_transfer.*
import org.koin.android.ext.android.inject
import org.koin.standalone.KoinComponent


/**
 * Created by denisvakulenko on 16/02/2018.
 */


class TransferActivity : AppCompatActivity(), KoinComponent {
    private val transportTypes: TransportTypesProvider by inject()
    private val transferModel: TransferModel by inject()


    override fun onCreate(savedInstanceState: Bundle?) {
        Log.i("TransferActivity", "onCreate()")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transfer)

        val id = intent.getIntExtra("id", -1)
        transferModel.id = id

        transferModel.updateOffers()

        transferModel.transfer
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe { updateUI(it) }

        transferModel.errors
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    tvTitle.text = "DRFDS"
                    //Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                }, {
                    //Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                })

        transferModel.busy
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    progressBar.visibility = if (it) View.VISIBLE else View.INVISIBLE
                    transferStatusView.visibility = if (!it) View.VISIBLE else View.INVISIBLE
                }

        val rv = rvOffers
        rv.layoutManager = LinearLayoutManager(applicationContext)
        rv.emptyView = tvNoOffers

        wv.loadUrl("https://gettransfer.com/en")
    }


    private fun updateUI(transfer: Transfer) {
        tvTitle.text = "Transfer #" + transfer.id

        tvFrom.text = transfer.from?.name
        tvTo.text = transfer.to?.name

        tvDate.text = Utils.dateToString(this, transfer.dateTo)

        tvPassengers.text = transfer.pax.toString()
        tvTypes.text = transportTypes.getNames(transfer.transportTypes)
        tvSign.text = transfer.nameSign
        tvChildSeats.text = transfer.childSeats
        tvComments.text = transfer.comments ?: "-"

        if (transfer.status == "new" && !transfer.bookNow) {
            clActive.visibility = View.VISIBLE
            clArchive.visibility = View.GONE

            setOffersAsync(transferModel.getOffers())
        } else {
            clActive.visibility = View.GONE
            clArchive.visibility = View.VISIBLE
        }

        transferStatusView.update(transfer)

        scrollView.post { scrollView.fullScroll(View.FOCUS_UP) }

        Log.i("TransferActivity", "UI updated")
    }


    var offers: RealmResults<Offer>? = null
    private fun setOffersAsync(offers: RealmResults<Offer>) {
        this.offers?.removeAllChangeListeners()

        rvOffers.adapter = OffersAdapter(offers, true)

        offers.addChangeListener { offers ->
            val hasOffers = offers.size > 0
            tvChooseCarrier.visibility = if (hasOffers) View.VISIBLE else View.GONE
            tvConnecting.visibility = if (!hasOffers) View.VISIBLE else View.GONE
        }

        this.offers = offers
    }


    fun back(v: View) {
        finish()
    }

    fun cancel(v: View) {
        transferModel.cancelTransfer()
    }

    fun restore(v: View) {
        transferModel.restoreTransfer()
    }

    override fun onDestroy() {
        super.onDestroy()
        offers?.removeAllChangeListeners()
        transferModel.close()
    }
}