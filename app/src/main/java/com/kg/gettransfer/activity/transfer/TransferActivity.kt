package com.kg.gettransfer.activity.transfer


import android.annotation.SuppressLint
import android.graphics.LightingColorFilter
import android.os.Build
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.View
import android.view.View.*
import android.widget.Toast
import com.kg.gettransfer.R
import com.kg.gettransfer.modules.TransferModel
import com.kg.gettransfer.modules.TransportTypes
import com.kg.gettransfer.realm.Offer
import com.kg.gettransfer.realm.Transfer
import com.kg.gettransfer.realm.Utils
import com.kg.gettransfer.views.OffersAdapter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
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

    private val disposables = CompositeDisposable()


    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transfer)

        wv.settings.javaScriptEnabled = true


        val id = intent.getIntExtra("id", -1)
        transferModel.id = id


        transferModel.addOnTransferUpdated { updateUI(it) }

        transferModel.addOnError {
            Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
        }

        transferModel.addOnBusyProgressBar(progressBar, INVISIBLE)
        transferModel.addOnBusyChanged {
            if (!it && transferStatusView.visibility == INVISIBLE) {
                transferStatusView.alpha = 0.0f
                transferStatusView.animate()
                        .alpha(1.0f)
                        .setStartDelay(200)
                        .setDuration(200)
            }
            transferStatusView.visibility = if (!it) VISIBLE else INVISIBLE
        }


        btnRestore.background.colorFilter = LightingColorFilter(
                ContextCompat.getColor(application, R.color.colorYellow), 0)

        rvOffers.layoutManager = object : LinearLayoutManager(applicationContext) {
            override fun canScrollVertically(): Boolean {
                return false
            }
        }
        rvOffers.emptyView = tvNoOffers


//        val density = applicationContext.resources.displayMetrics.density
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            scrollView.setOnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
//                line4.alpha = scrollY / density / 8
//            }
//        } else {
//            scrollView.viewTreeObserver.addOnScrollChangedListener({
//                line4.alpha = scrollView.scrollY / density / 8
//            })
//        }
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

        tvDate.text = Utils.dateToString(this, transfer.dateTo)

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

            if (this.offers == null) transferModel.updateOffers()

            setOffersAsync(transferModel.getOffersAsyncRealmResult())
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


    private fun pay(offerID: Int) {
        disposables.add(
                transferModel.payment(offerID)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
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

    fun restore(v: View) {
        scrollView.fullScroll(FOCUS_UP) //TODO: Remove when header implemented
        transferModel.restore()
    }

    override fun onBackPressed() {
        if (clPayment.visibility == VISIBLE) hidePayment(null)
        else super.onBackPressed()
    }

    override fun onDestroy() {
        super.onDestroy()
        offers?.removeAllChangeListeners()
        transferModel.close()
        disposables.clear()
    }
}