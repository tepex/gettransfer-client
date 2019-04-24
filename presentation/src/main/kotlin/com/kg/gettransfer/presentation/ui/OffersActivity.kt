package com.kg.gettransfer.presentation.ui

import android.content.Context
import android.graphics.drawable.Animatable

import android.os.Bundle

import android.support.annotation.CallSuper
import android.support.design.widget.BottomSheetBehavior

import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.Toolbar

import android.view.MotionEvent
import android.view.View

import android.widget.ImageView
import android.widget.RelativeLayout

import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions

import com.kg.gettransfer.R
import com.kg.gettransfer.domain.ApiException

import com.kg.gettransfer.extensions.*
import com.kg.gettransfer.presentation.adapter.OffersAdapter

import com.kg.gettransfer.presentation.mapper.TransportTypeMapper
import com.kg.gettransfer.presentation.model.*

import com.kg.gettransfer.presentation.presenter.OffersPresenter
import com.kg.gettransfer.presentation.ui.custom.RatingFieldView
import com.kg.gettransfer.presentation.ui.helpers.HourlyValuesHelper
import com.kg.gettransfer.presentation.ui.helpers.ScrollGalleryInflater

import com.kg.gettransfer.presentation.view.OffersView
import com.kg.gettransfer.presentation.view.OffersView.Sort

import kotlinx.android.synthetic.main.activity_offers.*
import kotlinx.android.synthetic.main.activity_offers.view.*
import kotlinx.android.synthetic.main.bottom_sheet_offer_details.view.*

