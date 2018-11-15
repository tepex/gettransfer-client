package com.kg.gettransfer.presentation.ui

import android.os.Bundle

import android.support.annotation.CallSuper
import android.support.design.widget.BottomSheetBehavior
import android.support.v4.view.ViewPager
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
import com.kg.gettransfer.presentation.adapter.VehiclePhotosVPAdapter

import com.kg.gettransfer.presentation.model.OfferModel
import com.kg.gettransfer.presentation.model.TransferModel

import com.kg.gettransfer.presentation.presenter.OffersPresenter
import com.kg.gettransfer.presentation.view.OffersView

import com.kg.gettransfer.service.OfferServiceConnection

import kotlinx.android.synthetic.main.activity_offers.*
import kotlinx.android.synthetic.main.bottom_sheet_offer_details.*
import kotlinx.android.synthetic.main.bottom_sheet_offer_details.view.*
import kotlinx.android.synthetic.main.toolbar.view.*
import kotlinx.android.synthetic.main.view_transfer_request_info.*

import org.koin.android.ext.android.inject

import timber.log.Timber

class OffersActivity: BaseActivity(), OffersView {
    companion object {
        const val TRANSFER_ID = "transfer_id"
    }
    
    @InjectPresenter
    internal lateinit var presenter: OffersPresenter

    private val offerInteractor: OfferInteractor by inject()
    private val transferInteractor: TransferInteractor by inject()
    private val offerServiceConnection: OfferServiceConnection by inject()

    private lateinit var bsOfferDetails: BottomSheetBehavior<View>
    
    @ProvidePresenter
    fun createOffersPresenter(): OffersPresenter = OffersPresenter(coroutineContexts,
                                                                   router,
                                                                   systemInteractor,
                                                                   transferInteractor,
                                                                   offerInteractor)

    override var navigator = object: BaseNavigator(this) {}
    
