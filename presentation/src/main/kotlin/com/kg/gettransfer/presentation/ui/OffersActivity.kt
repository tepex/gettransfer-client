package com.kg.gettransfer.presentation.ui

import android.content.Context
import android.graphics.Paint
import android.graphics.drawable.Animatable

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
import com.kg.gettransfer.domain.ApiException
import com.kg.gettransfer.domain.model.TransportType

import com.kg.gettransfer.extensions.*

import com.kg.gettransfer.presentation.adapter.OffersRVAdapter
import com.kg.gettransfer.presentation.adapter.VehiclePhotosVPAdapter
import com.kg.gettransfer.presentation.mapper.TransportTypeMapper
import com.kg.gettransfer.presentation.model.*

import com.kg.gettransfer.presentation.presenter.OffersPresenter

import com.kg.gettransfer.presentation.view.OffersView
import com.kg.gettransfer.presentation.view.OffersView.Sort

import com.kg.gettransfer.utilities.Analytics.Companion.OFFER_DETAILS_RATING

import kotlinx.android.synthetic.main.activity_offers.*
import kotlinx.android.synthetic.main.activity_offers.view.*
import kotlinx.android.synthetic.main.bottom_sheet_offer_details.*
import kotlinx.android.synthetic.main.bottom_sheet_offer_details.view.*
import kotlinx.android.synthetic.main.card_empty_offers.*
import kotlinx.android.synthetic.main.view_transfer_request_info.*

