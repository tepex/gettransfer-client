package com.kg.gettransfer.presentation.ui

import android.os.Bundle

import android.support.annotation.CallSuper
import android.support.annotation.StringRes

import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatDelegate
import android.support.v7.widget.Toolbar

import android.view.MotionEvent
import android.view.View

import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter

import com.kg.gettransfer.R

import com.kg.gettransfer.domain.interactor.TransferInteractor

import com.kg.gettransfer.presentation.model.OfferModel
import com.kg.gettransfer.presentation.model.RouteModel
import com.kg.gettransfer.presentation.model.TransferModel

import com.kg.gettransfer.presentation.presenter.TransferDetailsPresenter
import com.kg.gettransfer.presentation.view.TransferDetailsView

import kotlinx.android.synthetic.main.activity_transfer_details.*
import kotlinx.android.synthetic.main.toolbar.view.*
import kotlinx.android.synthetic.main.view_transfer_request_info.view.*
import kotlinx.android.synthetic.main.view_transport_type_transfer_details.view.*

import org.koin.android.ext.android.inject

import timber.log.Timber

class TransferDetailsActivity: BaseGoogleMapActivity(), TransferDetailsView {
    @InjectPresenter
    internal lateinit var presenter: TransferDetailsPresenter
    private val transferInteractor: TransferInteractor by inject()

    @ProvidePresenter
    fun createTransferDetailsPresenter(): TransferDetailsPresenter = TransferDetailsPresenter(coroutineContexts, router, systemInteractor, transferInteractor)

    protected override var navigator = object: BaseNavigator(this){}

    init {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
    }

    override fun getPresenter(): TransferDetailsPresenter = presenter
    
    @CallSuper
    protected override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_transfer_details)

        setSupportActionBar(toolbar as Toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        (toolbar as Toolbar).setNavigationOnClickListener { presenter.onBackCommandClick() }
        (toolbar as Toolbar).toolbar_title.text = resources.getString(R.string.activity_transfer_details_title)

        layoutTransferInfo.ivChevron.visibility = View.GONE

        _mapView = mapView
        initGoogleMap(savedInstanceState)
    }
    
    protected override fun customizeGoogleMaps() {
        super.customizeGoogleMaps()

        // https://stackoverflow.com/questions/16974983/google-maps-api-v2-supportmapfragment-inside-scrollview-users-cannot-scroll-th
        transparentImage.setOnTouchListener(View.OnTouchListener { _, motionEvent ->
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    svTransferDetails.requestDisallowInterceptTouchEvent(true)
                    return@OnTouchListener false
                }
                MotionEvent.ACTION_UP -> {
                    svTransferDetails.requestDisallowInterceptTouchEvent(false)
                    return@OnTouchListener true
                }
                MotionEvent.ACTION_MOVE -> {
                    svTransferDetails.requestDisallowInterceptTouchEvent(true)
                    return@OnTouchListener false
                }
            }
            return@OnTouchListener true
        })
    }

    override fun setTransfer(transferModel: TransferModel) {
        layoutTransferInfo.tvTransferRequestNumber.text = getString(R.string.transfer_request_num, transferModel.id)
        layoutTransferInfo.tvFrom.text = transferModel.from
        layoutTransferInfo.tvTo.text = transferModel.to
        layoutTransferInfo.tvOrderDateTime.text = transferModel.dateTime
        layoutTransferInfo.tvDistance.text = Utils.formatDistance(this, transferModel.distance, transferModel.distanceUnit)

        tvCountPassengers.text = transferModel.countPassengers.toString()
        if(transferModel.nameSign != null) {
            tvPassengerName.text = transferModel.nameSign
            layoutName.visibility = View.VISIBLE
        }
        if(transferModel.countChilds > 0) {
            tvCountChilds.text = transferModel.countChilds.toString()
            layoutChilds.visibility = View.VISIBLE
        }
        if(transferModel.flightNumber != null) {
            tvFlightNumber.text = transferModel.flightNumber
            layoutFlightNumber.visibility = View.VISIBLE
        }
        if(transferModel.comment != null) {
            tvComment.text = transferModel.comment
            layoutComment.visibility = View.VISIBLE
        }

        transferModel.transportTypes.forEach {
            var viewTransportType = layoutInflater.inflate(R.layout.view_transport_type_transfer_details, null, false)
            viewTransportType.tvNameTransportType.text = it.id
            viewTransportType.tvCountPersons.text = getString(R.string.count_persons_and_baggage, it.paxMax)
            viewTransportType.tvCountBaggage.text = getString(R.string.count_persons_and_baggage, it.luggageMax)
            layoutTransportTypesList.addView(viewTransportType)
        }
        
        paymentInfoPaid.text = getString(R.string.activity_transfer_details_paid_sum,
                                         transferModel.paidSum,
                                         transferModel.paidPercentage)
        paymentInfoPay.text = transferModel.remainToPay
        paymentInfoSum.text = transferModel.price
        layoutPaymentInfo.visibility = View.VISIBLE
    }

    override fun setButtonCancelVisible(visible: Boolean) {
        if(visible) btnCancel.visibility = View.VISIBLE else btnCancel.visibility = View.GONE
    }

    override fun setOffer(offerModel: OfferModel) {
        offerDriverInfoEmail.text = offerModel.driverEmail
        offerDriverInfoPhone.text = offerModel.driverPhone
        offerDriverInfoName.text = offerModel.driverName
        layoutOfferDriverInfo.visibility = View.VISIBLE

        offerTransportInfoCarType.text = offerModel.transportType
        offerTransportInfoCarName.text = offerModel.transportName
        offerTransportInfoCarNumber.text = offerModel.transportNumber
        offerTransportInfoPrice.text = offerModel.price
        layoutOfferTransportInfo.visibility = View.VISIBLE
    }

    override fun setRoute(routeModel: RouteModel) { Utils.setPins(this, googleMap, routeModel) }
}
