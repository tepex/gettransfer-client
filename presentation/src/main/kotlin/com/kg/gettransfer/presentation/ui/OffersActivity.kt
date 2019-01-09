package com.kg.gettransfer.presentation.ui

import android.graphics.Paint

import android.os.Bundle

import android.support.annotation.CallSuper
import android.support.design.widget.BottomSheetBehavior

import android.support.v4.view.ViewPager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.Toolbar

import android.view.MotionEvent
import android.view.View

import android.widget.ImageView
import android.widget.LinearLayout

import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter

import com.kg.gettransfer.R
import com.kg.gettransfer.domain.model.Offer

import com.kg.gettransfer.extensions.*

import com.kg.gettransfer.presentation.adapter.OffersRVAdapter
import com.kg.gettransfer.presentation.adapter.VehiclePhotosVPAdapter

import com.kg.gettransfer.presentation.model.OfferModel
import com.kg.gettransfer.presentation.model.TransferModel

import com.kg.gettransfer.presentation.presenter.OffersPresenter

import com.kg.gettransfer.presentation.view.OffersView
import com.kg.gettransfer.presentation.view.OffersView.Sort

import com.kg.gettransfer.utilities.Analytics.Companion.OFFER_DETAILS_RATING

import kotlinx.android.synthetic.main.activity_offers.*
import kotlinx.android.synthetic.main.bottom_sheet_offer_details.*
import kotlinx.android.synthetic.main.bottom_sheet_offer_details.view.*
import kotlinx.android.synthetic.main.view_transfer_request_info.*

import org.koin.android.ext.android.inject

import timber.log.Timber

class OffersActivity : BaseActivity(), OffersView {

    @InjectPresenter
    internal lateinit var presenter: OffersPresenter

    private lateinit var bsOfferDetails: BottomSheetBehavior<View>

    @ProvidePresenter
    fun createOffersPresenter() = OffersPresenter()

    override fun getPresenter(): OffersPresenter = presenter

    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        presenter.transferId = intent.getLongExtra(OffersView.EXTRA_TRANSFER_ID, 0)

        Timber.d("Start OffersActivity: transfer id: ${presenter.transferId}")

        setContentView(R.layout.activity_offers)

        setToolbar(toolbar as Toolbar, R.string.LNG_RIDE_CARRIERS)

        btnCancelRequest.isVisible = true
        rvOffers.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        bsOfferDetails = BottomSheetBehavior.from(sheetOfferDetails)
        bsOfferDetails.state = BottomSheetBehavior.STATE_HIDDEN

        _tintBackground = tintBackground
        bsOfferDetails.setBottomSheetCallback(bottomSheetCallback)

        viewNetworkNotAvailable = textNetworkNotAvailable

