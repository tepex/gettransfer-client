package com.kg.gettransfer.activity.transfer


import android.annotation.SuppressLint
import android.graphics.LightingColorFilter
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration.VERTICAL
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.View
import android.view.View.*
import android.widget.Toast
import com.kg.gettransfer.R
import com.kg.gettransfer.modules.OffersModel
import com.kg.gettransfer.modules.TransferModel
import com.kg.gettransfer.modules.TransportTypes
import com.kg.gettransfer.realm.Offer
import com.kg.gettransfer.realm.Transfer
import com.kg.gettransfer.realm.Utils
import com.kg.gettransfer.views.DividerItemDecoration
import com.kg.gettransfer.views.OffersAdapter
import com.kg.gettransfer.views.fadeIn
import com.kg.gettransfer.views.hide
import io.reactivex.disposables.CompositeDisposable
import io.realm.RealmResults
import kotlinx.android.synthetic.main.activity_transfer.*
import org.koin.android.ext.android.inject
import org.koin.standalone.KoinComponent


/**
 * Created by denisvakulenko on 16/02/2018.
 */


class TransferActivity : AppCompatActivity(), KoinComponent {
    private val transportTypes: TransportTypes by inject()

    private val transferModel: TransferModel by inject()
    private val offersModel: OffersModel by inject()

    private val disposables = CompositeDisposable()


    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transfer)

        val id = intent.getIntExtra("id", -1)

        wv.settings.javaScriptEnabled = true

        offersModel.addOnBusyProgressBar(progressBarOffers)
        offersModel.addOnBusyChanged { updateOffersState() }
        offersModel.addOnOffersUpdated { setOffersAsync(it) }
        offersModel.transferID = id

        transferModel.addOnTransferUpdated { updateUI(it) }
        transferModel.toastOnError(this)
        transferModel.addOnBusyProgressBar(progressBar)
        transferModel.addOnBusyChanged {
            if (it) transferStatusView.hide()
            else transferStatusView.fadeIn()
        }
        transferModel.id = id

        btnRestore.background.colorFilter = LightingColorFilter(
                ContextCompat.getColor(application, R.color.colorYellow), 0)

        val offset = (resources.displayMetrics.density * 32).toInt()
        val dividerItemDecoration = DividerItemDecoration(
                this, VERTICAL, offset, offset)

        rvOffers.addItemDecoration(dividerItemDecoration)
        rvOffers.layoutManager = object : LinearLayoutManager(applicationContext) {
            override fun canScrollVertically(): Boolean {
                return false
            }
        }
        rvOffers.emptyView = tvNoOffers

        swipeRefreshLayout.setOnRefreshListener {
            swipeRefreshLayout.isRefreshing = false
            transferModel.update()
        }
    }


    private fun updateUI(transfer: Transfer) {
        tvHeader.text = "Transfer #" + transfer.id
        tvPaymentHeader.text = "Book transfer #" + transfer.id

        tvFrom.text = transfer.from?.name

        if (transfer.to == null) {
            tvlTo.text = "Duration"
            tvTo.text = transfer.hireDurationString

            ivTo.setImageResource(R.drawable.ic_timer_blue_16dp)

            tvRouteInfo.visibility = GONE
        } else {
            tvlTo.text = "To"
            tvTo.text = transfer.to?.name

            ivTo.setImageResource(R.drawable.ic_arrow_blue_16dp)

            tvRouteInfo.visibility = VISIBLE
            tvRouteInfo.text =
                    "${transfer.routeDistance} km, expected travel time: ${transfer.routeDuration} min "
        }

        tvDate.text = Utils.dateToString(transfer.dateTo)

        tvPassengers.text = transfer.pax.toString()
        tvTypes.text = transportTypes.getNames(transfer.transportTypes)
        tvSign.text = transfer.nameSign
        tvChildSeats.text = transfer.childSeats
        tvComments.text = transfer.comment ?: "-"

        if (transfer.status == "new" && !transfer.bookNow) {
            clActive.visibility = VISIBLE
            clArchive.visibility = GONE

            val carriers = transfer.relevantCarrierProfilesCount ?: 0
            tvConnecting.text =
                    if (carriers > 1) "Connecting to $carriers carriers..."
                    else "Connecting to carriers..."
        } else if (transfer.status == "resolved") {
            clActive.visibility = GONE
            clArchive.visibility = GONE
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

        tvNoOffers.text =
                if (transfer.offersCount == 0) "Offers will be there shortly, often faster than in 1 day"
                else "Number of offers: " + transfer.offersCount + "\nSwipe to refresh"

        Log.i("TransferActivity", "UI updated")
    }


    private var offers: RealmResults<Offer>? = null
    private fun setOffersAsync(offers: RealmResults<Offer>) {
        this.offers?.removeAllChangeListeners()

        val offersAdapter = OffersAdapter(offers, true)
        offersAdapter.icl = View.OnClickListener {
            pay(it.getTag(R.id.key_id) as Int)
        }

        rvOffers.adapter = offersAdapter

        offers.addChangeListener { _ -> updateOffersState() }
        updateOffersState()

        this.offers = offers
    }


    private fun updateOffersState() {
        if (offersModel.busy) {
            tvConnecting.hide()
            tvChooseCarrier.hide()
        } else {
            val hasOffers = transferModel.transfer?.offers?.size ?: 0 > 0
            if (!hasOffers) tvConnecting.fadeIn() else tvConnecting.hide()
            if (hasOffers) tvChooseCarrier.fadeIn() else tvChooseCarrier.hide()
        }
    }


    private fun pay(offerID: Int) {
        disposables.add(
                transferModel.payment(offerID)
                        .subscribe(
                                {
                                    clPayment.visibility = VISIBLE
                                    wv.loadUrl(it.data?.url)
                                },
                                {
                                    Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                                }))
    }


    fun back(v: View) {
        finish()
    }

    fun hidePayment(v: View?) {
        clPayment.visibility = INVISIBLE
    }

    fun cancel(v: View) {
        scrollView.fullScroll(FOCUS_UP)
        transferModel.cancel()
    }

    fun showAllDetails(v: View) {
        tvShowAllDetails.visibility = GONE
        clDetails.visibility = VISIBLE
    }

    fun restore(v: View) {
        scrollView.fullScroll(FOCUS_UP) //TODO: Remove when header implemented
        transferModel.restore()
    }

    override fun onBackPressed() {
        if (clPayment.visibility == VISIBLE) hidePayment(null)
        else super.onBackPressed()
    }

    override fun onResume() {
        super.onResume()
        transferModel.updateIfOld()
    }

    override fun onDestroy() {
        super.onDestroy()
        offers?.removeAllChangeListeners()

        transferModel.stop()
        offersModel.stop()

        disposables.clear()
    }
}
