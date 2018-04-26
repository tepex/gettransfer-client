package com.kg.gettransfer.activity.transfer


import android.annotation.SuppressLint
import android.graphics.LightingColorFilter
import android.graphics.Paint
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration.VERTICAL
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.View
import android.view.View.*
import android.view.animation.*
import android.widget.Toast
import com.kg.gettransfer.R
import com.kg.gettransfer.modules.CurrentAccount
import com.kg.gettransfer.modules.OffersModel
import com.kg.gettransfer.modules.TransferModel
import com.kg.gettransfer.modules.TransportTypes
import com.kg.gettransfer.realm.Offer
import com.kg.gettransfer.realm.Transfer
import com.kg.gettransfer.realm.getPlString
import com.kg.gettransfer.views.DividerItemDecoration
import com.kg.gettransfer.views.OffersAdapter
import com.kg.gettransfer.views.fadeIn
import com.kg.gettransfer.views.hide
import io.reactivex.disposables.CompositeDisposable
import io.realm.RealmResults
import kotlinx.android.synthetic.main.activity_transfer.*
import kotlinx.android.synthetic.main.activity_transfer.view.*
import org.koin.android.ext.android.inject
import org.koin.standalone.KoinComponent
import java.text.DateFormat
import java.util.*


/**
 * Created by denisvakulenko on 16/02/2018.
 */


class TransferActivity : AppCompatActivity(), KoinComponent {
    private val transportTypes: TransportTypes by inject()

    private val currentAccount: CurrentAccount by inject()

    private val transferModel: TransferModel by inject()
    private val offersModel: OffersModel by inject()