        btnCancelRequest.setOnClickListener               { presenter.onCancelRequestClicked() }
        layoutTransferRequestInfo.setOnClickListener      { presenter.onRequestInfoClicked() }
        sortYear.setOnClickListener                       { presenter.changeSortType(Sort.YEAR) }
        sortRating.setOnClickListener                     { presenter.changeSortType(Sort.RATING) }
        sortPrice.setOnClickListener                      { presenter.changeSortType(Sort.PRICE) }
        (toolbar as Toolbar).setNavigationOnClickListener { navigateBackWithTransition()  }
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            if (bsOfferDetails.state == BottomSheetBehavior.STATE_EXPANDED) {
                if(hideBottomSheet(bsOfferDetails, sheetOfferDetails, BottomSheetBehavior.STATE_HIDDEN, event)) return true
            }
        }
        return super.dispatchTouchEvent(event)
    }

    private fun navigateBackWithTransition() {
        presenter.onBackCommandClick()
        overridePendingTransition(R.anim.transition_l2r, R.anim.transition_r2l)
    }

    override fun setTransfer(transferModel: TransferModel) {
        layoutTransferRequestInfo.setInfo(transferModel)
    }

    override fun setDate(date: String) { tvOrderDateTime.text = date }

    override fun setOffers(offers: List<OfferModel>) {
        rvOffers.adapter = OffersRVAdapter(offers.toMutableList()) { offer, isShowingOfferDetails -> presenter.onSelectOfferClicked(offer, isShowingOfferDetails) }
    }

    override fun setSortState(sortCategory: Sort, sortHigherToLower: Boolean) {
        cleanSortState()
        when (sortCategory) {
            Sort.YEAR   -> selectSort(sortYear, triangleYear, sortHigherToLower)
            Sort.RATING -> selectSort(sortRating, triangleRating, sortHigherToLower)
            Sort.PRICE  -> selectSort(sortPrice, trianglePrice, sortHigherToLower)
        }
    }

    private fun cleanSortState() {
        sortYear.isSelected   = false
        sortRating.isSelected = false
        sortPrice.isSelected  = false

        triangleYear.isVisible   = false
        triangleRating.isVisible = false
        trianglePrice.isVisible  = false
    }

    private fun selectSort(layout: LinearLayout, triangleImage: ImageView, higherToLower: Boolean) {
        layout.isSelected = true
        triangleImage.isVisible = true
        if (!higherToLower) triangleImage.rotation = 180f else triangleImage.rotation = 0f
    }

    override fun showAlertCancelRequest() {
        Utils.showAlertCancelRequest(this) { presenter.cancelRequest(it) }
    }

    override fun showBottomSheetOfferDetails(offer: OfferModel) {
        carrierId.text = getString(R.string.LNG_CARRIER).plus(" ").plus(offer.carrier.id)

        Utils.initCarrierLanguages(layoutCarrierLanguages, offer.carrier.languages)

        ivLikeDriver.isVisible = offer.carrier.approved

        offer.carrier.ratings.average?.let { ratingBarAverage.rating     = it }
        offer.carrier.approved.let         { tvTopSelection.isGone       = !it
                                             ivLikeDriver.isGone         = !it }
        offer.carrier.ratings.driver?.let  { ratingBarDriver.rating      = it }
        offer.carrier.ratings.fair?.let    { ratingBarPunctuality.rating = it }
        offer.carrier.ratings.vehicle?.let { ratingBarVehicle.rating     = it }
        ratingBarAverage.isVisible = true

        offer.vehicle.color?.let { colorVehicle.setImageDrawable(Utils.getVehicleColorFormRes(this, it)) }
        colorVehicle.isVisible = offer.vehicle.color != null

        vehicleName.text = offer.vehicle.name
        vehicleType.text = getString(offer.vehicle.transportType.nameId!!)
        sheetOfferDetails.tvCountPersons.text = Utils.formatPersons(this, offer.vehicle.transportType.paxMax)
        sheetOfferDetails.tvCountBaggage.text = Utils.formatLuggage(this, offer.vehicle.transportType.luggageMax)

        imgFreeWater.isVisible = offer.refreshments
        imgFreeWiFi.isVisible = offer.wifi

        offerPrice.text = offer.price.base.def

        offer.price.base.preferred?.let { offerPricePreferred.text = Utils.formatPrice(this, it) }
        offerPricePreferred.isVisible = offer.price.base.preferred != null

        offer.price.withoutDiscount?.let {
            with(offerPriceWithoutDiscountDefault) {
                paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
                text = it.def
            }
            it.preferred?.let { preferred ->
                with(offerPriceWithoutDiscountPreferred) {
                    paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
                    text = Utils.formatPrice(this@OffersActivity, preferred)
                }
            }
            offerPriceWithoutDiscountPreferred.isVisible = it.preferred != null
        }
        layoutOfferPriceWithoutDiscount.isVisible = offer.price.withoutDiscount != null

        btnBook.setOnClickListener {
            presenter.onSelectOfferClicked(offer, false)
            hideSheetOfferDetails()
        }

        if (offer.vehicle.photos.isNotEmpty()) {
            vpVehiclePhotos.adapter = VehiclePhotosVPAdapter(supportFragmentManager, offer.vehicle.photos)
            checkNumberOfPhoto(0, offer.vehicle.photos.size)

            if (offer.vehicle.photos.size > 1) {
                previousImageButton.setOnClickListener { vpVehiclePhotos.currentItem = vpVehiclePhotos.currentItem - 1 }
                nextImageButton.setOnClickListener { vpVehiclePhotos.currentItem = vpVehiclePhotos.currentItem + 1 }
                vpVehiclePhotos.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
                    override fun onPageScrollStateChanged(p0: Int) {}
                    override fun onPageScrolled(p0: Int, p1: Float, p2: Int) {}
                    override fun onPageSelected(p0: Int) { checkNumberOfPhoto(p0, offer.vehicle.photos.size) }
                })
            }
        }
        layoutPhotos.isVisible = offer.vehicle.photos.isNotEmpty()

        layoutSomeRatings.isVisible = false
        layoutRatingAverage.setOnClickListener {
            layoutSomeRatings.apply { isVisible = !isVisible }
            ratingBarAverage.apply { isVisible = !isVisible }
            presenter.logEvent(OFFER_DETAILS_RATING)
        }
        bsOfferDetails.state = BottomSheetBehavior.STATE_EXPANDED

    }

    private fun checkNumberOfPhoto(currentPos: Int, size: Int) {
        numberOfPhoto.isVisible = size > 1
        previousImageButton.isVisible = currentPos > 0
        nextImageButton.isVisible = currentPos < size - 1
        numberOfPhoto.text = "${currentPos + 1}/$size"
    }

    private fun hideSheetOfferDetails() { bsOfferDetails.state = BottomSheetBehavior.STATE_HIDDEN }

    override fun redirectView() =
        Utils.showScreenRedirectingAlert(this, getString(R.string.log_in_requirement_error_title),
            getString(R.string.log_in_to_see_transfers_and_offers)) { presenter.openLoginView() }

    @CallSuper
    override fun onBackPressed() {
        if (bsOfferDetails.state == BottomSheetBehavior.STATE_EXPANDED) hideSheetOfferDetails() else navigateBackWithTransition()
    }

    override fun addNewOffer(offer: OfferModel) { (rvOffers.adapter as OffersRVAdapter).add(offer) }

    companion object {
        val ACTION_NEW_OFFER = "${OffersActivity::class.java.name}.offer"
    }
}
