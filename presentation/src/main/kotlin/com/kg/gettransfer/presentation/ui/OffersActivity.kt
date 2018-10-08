package com.kg.gettransfer.presentation.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle

import android.support.annotation.CallSuper

import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.Toolbar
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout

import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter

import com.kg.gettransfer.R

import com.kg.gettransfer.domain.interactor.OffersInteractor
import com.kg.gettransfer.domain.interactor.TransferInteractor
import com.kg.gettransfer.presentation.Screens

import com.kg.gettransfer.presentation.adapter.OffersRVAdapter
import com.kg.gettransfer.presentation.model.OfferModel

import com.kg.gettransfer.presentation.model.TransferModel
import com.kg.gettransfer.presentation.presenter.OffersPresenter

import com.kg.gettransfer.presentation.view.OffersView

import kotlinx.android.synthetic.main.activity_offers.*
import kotlinx.android.synthetic.main.toolbar.view.*
import kotlinx.android.synthetic.main.view_transfer_request_info.*

import org.koin.android.ext.android.inject

class OffersActivity: BaseActivity(), OffersView {
    @InjectPresenter
    internal lateinit var presenter: OffersPresenter
    
    private val offersInteractor: OffersInteractor by inject()
    private val transferInteractor: TransferInteractor by inject()
    
    @ProvidePresenter
    fun createOffersPresenter(): OffersPresenter = OffersPresenter(coroutineContexts,
                                                                   router,
                                                                   systemInteractor,
                                                                   transferInteractor,
                                                                   offersInteractor)
    
    protected override var navigator = object : BaseNavigator(this) {
        override fun createActivityIntent(context: Context, screenKey: String, data: Any?): Intent? {
            return if (screenKey == Screens.PAYMENT_SETTINGS) {
                context.getPaymentSettingsActivityLaunchIntent()
            } else {
                null
            }
        }
    }
    
    override fun getPresenter(): OffersPresenter = presenter

    companion object {
        @JvmField val SORT_YEAR = "sort_year"
        @JvmField val SORT_RATING = "sort_rating"
        @JvmField val SORT_PRICE = "sort_price"
    }

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

        btnCancelRequest.setOnClickListener { presenter.onCancelRequestClicked() }
        layoutTransferRequestInfo.setOnClickListener { presenter.onRequestInfoClicked() }
        sortYear.setOnClickListener { presenter.changeSortType(SORT_YEAR) }
        sortRating.setOnClickListener { presenter.changeSortType(SORT_RATING) }
        sortPrice.setOnClickListener { presenter.changeSortType(SORT_PRICE) }
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
        rvOffers.adapter = OffersRVAdapter(offers) { offer -> presenter.onSelectOfferClicked(offer) }
    }

    override fun setSortState(sortCategory: String, sortHigherToLower: Boolean) {
        cleanSortState()
        when(sortCategory){
            SORT_YEAR -> { selectSort(sortYear, triangleYear, sortHigherToLower) }
            SORT_RATING -> { selectSort(sortRating, triangleRating, sortHigherToLower) }
            SORT_PRICE -> { selectSort(sortPrice, trianglePrice, sortHigherToLower) }
        }
    }

    private fun cleanSortState(){
        sortYear.isSelected = false
        triangleYear.visibility = View.GONE
        sortRating.isSelected = false
        triangleRating.visibility = View.GONE
        sortPrice.isSelected = false
        trianglePrice.visibility = View.GONE
    }

    private fun selectSort(layout: LinearLayout, triangleImage: ImageView, higherToLower: Boolean){
        layout.isSelected = true
        triangleImage.visibility = View.VISIBLE
        if(!higherToLower) triangleImage.rotation = 180f
        else triangleImage.rotation = 0f
    }

    override fun showAlertCancelRequest() {
        Utils.showAlertCancelRequest(this){
            isCancel -> presenter.cancelRequest(isCancel)
        }
    }
}