    override fun getPresenter(): OffersPresenter = presenter

    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_offers)

        setSupportActionBar(toolbar as Toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        (toolbar as Toolbar).toolbar_title.setText(R.string.LNG_RIDE_CARRIERS)
        (toolbar as Toolbar).setNavigationOnClickListener { presenter.onBackCommandClick() }

        btnCancelRequest.visibility = View.VISIBLE
        rvOffers.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        bsOfferDetails = BottomSheetBehavior.from(sheetOfferDetails)
        bsOfferDetails.state = BottomSheetBehavior.STATE_HIDDEN

        viewNetworkNotAvailable = textNetworkNotAvailable

        btnCancelRequest.setOnClickListener { presenter.onCancelRequestClicked() }
        layoutTransferRequestInfo.setOnClickListener { presenter.onRequestInfoClicked() }
        sortYear.setOnClickListener   { presenter.changeSortType(OffersPresenter.SORT_YEAR) }
        sortRating.setOnClickListener { presenter.changeSortType(OffersPresenter.SORT_RATING) }
        sortPrice.setOnClickListener  { presenter.changeSortType(OffersPresenter.SORT_PRICE) }

        setOfferDetailsSheetListener()
        
        presenter.transferId = intent.getLongExtra(TRANSFER_ID, -1)
        Timber.d("OffersActivity for transfer: ${presenter.transferId}")
    }

    @CallSuper
    protected override fun onStart() {
        super.onStart()
        systemInteractor.addListener(offerServiceConnection)
        offerServiceConnection.connectionChanged(systemInteractor.endpoint, systemInteractor.accessToken)
        offerServiceConnection.connect(this) { newOffer -> presenter.onNewOffer(newOffer) }
    }
    
    @CallSuper
    protected override fun onStop() {
        offerServiceConnection.disconnect(this)
        systemInteractor.removeListener(offerServiceConnection)
        super.onStop()
    }
    
    private fun setOfferDetailsSheetListener() {
        bsOfferDetails.setBottomSheetCallback(object: BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(p0: View, p1: Float) {}

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when(newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        for(frag in supportFragmentManager.fragments) {
                            vpVehiclePhotos.currentItem = 0
                            supportFragmentManager.beginTransaction().remove(frag).commit()
                        }
                    }
                }
            }
        })
    }
    
    override fun setTransfer(transferModel: TransferModel) {
        //tvConnectingCarriers.text = getString(R.string.transfer_connecting_carriers, transferModel.relevantCarriersCount)
        tvTransferRequestNumber.text = getString(R.string.LNG_RIDE_NUMBER).plus(transferModel.id)
        tvFrom.text = transferModel.from
        if(transferModel.to != null) {
            tvTo.text = transferModel.to
            tvDistance.text = Utils.formatDistance(this, transferModel.distance, transferModel.distanceUnit)
        } else if(transferModel.duration != null){
            tvTo.text = getString(R.string.LNG_TIME_RIDE)
            tvDistance.text = Utils.formatDuration(this, transferModel.duration)
        }
        //tvTo.text = transferModel.to
        //tvDistance.text = Utils.formatDistance(this, transferModel.distance, transferModel.distanceUnit)
    }
    
    override fun setDate(date: String) { tvOrderDateTime.text = date }

    override fun setOffers(offers: List<OfferModel>) {
        rvOffers.adapter = OffersRVAdapter(offers.toMutableList()) { offer, isShowingOfferDetails -> presenter.onSelectOfferClicked(offer, isShowingOfferDetails) }
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
        carrierId.text = getString(R.string.LNG_CARRIER).plus(" ").plus(offer.carrier.id)

        layoutCarrierLanguages.removeAllViews()
        val lp = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        lp.setMargins(8, 0, 8, 0)
        for(item in offer.carrier.languages) {
            val ivLanguage = ImageView(this)
            ivLanguage.setImageResource(Utils.getLanguageImage(item.delegate.language))
            ivLanguage.layoutParams = lp
            layoutCarrierLanguages.addView(ivLanguage)
        }

        offer.carrier.ratings.average?.let { ratingBarAverage.rating = it }
        offer.carrier.ratings.driver?.let  { ratingBarDriver.rating = it }
        offer.carrier.ratings.fair?.let    { ratingBarPunctuality.rating = it }
        offer.carrier.ratings.vehicle?.let { ratingBarVehicle.rating = it }

        vehicleName.text = offer.vehicle.vehicleBase.name
        if(offer.vehicle.color != null) colorVehicle.setImageDrawable(Utils.getVehicleColorFormRes(this, offer.vehicle.color))
        vehicleType.text = getString(offer.vehicle.transportType.nameId!!)
        sheetOfferDetails.tvCountPersons.text = Utils.formatPersons(this, offer.vehicle.transportType.paxMax)
        sheetOfferDetails.tvCountBaggage.text = Utils.formatLuggage(this, offer.vehicle.transportType.luggageMax)

        if(offer.wifi) imgFreeWiFi.visibility = View.VISIBLE
        else imgFreeWiFi.visibility = View.GONE
        if(offer.refreshments) imgFreeWater.visibility = View.VISIBLE
        else imgFreeWater.visibility = View.GONE

        offerPrice.text = offer.price.base.default
        if(offer.price.base.preferred != null) {
            offerPricePreferred.text = Utils.formatPrice(this, offer.price.base.preferred)
            offerPricePreferred.visibility = View.VISIBLE
        } else offerPricePreferred.visibility = View.GONE

        btnBook.setOnClickListener {
            presenter.onSelectOfferClicked(offer, false)
            hideSheetOfferDetails()
        }

        if(offer.vehicle.photos.isEmpty()) layoutPhotos.visibility = View.GONE
        else {
            layoutPhotos.visibility = View.VISIBLE
            vpVehiclePhotos.adapter = VehiclePhotosVPAdapter(supportFragmentManager, offer.vehicle.photos)
            checkNumberOfPhoto(0, offer.vehicle.photos.size)

            if(offer.vehicle.photos.size > 1) {
                previousImageButton.setOnClickListener { vpVehiclePhotos.currentItem = vpVehiclePhotos.currentItem - 1 }
                nextImageButton.setOnClickListener { vpVehiclePhotos.currentItem = vpVehiclePhotos.currentItem + 1 }
                vpVehiclePhotos.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
                    override fun onPageScrollStateChanged(p0: Int) {}
                    override fun onPageScrolled(p0: Int, p1: Float, p2: Int) {}
                    override fun onPageSelected(p0: Int) { checkNumberOfPhoto(p0, offer.vehicle.photos.size) }
                })
            }
        }

        layoutSomeRatings.visibility = View.GONE
        layoutRatingAverage.setOnClickListener {
            if(layoutSomeRatings.visibility == View.VISIBLE) {
                layoutSomeRatings.visibility = View.GONE
                ratingBarAverage.visibility = View.VISIBLE
            } else {
                layoutSomeRatings.visibility = View.VISIBLE
                ratingBarAverage.visibility = View.GONE
            }
        }
        bsOfferDetails.state = BottomSheetBehavior.STATE_EXPANDED
    }

    private fun checkNumberOfPhoto(currentPos: Int, size: Int) {
        if(size == 1) numberOfPhoto.visibility = View.GONE

        if(currentPos == 0) previousImageButton.visibility = View.GONE
        else previousImageButton.visibility = View.VISIBLE

        if(currentPos == size - 1) nextImageButton.visibility = View.GONE
        else nextImageButton.visibility = View.VISIBLE

        numberOfPhoto.text = "${currentPos + 1}/$size"
    }

    private fun hideSheetOfferDetails() { bsOfferDetails.state = BottomSheetBehavior.STATE_HIDDEN }

    override fun redirectView() =
        Utils.showScreenRedirectingAlert(this, getString(R.string.log_in_requirement_error_title),
                getString(R.string.log_in_to_see_transfers_and_offers)) { presenter.openLoginView() }


    @CallSuper
    override fun onBackPressed() {
        if(bsOfferDetails.state == BottomSheetBehavior.STATE_EXPANDED) hideSheetOfferDetails()
        else super.onBackPressed()
    }

    override fun addNewOffer(offer: OfferModel) { (rvOffers.adapter as OffersRVAdapter).add(offer) }
}
