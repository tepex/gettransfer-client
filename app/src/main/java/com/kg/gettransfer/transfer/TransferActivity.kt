package com.kg.gettransfer.transfer


import android.graphics.LightingColorFilter
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.View
import android.view.View.*
import android.widget.Toast
import com.kg.gettransfer.R
import com.kg.gettransfer.modules.TransferModel
import com.kg.gettransfer.modules.TransportTypesProvider
import com.kg.gettransfer.realm.Offer
import com.kg.gettransfer.realm.Transfer
import com.kg.gettransfer.realm.Utils
import com.kg.gettransfer.views.OffersAdapter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
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

    private val disposables = CompositeDisposable()


    override fun onCreate(savedInstanceState: Bundle?) {
        Log.i("TransferActivity", "onCreate()")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transfer)

        val id = intent.getIntExtra("id", -1)
        transferModel.id = id

        disposables.add(
                transferModel.transfer
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe { updateUI(it) })

        transferModel.addOnError {
            Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
        }

        transferModel.addOnBusyChanged {
            progressBar.visibility = if (it) VISIBLE else INVISIBLE

            if (!it && transferStatusView.visibility == INVISIBLE) {
                transferStatusView.alpha = 0.0f
                transferStatusView.animate()
                        .alpha(1.0f)
                        .setStartDelay(200)
                        .setDuration(200)
            }
            transferStatusView.visibility = if (!it) VISIBLE else INVISIBLE
        }

        btnRestore.background.colorFilter = LightingColorFilter(0xffffcc4c.toInt(), 0x0)

        val rv = rvOffers
        rv.layoutManager = object : LinearLayoutManager(applicationContext) {
            override fun canScrollVertically(): Boolean {
                return false
            }
        }
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
            clActive.visibility = VISIBLE
            clArchive.visibility = GONE

            val carriers = transfer.relevantCarrierProfilesCount ?: 0
            tvConnecting.text =
                    if (carriers > 1) "Connecting to $carriers carriers..."
                    else "Connecting to carriers..."

            if (this.offers == null) transferModel.updateOffers()

            setOffersAsync(transferModel.getOffersAsyncRealmResult())
        } else {
            clActive.visibility = GONE
            clArchive.visibility = VISIBLE

            when {
                transfer.status == "outdated" -> {
                    tvArchiveHeader.text = "Transfer date passed"
                    tvRestoreHint.text = ""
                    btnRestore.visibility = INVISIBLE
                }
                transfer.status == "rejected" -> {
                    tvArchiveHeader.text = "Manager rejected request"
                    tvRestoreHint.text = "Restore?"
                    btnRestore.visibility = VISIBLE
                }
                else -> {
                    tvArchiveHeader.text = "You cancelled request"
                    tvRestoreHint.text = "Restore?"
                    btnRestore.visibility = VISIBLE
                }
            }
        }

        transferStatusView.update(transfer)

        Log.i("TransferActivity", "UI updated")
    }


    private var offers: RealmResults<Offer>? = null
    private fun setOffersAsync(offers: RealmResults<Offer>) {
        this.offers?.removeAllChangeListeners()

        val offersAdapter = OffersAdapter(offers, true)
        offersAdapter.icl = View.OnClickListener {
            disposables.add(
                    transferModel.payment(it.getTag(R.id.key_id) as Int)
                            .subscribe { wv.loadUrl(it.data?.url) })
        }

        rvOffers.adapter = offersAdapter

        offers.addChangeListener { offers ->
            val hasOffers = offers.size > 0
            tvChooseCarrier.visibility = if (hasOffers) VISIBLE else GONE
            tvConnecting.visibility = if (!hasOffers) VISIBLE else GONE
            if (!hasOffers) {
                tvConnecting.alpha = 0.0f
                tvConnecting.animate()
                        .alpha(1.0f)
                        .setStartDelay(800)
                        .setDuration(200)
            }
        }

        this.offers = offers
    }


    fun back(v: View) {
        finish()
    }

    fun cancel(v: View) {
        scrollView.fullScroll(FOCUS_UP)
        transferModel.cancel()
    }

    fun restore(v: View) {
        scrollView.fullScroll(FOCUS_UP) //TODO: Remove when header implemented
        transferModel.restore()
    }

    override fun onDestroy() {
        super.onDestroy()
        offers?.removeAllChangeListeners()
        transferModel.close()
        disposables.clear()
    }
}