package com.kg.gettransfer.presentation.ui

import android.os.Bundle

import android.support.annotation.CallSuper
import android.support.design.widget.BottomSheetBehavior

import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.Toolbar

import android.view.View

import android.widget.ImageView
import android.widget.LinearLayout

import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter

import com.kg.gettransfer.R

import com.kg.gettransfer.domain.interactor.OfferInteractor
import com.kg.gettransfer.domain.interactor.TransferInteractor

import com.kg.gettransfer.presentation.adapter.OffersRVAdapter
import com.kg.gettransfer.presentation.model.OfferModel
import com.kg.gettransfer.presentation.model.TransferModel
import com.kg.gettransfer.presentation.presenter.OffersPresenter
import com.kg.gettransfer.presentation.view.OffersView

import kotlinx.android.synthetic.main.activity_offers.*
import kotlinx.android.synthetic.main.bottom_sheet_offer_details.view.*
import kotlinx.android.synthetic.main.toolbar.view.*
import kotlinx.android.synthetic.main.view_transfer_request_info.*

import org.koin.android.ext.android.inject

class OffersActivity: BaseLoadingActivity(), OffersView {
    @InjectPresenter
    internal lateinit var presenter: OffersPresenter

    private val offerInteractor: OfferInteractor by inject()
    private val transferInteractor: TransferInteractor by inject()

    private lateinit var bsOfferDetails: BottomSheetBehavior<View>
    
    @ProvidePresenter
    fun createOffersPresenter(): OffersPresenter = OffersPresenter(coroutineContexts,
                                                                   router,
                                                                   systemInteractor,
                                                                   transferInteractor,
                                                                   offerInteractor)
    
    protected override var navigator = object: BaseNavigator(this) {}
    
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

        bsOfferDetails = BottomSheetBehavior.from(sheetOfferDetails)
        bsOfferDetails.state = BottomSheetBehavior.STATE_HIDDEN

        btnCancelRequest.setOnClickListener { presenter.onCancelRequestClicked() }
        layoutTransferRequestInfo.setOnClickListener { presenter.onRequestInfoClicked() }
        sortYear.setOnClickListener { presenter.changeSortType(OffersPresenter.SORT_YEAR) }
        sortRating.setOnClickListener { presenter.changeSortType(OffersPresenter.SORT_RATING) }
        sortPrice.setOnClickListener { presenter.changeSortType(OffersPresenter.SORT_PRICE) }
    }

    /*private fun setOfferDetailsSheetListener(){
        bsOfferDetails.setBottomSheetCallback(object: BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(p0: View, p1: Float) {

            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when(newState) {
                    BottomSheetBehavior.STATE_DRAGGING -> {
                        //collapsedOrderSheet()
                    }
                }
            }

        })
    }*/
    
    override fun setTransfer(transferModel: TransferModel) {
        //tvConnectingCarriers.text = getString(R.string.transfer_connecting_carriers, transferModel.relevantCarriersCount)
        tvTransferRequestNumber.text = getString(R.string.transfer_order, transferModel.id)
        tvFrom.text = transferModel.from
        tvTo.text = transferModel.to
        tvDistance.text = Utils.formatDistance(this, transferModel.distance, transferModel.distanceUnit)
    }
    
    override fun setDate(date: String) { tvOrderDateTime.text = date }

    override fun setOffers(offers: List<OfferModel>) {
        rvOffers.adapter = OffersRVAdapter(offers) { offer, isShowingOfferDetails -> presenter.onSelectOfferClicked(offer, isShowingOfferDetails) }
    }

    override fun setSortState(sortCategory: String, sortHigherToLower: Boolean) {
        cleanSortState()
        when(sortCategory) {
            OffersPresenter.SORT_YEAR   -> { selectSort(sortYear, triangleYear, sortHigherToLower) }
            OffersPresenter.SORT_RATING -> { selectSort(sortRating, triangleRating, sortHigherToLower) }
            OffersPresenter.SORT_PRICE  -> { selectSort(sortPrice, trianglePrice, sortHigherToLower) }
        }
    }

    private fun cleanSortState() {
        sortYear.isSelected = false
        triangleYear.visibility = View.GONE
        sortRating.isSelected = false
        triangleRating.visibility = View.GONE
        sortPrice.isSelected = false
        trianglePrice.visibility = View.GONE
    }

    private fun selectSort(layout: LinearLayout, triangleImage: ImageView, higherToLower: Boolean) {
        layout.isSelected = true
        triangleImage.visibility = View.VISIBLE
        if(!higherToLower) triangleImage.rotation = 180f
        else triangleImage.rotation = 0f
    }

    override fun showAlertCancelRequest() {
        Utils.showAlertCancelRequest(this) { isCancel -> presenter.cancelRequest(isCancel) }
    }

    override fun showBottomSheetOfferDetails(offer: OfferModel) {
        sheetOfferDetails.carrierId.text = getString(R.string.carrier_number, offer.carrierId)

        sheetOfferDetails.layoutCarrierLanguages.removeAllViews()
        val lp = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        lp.setMargins(8, 0, 8, 0)
        for (item in offer.carrierLanguages){
            val ivLanguage = ImageView(this)
            ivLanguage.setImageResource(Utils.getLanguageImage(item.language))
            ivLanguage.layoutParams = lp
            sheetOfferDetails.layoutCarrierLanguages.addView(ivLanguage)
        }

        sheetOfferDetails.ratingBarDriver.rating = offer.ratings.driver!!.toFloat()
        sheetOfferDetails.ratingBarPunctuality.rating = offer.ratings.fair!!.toFloat()
        sheetOfferDetails.ratingBarVehicle.rating = offer.ratings.vehicle!!.toFloat()
        sheetOfferDetails.vehicleName.text = if(offer.vehicleColor == null) offer.transportName
                                             else Utils.getVehicleNameWithColor(this, offer.transportName, offer.vehicleColor)
        sheetOfferDetails.vehicleType.setText(Utils.getTransportTypeName(offer.transportType))
        sheetOfferDetails.tvCountPersons.text = getString(R.string.count_persons_and_baggage, offer.paxMax)
        sheetOfferDetails.tvCountBaggage.text = getString(R.string.count_persons_and_baggage, offer.baggageMax)
        if(offer.wifi) sheetOfferDetails.imgFreeWiFi.visibility = View.VISIBLE
        if(offer.refreshments) sheetOfferDetails.imgFreeWater.visibility = View.VISIBLE
        sheetOfferDetails.offerPrice.text = offer.priceDefault
        if(offer.pricePreferred != null){
            sheetOfferDetails.offerPricePreferred.text = getString(R.string.preferred_cost, offer.pricePreferred)
            sheetOfferDetails.offerPricePreferred.visibility = View.VISIBLE
        } else sheetOfferDetails.offerPricePreferred.visibility = View.GONE
        sheetOfferDetails.btnBook.setOnClickListener {
            presenter.onSelectOfferClicked(offer, false)
            hideSheetOfferDetails()
        }
        bsOfferDetails.state = BottomSheetBehavior.STATE_EXPANDED
    }

    private fun hideSheetOfferDetails(){
        bsOfferDetails.state = BottomSheetBehavior.STATE_HIDDEN
    }

    @CallSuper
    override fun onBackPressed() {
        if(bsOfferDetails.state == BottomSheetBehavior.STATE_EXPANDED) hideSheetOfferDetails()
        else super.onBackPressed()
    }
}