import kotlinx.android.synthetic.main.bottom_sheet_offers.*
import kotlinx.android.synthetic.main.card_empty_offers.*
import kotlinx.android.synthetic.main.toolbar_nav.view.*
import kotlinx.android.synthetic.main.vehicle_items.view.*
import kotlinx.android.synthetic.main.view_offer_bottom.view.*
import kotlinx.android.synthetic.main.view_offer_conditions.view.*
import kotlinx.android.synthetic.main.view_offer_rating_details.*
import kotlinx.android.synthetic.main.view_offer_rating_field.*
import kotlinx.android.synthetic.main.view_transport_capacity.view.*

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
        initToolBar()
        initAdapter()
        initBottomSheet()
        initClickListeners()
        viewNetworkNotAvailable = textNetworkNotAvailable
        intent.getStringExtra(OffersView.EXTRA_ORIGIN)?.let { presenter.isViewRoot = true }
    }

    private fun initClickListeners() {
        sortYear.setOnClickListener                       { presenter.changeSortType(Sort.YEAR) }
        sortRating.setOnClickListener                     { presenter.changeSortType(Sort.RATING) }
        sortPrice.setOnClickListener                      { presenter.changeSortType(Sort.PRICE) }
    }

    private fun initToolBar() =
            with(toolbar) {
                setSupportActionBar(this as Toolbar)
                btn_back.setOnClickListener { navigateBackWithTransition() }
                btn_forward.setOnClickListener { presenter.onRequestInfoClicked() }
                tv_title.isSelected = true
            }


    private fun initBottomSheet() {
        bsOfferDetails = BottomSheetBehavior.from(sheetOfferDetails)
        bsOfferDetails.state = BottomSheetBehavior.STATE_HIDDEN
        _tintBackground = tintBackground
        bsOfferDetails.setBottomSheetCallback(bottomSheetCallback)
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
        toolbar.tv_title.text = transferModel.from
                .let { from ->
                    transferModel.to?.let {
                        from.plus(" - ").plus(it)
                    } ?: transferModel.duration?.let {
                        from.plus(" - ").plus(HourlyValuesHelper.getValue(it, this))
                    } ?: from
                }
        toolbar.tv_subtitle.text = SystemUtils.formatDateTime(transferModel.dateTime)
        fl_drivers_count_text.apply {
            tv_drivers_count.text =
                    if (transferModel.relevantCarriersCount ?: 0 > 4)
                        getString(R.string.LNG_RIDE_CONNECT_CARRIERS, transferModel.relevantCarriersCount)
                    else
                        getString(R.string.LNG_RIDE_CONNECT_CARRIERS_NONUM)
        }
    }

    private fun initAdapter() {
        rvOffers.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
    }

    override fun setOffers(offers: List<OfferItem>) {
        hideSheetOfferDetails()
        rvOffers.adapter = OffersAdapter(offers.toMutableList()) { offer, showDetails -> presenter.onSelectOfferClicked(offer, showDetails) }
        if (offers.isNotEmpty()) {
            noOffers.isVisible = false
            fl_drivers_count_text.isVisible = false
            cl_fixPrice.isVisible = viewNetworkNotAvailable?.isVisible?.not() ?: true
        } else {
            setAnimation()
            fl_drivers_count_text.isVisible = viewNetworkNotAvailable?.isVisible?.not() ?: true
        }
    }

    override fun setBannersVisible(hasOffers: Boolean) {
        if (hasOffers) cl_fixPrice.isVisible = true
        else fl_drivers_count_text.isVisible = true
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

        triangleYear.isInvisible   = true
        triangleRating.isInvisible = true
        trianglePrice.isInvisible  = true
    }

    private fun selectSort(layout: RelativeLayout, triangleImage: ImageView, higherToLower: Boolean) {
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
                setVehicleName(vehicle = offer.vehicle)
                Utils.initCarrierLanguages(languages_container_bs, offer.carrier.languages)
                setCapacity(offer.vehicle.transportType)
                with(offer_conditions_bs.vehicle_conveniences) {
                    imgFreeWater.isVisible = offer.refreshments
                    imgFreeWiFi.isVisible = offer.wifi
                    imgCharge.isVisible = offer.charger
                    isVisible = offer.refreshments || offer.wifi || offer.charger
                }
                setWithoutDiscount(offer.price.withoutDiscount)
                setPrice(offer.price.base.preferred ?: offer.price.base.def)
                addOfferPhotos(offer.vehicle.photos)
                setRating(offer.carrier)
            }
            is BookNowOfferModel -> {
                setVehicleName(nameById = getString(TransportTypeMapper.getModelsById(offer.transportType.id)))
                Utils.initCarrierLanguages(languages_container_bs, listOf(LocaleModel.BOOK_NOW_LOCALE_DEFAULT))
                setCapacity(offer.transportType)
                setPrice(offer.base.preferred ?: offer.base.def)
                setWithoutDiscount(offer.withoutDiscount)
                view_offer_rating_bs.isVisible = false
                offer_ratingDivider_bs.isVisible = false
                addSinglePhoto(resId = TransportTypeMapper.getImageById(offer.transportType.id))
            }
        }

        offer_bottom_bs.btn_book.setOnClickListener {
            presenter.onSelectOfferClicked(offer, false)
            hideSheetOfferDetails()
        }
        bsOfferDetails.state = BottomSheetBehavior.STATE_EXPANDED
    }

    private fun setWithoutDiscount(withoutDiscount: MoneyModel?) {
        with(offer_bottom_bs) {
            if (withoutDiscount != null)
                tv_old_price.strikeText = withoutDiscount.preferred ?: withoutDiscount.def
            tv_old_price.isVisible = withoutDiscount != null
        }
    }

    private fun addOfferPhotos(paths: List<String>){
        photos_container_bs.removeAllViews()
        iv_offer_bs_booknow.isVisible = false
        val hasPhotos = paths.isNotEmpty()
        sv_photo.isVisible = hasPhotos
        if (hasPhotos) {
            if (paths.size == 1) {
                addSinglePhoto(path = paths.first())
            }
            else {
                val size = getPhotoSize()
                inflatePhotoScrollView(paths.size)
                for (i in 0 until photos_container_bs.childCount) {
                    Glide.with(this)
                            .load(paths[i])
                            .apply(RequestOptions().transforms(CenterCrop(),
                                    RoundedCorners(Utils.dpToPxInt(this, PHOTO_CORNER)))
                                    .override(size.first, size.second))
                            .into(photos_container_bs.getChildAt(i) as ImageView)
                }
            }
        }
    }

    private fun addSinglePhoto(resId: Int = 0, path: String? = null) {
        sv_photo.isVisible = false
        iv_offer_bs_booknow.isVisible = true
        val size = getPhotoSize()
        Glide.with(this)
                .let {
                    if (path != null) it.load(path)
                    else it.load(resId)
                }
                .apply(RequestOptions().transforms(CenterCrop(),
                        RoundedCorners(Utils.dpToPxInt(this, PHOTO_CORNER)))
                        .override(size.first, size.second))
                .into(iv_offer_bs_booknow)
    }

    private fun getPhotoSize(): Pair<Int, Int> {
        val imgHorizontalMargins = resources.getDimensionPixelSize(R.dimen.bottom_sheet_offer_details_margin_16dp)
        val imgWidth = resources.displayMetrics.widthPixels - imgHorizontalMargins * 2
        val imgHeight = resources.getDimensionPixelSize(R.dimen.bottom_sheet_offer_details_sv_photo_height)
        return Pair(imgWidth, imgHeight)
    }

    private fun inflatePhotoScrollView(imagesCount: Int) {
        ScrollGalleryInflater.addImageViews(imagesCount, photos_container_bs)
    }

    private fun setPrice(price: String) {
        offer_bottom_bs.tv_current_price.text = price
    }

    private fun setCapacity(transport: TransportTypeModel) {
        with(offer_conditions_bs.view_capacity) {
            transportType_сountPassengers.text = "x".plus(transport.paxMax)
            transportType_сountBaggage.text = "x".plus(transport.luggageMax)
        }
    }

    private fun setVehicleName(nameById: String? = null, vehicle: VehicleModel? = null) {
        if (nameById == null && vehicle == null) throw IllegalArgumentException()
        tv_car_model_bs.text =
                nameById ?:
                        vehicle!!.color?.let { Utils.getVehicleNameWithColor(this, vehicle.name, it) } ?:
                        vehicle!!.name
    }

    private fun setRating(carrier: CarrierModel) {
        val hasRating = presenter.hasAnyRate(carrier)
        offer_ratingDivider_bs.isVisible = hasRating
        view_offer_rating_bs.isVisible = hasRating
        if (!hasRating) return

        carrier.ratings.let { ratings ->
            ratings.driver?.let { if (it != NO_RATE) setRating(it, ratingDriver) }
            ratings.fair?.let { if (it != NO_RATE) setRating(it, ratingPunctuality) }
            ratings.vehicle?.let { if (it != NO_RATE) setRating(it, ratingVehicle) }
        }
        layoutTopSelection.isVisible = carrier.approved
    }

    private fun setRating(rate: Float, ratingLayout: RatingFieldView) {
        with(ratingLayout) {
            ratingBar.visibleRating = rate
            ratingNumber.text = rate.toString().replace(".", ",")
            isVisible = true
        }
    }

    private fun hideSheetOfferDetails() { bsOfferDetails.state = BottomSheetBehavior.STATE_HIDDEN }

    @CallSuper
    override fun onBackPressed() {
        if (bsOfferDetails.state == BottomSheetBehavior.STATE_EXPANDED) hideSheetOfferDetails() else navigateBackWithTransition()
    }

    override fun addNewOffer(offer: OfferModel) { (rvOffers.adapter as OffersAdapter).add(offer) }

    override fun setError(e: ApiException) {
        if (e.code != ApiException.NETWORK_ERROR) Utils.showError(this, true, e.details)
    }

    override fun setNetworkAvailability(context: Context): Boolean {
        val available = super.setNetworkAvailability(context)
        if (available) presenter.checkNewOffers()
        offer_bottom_bs.btn_book.isEnabled = !textNetworkNotAvailable.isVisible
        if (available) presenter.updateBanners()
        else {
            cl_fixPrice.isVisible = false
            fl_drivers_count_text.isVisible = false
        }
        return available
    }

    companion object {
        const val PHOTO_CORNER = 7F
        const val NO_RATE      = 0f
    }
}