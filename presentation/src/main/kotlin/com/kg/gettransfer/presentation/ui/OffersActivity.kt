package com.kg.gettransfer.presentation.ui

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Animatable
import android.os.Bundle

import androidx.annotation.CallSuper
import com.google.android.material.bottomsheet.BottomSheetBehavior
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.appcompat.widget.Toolbar

import android.view.MotionEvent
import android.view.View
import androidx.recyclerview.widget.RecyclerView

import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter

import com.kg.gettransfer.R
import com.kg.gettransfer.domain.ApiException

import com.kg.gettransfer.extensions.isVisible
import com.kg.gettransfer.extensions.setThrottledClickListener
import com.kg.gettransfer.extensions.strikeText
import com.kg.gettransfer.extensions.toHalfEvenRoundedFloat

import com.kg.gettransfer.domain.model.Money

import com.kg.gettransfer.presentation.adapter.OffersAdapter
import com.kg.gettransfer.presentation.delegate.Either
import com.kg.gettransfer.presentation.delegate.OfferItemBindDelegate

import com.kg.gettransfer.presentation.model.TransferModel
import com.kg.gettransfer.presentation.model.OfferItemModel
import com.kg.gettransfer.presentation.model.OfferModel
import com.kg.gettransfer.presentation.model.BookNowOfferModel
import com.kg.gettransfer.presentation.model.LocaleModel
import com.kg.gettransfer.presentation.model.CarrierModel
import com.kg.gettransfer.presentation.model.TransportTypeModel
import com.kg.gettransfer.presentation.model.VehicleModel
import com.kg.gettransfer.presentation.model.getImageRes
import com.kg.gettransfer.presentation.model.getModelsRes

import com.kg.gettransfer.presentation.presenter.OffersPresenter

import com.kg.gettransfer.presentation.ui.custom.RatingFieldView
import com.kg.gettransfer.presentation.ui.helpers.HourlyValuesHelper
import com.kg.gettransfer.presentation.ui.helpers.LanguageDrawer

import com.kg.gettransfer.presentation.view.OffersView
import com.kg.gettransfer.presentation.view.OffersView.Sort

import kotlinx.android.synthetic.main.activity_offers.*
import kotlinx.android.synthetic.main.bottom_sheet_offers.*
import kotlinx.android.synthetic.main.bottom_sheet_offers.view.*
import kotlinx.android.synthetic.main.card_empty_offers.*
import kotlinx.android.synthetic.main.drivers_count.*
import kotlinx.android.synthetic.main.toolbar_nav_offers.view.*
import kotlinx.android.synthetic.main.vehicle_items.view.*
import kotlinx.android.synthetic.main.view_offer_bottom.view.*
import kotlinx.android.synthetic.main.view_offer_rating_details.*
import kotlinx.android.synthetic.main.view_offer_rating_field.*
import kotlinx.android.synthetic.main.view_transport_capacity.view.transportType_сountBaggage
import kotlinx.android.synthetic.main.view_transport_capacity.view.transportType_сountPassengers

import timber.log.Timber

