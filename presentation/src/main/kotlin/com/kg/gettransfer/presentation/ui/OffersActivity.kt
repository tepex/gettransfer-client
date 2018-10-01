package com.kg.gettransfer.presentation.ui

import android.content.Context
import android.content.Intent

import android.graphics.PorterDuff
import android.graphics.drawable.LayerDrawable

import android.os.Bundle

import android.support.annotation.CallSuper
import android.support.annotation.StringRes

import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.Toolbar
import android.view.View

import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter

import com.kg.gettransfer.R
//import com.kg.gettransfer.R.id.ratingBar

import com.kg.gettransfer.domain.interactor.TransferInteractor
import com.kg.gettransfer.domain.model.Offer

import com.kg.gettransfer.presentation.Screens
import com.kg.gettransfer.presentation.adapter.OffersRVAdapter
import com.kg.gettransfer.presentation.adapter.RequestsRVAdapter
import com.kg.gettransfer.presentation.model.OfferModel

import com.kg.gettransfer.presentation.model.TransferModel
import com.kg.gettransfer.presentation.presenter.OffersPresenter

import com.kg.gettransfer.presentation.view.OffersView

import kotlinx.android.synthetic.main.activity_offers.*
import kotlinx.android.synthetic.main.toolbar.view.*
import kotlinx.android.synthetic.main.view_offer.view.*
import kotlinx.android.synthetic.main.view_offer.*
import kotlinx.android.synthetic.main.view_transfer_request_info.*

import org.koin.android.ext.android.inject

class OffersActivity: BaseActivity(), OffersView {
    @InjectPresenter
    internal lateinit var presenter: OffersPresenter
    
    private val transferInteractor: TransferInteractor by inject()
    
    @ProvidePresenter
    fun createOffersPresenter(): OffersPresenter = OffersPresenter(coroutineContexts,
                                                                   router,
                                                                   systemInteractor,
                                                                   transferInteractor)
    
    protected override var navigator = BaseNavigator(this)
    
    override fun getPresenter(): OffersPresenter = presenter

    @CallSuper
    protected override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_offers)

        setSupportActionBar(toolbar as Toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        (toolbar as Toolbar).toolbar_title.setText(R.string.carrier_offers)
        (toolbar as Toolbar).setNavigationOnClickListener { presenter.onBackCommandClick() }

        btnCancelRequest.visibility = View.VISIBLE
        rvOffers.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        /*val stars = ratingBarOffer.progressDrawable as LayerDrawable
        stars.getDrawable(2).setColorFilter(ContextCompat.getColor(this, R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP)*/

        btnCancelRequest.setOnClickListener {  }
        layoutTransferRequestInfo.setOnClickListener { presenter.onRequestInfoClicked() }
    }
    
    override fun setTransfer(transferModel: TransferModel) {
        //tvConnectingCarriers.text = getString(R.string.transfer_connecting_carriers, transferModel.relevantCarriersCount)
        tvTransferRequestNumber.text = getString(R.string.transfer_order, transferModel.id)
        tvFrom.text = transferModel.from
        tvTo.text = transferModel.to
        tvDistance.text = Utils.formatDistance(this, transferModel.distance, transferModel.distanceUnit)
    }
    
    override fun setDate(date: String) { tvOrderDateTime.text = date }

    override fun setOffers(offers: List<OfferModel>) {
        rvOffers.adapter = OffersRVAdapter(offers) { offer -> presenter.onSelectOfferClicked() }
    }
}