import timber.log.Timber
import java.util.*

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

    override fun setNetworkAvailability(context: Context): Boolean{
        val available = super.setNetworkAvailability(context)
        presenter.checkNewOffers()
        btnBook.isEnabled = !textNetworkNotAvailable.isVisible
        return available
    }

    private fun navigateBackWithTransition() {
        presenter.onBackCommandClick()
        overridePendingTransition(R.anim.transition_l2r, R.anim.transition_r2l)
    }

    override fun setTransfer(transferModel: TransferModel) {
        layoutTransferRequestInfo.setInfo(transferModel)
        fl_drivers_count_text.apply {
            isVisible = true
            tv_drivers_count.text =
                    if (transferModel.relevantCarriersCount?:0 > 4)
                        getString(R.string.LNG_RIDE_CONNECT_CARRIERS, transferModel.relevantCarriersCount)
                    else
                        getString(R.string.LNG_RIDE_CONNECT_CARRIERS_NONUM)
        }
    }

    override fun setDate(date: String) { tvOrderDateTime.text = date }

    override fun setOffers(offers: List<OfferItem>) {
        hideSheetOfferDetails()
        rvOffers.adapter = OffersRVAdapter(offers.toMutableList(), textNetworkNotAvailable.isVisible) { offer, isShowingOfferDetails ->
            presenter.onSelectOfferClicked(offer, isShowingOfferDetails) }
        if (offers.isNotEmpty()) {
            noOffers.isVisible = false
            fl_drivers_count_text.isVisible = false
            cl_fixPrice.isVisible = true
        } else {
            setAnimation()
        }
    }

    private fun setAnimation() {
        noOffers.isVisible = true
        val drawable = ivClock.drawable as Animatable
        drawable.start()
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

    override fun showBottomSheetOfferDetails(offer: OfferItem) {
        when(offer) {
            is OfferModel -> {
                setCarrierId(offer.carrier.id.toString())
                Utils.initCarrierLanguages(layoutCarrierLanguages, offer.carrier.languages)
                setLikeDriver(offer.carrier.approved)
                setRatings(offer)
                setVehicleColor(offer)
                setVehicleName(offer.vehicle.name)
                setVehicleType(offer.vehicle.transportType.nameId!!)
                setPassengers(offer.vehicle.transportType.paxMax)
                setBaggage(offer.vehicle.transportType.luggageMax)

                imgFreeWater.isVisible = offer.refreshments
                imgFreeWiFi.isVisible = offer.wifi

                setPrice(offer.price.base.def)
                setPricePreferred(offer.price.base.def)
                offer.price.withoutDiscount?.let { setWithoutDiscount(it) }

                setOfferCarPhoto(offer)
            }
            is BookNowOfferModel -> {
                setCarrierId("")
                Utils.initCarrierLanguages(layoutCarrierLanguages, listOf(LocaleModel(Locale.ENGLISH)))
                setLikeDriver(true)
                setBookNowRatings()
                setVehicleType(TransportTypeMapper.getNameById(offer.transportType.id))
                setPassengers(offer.transportType.paxMax)
                setBaggage(offer.transportType.luggageMax)
                setPrice(offer.base.def)
                setPricePreferred(offer.base.preferred)
                offer.withoutDiscount?.let { setWithoutDiscount(it) }
                setBookNowPhoto(offer.transportType.id)
                setVehicleName(getString(TransportTypeMapper.getDescriptionById(offer.transportType.id)))
                colorVehicle.isVisible = false
            }
        }
        setRatings()
        btnBook.setOnClickListener {
            presenter.onSelectOfferClicked(offer, false)
            hideSheetOfferDetails()
        }
        bsOfferDetails.state = BottomSheetBehavior.STATE_EXPANDED
    }


    private fun setRatings() {
        layoutSomeRatings.isVisible = false
        layoutRatingAverage.setOnClickListener {
            layoutSomeRatings.apply { isVisible = !isVisible }
            ratingBarAverage.isVisible = layoutSomeRatings.isVisible
            presenter.logButtons(OFFER_DETAILS_RATING)
        }
    }

    private fun setWithoutDiscount(withoutDiscount: MoneyModel) {
        with(offerPriceWithoutDiscountDefault) {
            paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
            text = withoutDiscount.def
        }
        withoutDiscount.preferred?.let { preferred ->
            with(offerPriceWithoutDiscountPreferred) {
                paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
                text = Utils.formatPrice(this@OffersActivity, preferred)
            }
        }
        offerPriceWithoutDiscountPreferred.isVisible = withoutDiscount.preferred != null
        layoutOfferPriceWithoutDiscount.isVisible = true
    }

    private fun setBookNowPhoto(transportTypeId: TransportType.ID) {
        constraintPhotos.visibility = View.GONE
        ivBookNowPhoto.visibility = View.VISIBLE
        ivBookNowPhoto.setImageResource(TransportTypeMapper.getImageById(transportTypeId))
    }

    private fun setOfferCarPhoto(offer: OfferModel) {
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
    }

    private fun setPricePreferred(price: String?) {
        price?.let { offerPricePreferred.text = Utils.formatPrice(this, it) }
        offerPricePreferred.isVisible = price != null
    }

    private fun setPrice(price: String) {
        offerPrice.text = price
    }

    private fun setBaggage(count: Int) {
        sheetOfferDetails.tvCountBaggage.text = Utils.formatLuggage(this, count)
    }

    private fun setPassengers(count: Int) {
        sheetOfferDetails.tvCountPersons.text = Utils.formatPersons(this, count)
    }

    private fun setVehicleType(nameId: Int) {
        vehicleType.text = getString(nameId)
    }

    private fun setVehicleName(name: String) {
        vehicleName.text = name
    }

    private fun setVehicleColor(offer: OfferModel) {
        offer.vehicle.color?.let { colorVehicle.setImageDrawable(Utils.getVehicleColorFormRes(this, it)) }
        colorVehicle.isVisible = offer.vehicle.color != null
    }

    private fun setBookNowRatings() {
        ratingBarAverage.rating = 4f
        ratingBarDriver.rating = 4f
        ratingBarPunctuality.rating = 4f
        ratingBarVehicle.rating = 4f
        ratingBarAverage.isVisible = true
    }

    private fun setRatings(offer: OfferModel) {
        offer.carrier.ratings.average?.let { ratingBarAverage.rating = it }
        offer.carrier.approved.let {
            tvTopSelection.isGone = !it
            ivLikeDriver.isGone = !it
        }
        offer.carrier.ratings.driver?.let { ratingBarDriver.rating = it }
        offer.carrier.ratings.fair?.let { ratingBarPunctuality.rating = it }
        offer.carrier.ratings.vehicle?.let { ratingBarVehicle.rating = it }
        ratingBarAverage.isVisible = true
    }

    private fun setLikeDriver(approved: Boolean) {
        ivLikeDriver.isVisible = approved
    }

    private fun setCarrierId(id: String) {
        carrierId.text = getString(R.string.LNG_CARRIER).plus(" ").plus(id)
    }

    private fun checkNumberOfPhoto(currentPos: Int, size: Int) {
        numberOfPhoto.isVisible = size > 1
        previousImageButton.isVisible = currentPos > 0
        nextImageButton.isVisible = currentPos < size - 1
        numberOfPhoto.text = "${currentPos + 1}/$size"
    }

    private fun hideSheetOfferDetails() { bsOfferDetails.state = BottomSheetBehavior.STATE_HIDDEN }

    /*override fun redirectView() =
        Utils.showScreenRedirectingAlert(this, getString(R.string.log_in_requirement_error_title),
            getString(R.string.log_in_to_see_transfers_and_offers)) { presenter.openLoginView() }*/

    @CallSuper
    override fun onBackPressed() {
        if (bsOfferDetails.state == BottomSheetBehavior.STATE_EXPANDED) hideSheetOfferDetails() else navigateBackWithTransition()
    }

    override fun addNewOffer(offer: OfferModel) { (rvOffers.adapter as OffersRVAdapter).add(offer) }

    companion object {
        val ACTION_NEW_OFFER = "${OffersActivity::class.java.name}.offer"
    }

    override fun setError(e: ApiException) {
        if (e.code != ApiException.NETWORK_ERROR) Utils.showError(this, true, e.details)
    }
}