@Suppress("TooManyFunctions")
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
        initSelectingSortTypeLayout()
        viewNetworkNotAvailable = layoutTextNetworkNotAvailable
        intent.getStringExtra(OffersView.EXTRA_ORIGIN)?.let { presenter.isViewRoot = true }
    }

    private fun initToolBar() =
        with(toolbar) {
            @Suppress("UnsafeCast")
            setSupportActionBar(this as Toolbar)
            btn_back.setOnClickListener { navigateBackWithTransition() }
            btn_request_info.setThrottledClickListener { presenter.onRequestInfoClicked() }
            tv_title.isSelected = true
        }

    private fun initBottomSheet() {
        bsOfferDetails = BottomSheetBehavior.from(sheetOfferDetails)
        bsOfferDetails.state = BottomSheetBehavior.STATE_HIDDEN
        tintBackgroundShadow = tintBackground
        bsOfferDetails.setBottomSheetCallback(bottomSheetCallback)
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            if (bsOfferDetails.state == BottomSheetBehavior.STATE_EXPANDED) {
                if (hideBottomSheet(
                        bsOfferDetails,
                        sheetOfferDetails,
                        BottomSheetBehavior.STATE_HIDDEN,
                        event
                    )
                ) return true
            }
        }
        return super.dispatchTouchEvent(event)
    }

    private fun navigateBackWithTransition() {
        presenter.onBackCommandClick()
        overridePendingTransition(R.anim.transition_l2r, R.anim.transition_r2l)
    }

    override fun setTransfer(transferModel: TransferModel) {
        toolbar.tv_title.text = transferModel.from.let { from ->
            transferModel.to?.let { "$from - $it" } ?: transferModel.duration?.let { duration ->
                "$from - ${HourlyValuesHelper.getValue(duration, this)}"
            } ?: from
        }
        toolbar.tv_subtitle.text = SystemUtils.formatDateTime(transferModel.dateTime)
        fl_drivers_count_text.apply {
            tv_drivers_count.text =
                if (transferModel.relevantCarriersCount ?: 0 > MIN_CARRIERS_COUNT) {
                    getString(R.string.LNG_RIDE_CONNECT_CARRIERS, transferModel.relevantCarriersCount)
                } else {
                    getString(R.string.LNG_RIDE_CONNECT_CARRIERS_NONUM)
                }
        }
    }

    private fun initAdapter() {
        rvOffers.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        swipe_rv_offers_container.setOnRefreshListener { presenter.checkNewOffers(true) }
    }

    override fun setOffers(offers: List<OfferItemModel>) {
        hideSheetOfferDetails()
        setupAdapter(offers)
        setupPriceOrDriversInfo(offers)
    }

    private fun setupPriceOrDriversInfo(offers: List<OfferItemModel>) {
        if (offers.isNotEmpty()) {
            groupFilter.isVisible = true
            noOffers.isVisible = false
            fl_drivers_count_text.isVisible = false
            cl_fixPrice.isVisible = viewNetworkNotAvailable?.isShowing()?.not() ?: true
        } else {
            groupFilter.isVisible = false
            setAnimation()
            fl_drivers_count_text.isVisible = viewNetworkNotAvailable?.isShowing()?.not() ?: true
        }
    }

    private fun setupAdapter(offers: List<OfferItemModel>) {
        rvOffers.adapter = OffersAdapter(offers.toMutableList(), presenter::onSelectOfferClicked)
    }

    override fun setBannersVisible(hasOffers: Boolean) {
        if (hasOffers) cl_fixPrice.isVisible = true else fl_drivers_count_text.isVisible = true
    }

    private fun setAnimation() {
        noOffers.isVisible = true
        val drawable = ivClock.drawable
        if (drawable is Animatable) {
            drawable.start()
        }
    }

    private fun initSelectingSortTypeLayout() {
        val sortTypes = listOf(
            R.string.LNG_FILTER_YEAR to Sort.YEAR,
            R.string.LNG_FILTER_RATING to Sort.RATING,
            R.string.LNG_FILTER_PRICE to Sort.PRICE
        )
        val sortTypesNames: List<CharSequence> = sortTypes.map { getString(it.first) }
        Utils.setOfferFilterDialogListener(this, tv_year_sort_title, sortTypesNames) {
            presenter.changeSortType(sortTypes[it].second) }
        sortOrder.setOnClickListener { presenter.changeSortOrder() }
    }

    @SuppressLint("SetTextI18n")
    override fun setSortType(sortType: Sort, sortHigherToLower: Boolean) {
        val sortName = getString(when (sortType) {
            Sort.YEAR -> R.string.LNG_FILTER_YEAR
            Sort.RATING -> R.string.LNG_FILTER_RATING
            Sort.PRICE -> R.string.LNG_FILTER_PRICE
        })
        tv_year_sort_title.text = "${getString(R.string.LNG_FILTER)}: $sortName"
        sortOrder.rotation = if (!sortHigherToLower) SEMI_ROUND else 0f
    }

    override fun showAlertCancelRequest() {
        Utils.showAlertCancelRequest(this) { presenter.cancelRequest(it) }
    }

    override fun showBottomSheetOfferDetails(offer: OfferItemModel) {
        when (offer) {
            is OfferModel -> {
                setVehicleNameAndColor(vehicle = offer.vehicle)
                OfferItemBindDelegate.bindLanguages(
                    Either.Single(languages_container_bs),
                    offer.carrier.languages,
                    layoutParamsRes = LanguageDrawer.LanguageLayoutParamsRes.OFFER_DETAILS
                )
                setCapacity(offer.vehicle.transportType)
                with(vehicle_conveniences) {
                    imgFreeWater.isVisible = offer.refreshments
                    imgFreeWiFi.isVisible = offer.wifi
                    imgCharge.isVisible = offer.charger
                    isVisible = offer.refreshments || offer.wifi || offer.charger
                }
                setWithoutDiscount(offer.price.withoutDiscount)
                setPrice(offer.price.base.preferred ?: offer.price.base.def)
                if (offer.vehicle.photos.isNotEmpty()) {
                    vehiclePhotosView.setPhotos(offer.vehicle.transportType.imageId, offer.vehicle.photos)
                } else {
                    vehiclePhotosView.hidePhotos()
                }
                setRating(offer.carrier)
            }
            is BookNowOfferModel -> {
                setVehicleNameAndColor(nameById = getString(offer.transportType.id.getModelsRes()))
                OfferItemBindDelegate.bindLanguages(
                    Either.Single(languages_container_bs),
                    listOf(LocaleModel.BOOK_NOW_LOCALE_DEFAULT),
                    layoutParamsRes = LanguageDrawer.LanguageLayoutParamsRes.OFFER_DETAILS
                )
                setCapacity(offer.transportType)
                vehicle_conveniences.isVisible = false
                setWithoutDiscount(offer.withoutDiscount)
                setPrice(offer.base.preferred ?: offer.base.def)
                vehiclePhotosView.setPhotos(offer.transportType.id.getImageRes())
                view_offer_rating_bs.isVisible = false
                offer_ratingDivider_bs.isVisible = false
            }
        }

        offer_bottom_bs.btn_book.setOnClickListener {
            presenter.onSelectOfferClicked(offer, false)
            hideSheetOfferDetails()
        }
        bsOfferDetails.state = BottomSheetBehavior.STATE_EXPANDED
    }

    private fun setWithoutDiscount(withoutDiscount: Money?) {
        with(offer_bottom_bs) {
            withoutDiscount?.let { tv_old_price.strikeText = it.preferred ?: it.def }
            tv_old_price.isVisible = withoutDiscount != null
        }
    }

    private fun setPrice(price: String) {
        offer_bottom_bs.tv_current_price.text = price
    }

    private fun setCapacity(transport: TransportTypeModel) {
        with(sheetOfferDetails.view_capacity) {
            transportType_сountPassengers.text = "x".plus(transport.paxMax)
            transportType_сountBaggage.text = "x".plus(transport.luggageMax)
        }
    }

    private fun setVehicleNameAndColor(nameById: String? = null, vehicle: VehicleModel? = null) {
        if (nameById == null && vehicle == null) throw IllegalArgumentException()
        tv_car_model_bs.text = vehicle?.name ?: nameById
        with(ivCarColor) {
            if (vehicle?.color != null && vehicle.photos.isEmpty()) {
                isVisible = true
                setImageDrawable(Utils.getCarColorFormRes(this@OffersActivity, vehicle.color))
            } else {
                isVisible = false
            }
        }
    }

    private fun setRating(carrier: CarrierModel) {
        view_offer_rating_bs.isVisible = true
        offer_ratingDivider_bs.isVisible = true
        carrier.ratings.let { ratings ->
            setRating(ratings.driver, ratingDriver)
            setRating(ratings.communication, ratingPunctuality)
            setRating(ratings.vehicle, ratingVehicle)
        }
        groupTopSelection.isVisible = carrier.approved
    }

    private fun setRating(rate: Double?, ratingLayout: RatingFieldView) {
        with(ratingLayout) {
            val rating = rate ?: 0.0
            ratingBar.rating = rating.toFloat()
            ratingNumber.text = rating.toHalfEvenRoundedFloat().toString().replace(".", ",")
        }
    }

    private fun hideSheetOfferDetails() {
        bsOfferDetails.state = BottomSheetBehavior.STATE_HIDDEN
    }

    override fun onBackPressed() {
        if (bsOfferDetails.state == BottomSheetBehavior.STATE_EXPANDED) {
            hideSheetOfferDetails()
        } else {
            navigateBackWithTransition()
        }
    }

    override fun addNewOffer(offer: OfferModel) {
        val adapter = rvOffers.adapter
        if (adapter is OffersAdapter) adapter.add(offer)
    }

    override fun setError(e: ApiException) {
        if (e.code != ApiException.NETWORK_ERROR) {
            Utils.showError(this, true, e.details)
        }
    }

    override fun hideRefreshSpinner() {
        swipe_rv_offers_container.isRefreshing = false
    }

    @CallSuper
    override fun setNetworkAvailability(context: Context): Boolean {
        val available = super.setNetworkAvailability(context)
        if (available) {
            presenter.checkNewOffers()
        }
        offer_bottom_bs.btn_book.isEnabled = viewNetworkNotAvailable?.isShowing()?.not() ?: true
        if (available) {
            presenter.updateBanners()
        } else {
            cl_fixPrice.isVisible = false
            fl_drivers_count_text.isVisible = false
        }
        return available
    }

    companion object {
        const val PHOTO_CORNER = 7f
        const val MIN_CARRIERS_COUNT = 4
        const val SEMI_ROUND = 180f
    }
}