    private val disposables = CompositeDisposable()


    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transfer)

        val id = intent.getIntExtra("id", -1)

        wv.settings.javaScriptEnabled = true

        offersModel.addOnOffersUpdated { setOffersAsync(it) }
        offersModel.toastOnError(this, getString(R.string.unable_to_update_offers))
        offersModel.addOnBusyProgressBar(progressBarOffers)
        offersModel.addOnBusyChanged { updateOffersState() }
        offersModel.transferID = id

        transferModel.addOnTransferUpdated { updateUI(it) }
        transferModel.toastOnError(this, getString(R.string.unable_to_update_transfer))
        transferModel.addOnBusyProgressBar(progressBar)
        transferModel.addOnBusyChanged {
            if (it) transferStatusView.hide()
            else transferStatusView.fadeIn()
        }
        transferModel.id = id

        btnRestore.background.colorFilter = LightingColorFilter(
                ContextCompat.getColor(application, R.color.colorYellow), 0)

        val offset = (resources.displayMetrics.density * 0).toInt()
        val dividerItemDecoration = DividerItemDecoration(
                this, VERTICAL, offset, offset)

        rvOffers.addItemDecoration(dividerItemDecoration)
        rvOffers.layoutManager = object : LinearLayoutManager(applicationContext) {
            override fun canScrollVertically(): Boolean {
                return false
            }
        }
        rvOffers.emptyView = clNoOffers

        srlTransfer.setOnRefreshListener {
            srlTransfer.isRefreshing = false
            transferModel.update()
            if (transferModel.transfer?.isActive == true) offersModel.update()
        }

        btnPay.setOnClickListener {
            pay(offerID)
        }

        tvPriceOff.paintFlags = tvPriceOff.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        tvPriceOffOrigin.paintFlags = tvPriceOff.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
    }


    private fun updateUI(transfer: Transfer) {
        tvHeader.text = getString(R.string.transfer_header) + transfer.id
        tvPaymentHeader.text = getString(R.string.book_header) + transfer.id

        tvFrom.text = transfer.from?.name

        if (transfer.to == null) {
            tvlTo.text = getString(R.string.duration)
            tvTo.text = transfer.hireDurationString(this)

            ivTo.visibility = GONE
            ivMarkerTo.setImageResource(R.drawable.ic_timer_blue_22dp_padding)

            tvRouteInfo.visibility = GONE
        } else {
            tvlTo.text = getString(R.string.to)
            tvTo.text = transfer.to?.name

            ivTo.visibility = VISIBLE
            ivMarkerTo.setImageResource(R.drawable.ic_place_blue_24dp)

            tvRouteInfo.visibility = VISIBLE
            tvRouteInfo.text = String.format(
                    getString(R.string.distance_time),
                    transfer.getRouteDistanceConverted(
                            this, currentAccount.accountInfo.getDistanceUnitId()),
                    transfer.routeDuration.toString() + " " + getString(R.string.min))
        }

        tvTime.text = DateFormat
                .getTimeInstance(DateFormat.SHORT, Locale.getDefault())
                .format(transfer.dateTo)
        tvDate.text = DateFormat.getDateInstance().format(transfer.dateTo)

        tvPassengers.text = transfer.pax.toString()
        tvTypes.text = transportTypes.getNames(transfer.transportTypes)
        tvSign.text = transfer.nameSign
        tvChildSeats.text = transfer.childSeats

        if (transfer.comment == null) {
            grComments.visibility = GONE
        } else {
            grComments.visibility = VISIBLE
            tvComments.text = transfer.comment ?: "-"
        }

        if (transfer.status == "new" && !transfer.bookNow) {
            clActive.visibility = VISIBLE
            clArchive.visibility = GONE

            tvConnecting.text = getConnectingToCarriersString(transfer.relevantCarrierProfilesCount)
        } else if (transfer.status == "resolved") {
            clActive.visibility = GONE
            clArchive.visibility = GONE
        } else {
            clActive.visibility = GONE
            clArchive.visibility = VISIBLE

            when {
                transfer.status == "outdated" -> {
                    tvArchiveHeader.text = getString(R.string.transfer_date_passed)
                    tvRestoreHint.text = null
                    btnRestore.visibility = INVISIBLE
                }
                transfer.status == "rejected" -> {
                    tvArchiveHeader.text = getString(R.string.manager_rejected_request)
                    tvRestoreHint.text = getString(R.string.restore_question)
                    btnRestore.visibility = VISIBLE
                }
                else -> {
                    tvArchiveHeader.text = getString(R.string.you_cancelled_request)
                    tvRestoreHint.text = getString(R.string.restore_question)
                    btnRestore.visibility = VISIBLE
                }
            }
        }

        transferStatusView.update(transfer)

        val offersCount = transfer.offersCount
        tvNoOffers.text =
                if (offersCount == 0) getString(R.string.offers_will_be_shortly)
                else offersCount.toString() + " " + getPlString(R.string.pl_offers).forN(offersCount) +
                        "\n" + getString(R.string.swipe_to_refresh)

        Log.i("TransferActivity", "UI updated")
    }


    private fun getConnectingToCarriersString(relevantCarrierProfilesCount: Int?): String {
        val carriers = relevantCarrierProfilesCount ?: 0
        return if (carriers > 1) {
            getString(R.string.connecting_to) + " " + carriers +
                    " " + getPlString(R.string.pl_carriers).forN(carriers)
        } else getString(R.string.connecting_to_carriers)
    }


    private var offers: RealmResults<Offer>? = null
    private fun setOffersAsync(offers: RealmResults<Offer>) {
        this.offers?.removeAllChangeListeners()

        val offersAdapter = OffersAdapter(offers)
        offersAdapter.icl = View.OnClickListener {
            showOffer(it.getTag(R.id.key_id) as Int)
        }

        rvOffers.adapter = offersAdapter

        offers.addChangeListener { _ -> updateOffersState() }
        updateOffersState()

        this.offers = offers
    }


    private fun updateOffersState() {
        if (offersModel.busy) {
            tvConnecting.text = ""

            tvConnecting.hide(true)
            tvChooseCarrier.hide()
        } else {
            val hasOffers = transferModel.transfer?.offers?.size ?: 0 > 0
            if (!hasOffers) {
                tvConnecting.text = getConnectingToCarriersString(
                        transferModel.transfer?.relevantCarrierProfilesCount)
                tvConnecting.fadeIn()
            } else tvConnecting.hide(true)
            if (hasOffers) tvChooseCarrier.fadeIn() else tvChooseCarrier.hide()
        }
    }


    private var offerID: Int = 0

    private fun showOffer(offerID: Int) {
        this.offerID = offerID

        wv.visibility = INVISIBLE
        btnPay.visibility = VISIBLE

        val offer = offers?.find { it.id == offerID } ?: return
        with(clOffer) {
            tvCarrier.text = offer.carrier?.title(this@TransferActivity)

            val vehicle = offer.vehicle
            if (vehicle != null) {
                val title =
                        if (vehicle.year > 0) vehicle.name + "    " + vehicle.year
                        else vehicle.name
                tvVehicle.text = title
            }

            tvVehicleType.text = transportTypes.typesMap[offer.vehicle?.transportTypeID]?.title

            val facilities = offer.facilities(this@TransferActivity)
            tvWifiRefresh.text = facilities
            tvWifiRefresh.visibility = if (facilities == null) GONE else VISIBLE

            val rating = offer.carrier?.rating
            if (rating != null) {
                rbDriver.rating = rating.drive.toFloat() / 5f
                rbVehicle.rating = rating.vehicle.toFloat() / 5f
                rbFair.rating = rating.fair.toFloat() / 5f
            }

            tvTransfers.text = (offer.carrier?.completedTransfers ?: 0).toString()

            val base = offer.price?.base
            if (base?.preferredCurrency != null) {
                tvPrice.text = offer.price?.base?.preferredCurrency
                tvPriceOff.text = offer.price?.withoutDiscount?.preferredCurrency

                tvPriceOrigin.text = offer.price?.base?.defaultCurrency
                tvPriceOffOrigin.text = offer.price?.withoutDiscount?.defaultCurrency

                tvPriceOrigin.visibility = VISIBLE
                tvPriceOffOrigin.visibility = VISIBLE

                tv100.text = String.format(
                        getString(R.string.pay_n_now),
                        offer.price?.base?.defaultCurrency)
            } else {
                tvPrice.text = offer.price?.base?.defaultCurrency
                tvPriceOff.text = offer.price?.withoutDiscount?.defaultCurrency

                tvPriceOrigin.visibility = GONE
                tvPriceOffOrigin.visibility = GONE

                tv100.text = String.format(
                        getString(R.string.pay_n_now),
                        offer.price?.base?.defaultCurrency)
            }

            tv30.text = String.format(
                    getString(R.string.pay_n_now_n_to_the_driver),
                    offer.price?.p30,
                    offer.price?.p70)
        }

        clOffer.visibility = VISIBLE
        val alphaAnimation = AlphaAnimation(0.0f, 1.0f)
        alphaAnimation.duration = 120
        alphaAnimation.startOffset = 15
        alphaAnimation.fillAfter = true
        val translateAnimation = TranslateAnimation(
                16f * resources.displayMetrics.density, 0f,
                0f, 0f)
        translateAnimation.duration = 120
        translateAnimation.startOffset = 15
        translateAnimation.interpolator = DecelerateInterpolator(2f)
        val set = AnimationSet(true)
        set.addAnimation(alphaAnimation)
        set.addAnimation(translateAnimation)
        clOffer.startAnimation(set)

        svOffer.scrollTo(0, 0)
    }


    private fun pay(offerID: Int) {
        wv.stopLoading()
        wv.loadUrl("about:blank")
        progressBarPayment.visibility = VISIBLE
        disposables.add(
                transferModel.payment(offerID, rb100.isChecked)
                        .subscribe(
                                {
                                    wv.loadUrl(it.data?.url)
                                    wv.postDelayed({
                                        wv.visibility = VISIBLE
                                        progressBarPayment.visibility = INVISIBLE
                                        btnPay.visibility = INVISIBLE
                                    }, 666)
                                },
                                {
                                    Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                                    progressBarPayment.visibility = INVISIBLE
                                }))
    }


    fun back(v: View) {
        finish()
    }

    fun hidePayment(v: View?) {
//        transferModel.pingPayment(1)
//                .subscribe(
//                        {
//                            Toast.makeText(this, it.data?.paymentStatus, Toast.LENGTH_SHORT).show()
//                        })

        val alphaAnimation = AlphaAnimation(1f, 0f)
        alphaAnimation.duration = 90
        val translateAnimation = TranslateAnimation(
                0f, 12f * resources.displayMetrics.density,
                0f, 0f)
        translateAnimation.duration = 90
        translateAnimation.interpolator = AccelerateInterpolator()
        val set = AnimationSet(true)
        set.addAnimation(alphaAnimation)
        set.addAnimation(translateAnimation)
        set.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) = Unit
            override fun onAnimationRepeat(animation: Animation?) = Unit
            override fun onAnimationEnd(animation: Animation?) {
                clOffer.visibility = INVISIBLE
            }
        })

        clOffer.startAnimation(set)
    }

    fun cancel(v: View) {
        svTransfer.fullScroll(FOCUS_UP)
        transferModel.cancel()
    }

    fun showAllDetails(v: View) {
        tvShowAllDetails.visibility = GONE
        ivShowAllDetails.visibility = GONE
        clDetails.visibility = VISIBLE
    }

    fun restore(v: View) {
        transferModel.restore()
    }

    override fun onBackPressed() {
        if (clOffer.visibility == VISIBLE) hidePayment(null)
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
