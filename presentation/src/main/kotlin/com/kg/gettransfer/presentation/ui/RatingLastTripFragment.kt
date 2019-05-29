package com.kg.gettransfer.presentation.ui

import android.os.Bundle
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.kg.gettransfer.R
import com.kg.gettransfer.domain.ApiException
import com.kg.gettransfer.domain.DatabaseException
import com.kg.gettransfer.presentation.model.RouteModel
import com.kg.gettransfer.presentation.model.TransferModel
import com.kg.gettransfer.presentation.presenter.RatingLastTripPresenter
import com.kg.gettransfer.presentation.ui.dialogs.BaseBottomSheetDialogFragment
import com.kg.gettransfer.presentation.ui.dialogs.RatingDetailDialogFragment
import com.kg.gettransfer.presentation.ui.dialogs.StoreDialogFragment
import com.kg.gettransfer.presentation.ui.helpers.MapHelper
import com.kg.gettransfer.presentation.view.RatingLastTripView
import kotlinx.android.synthetic.main.view_last_trip_rate.*

class RatingLastTripFragment: BaseBottomSheetDialogFragment(), RatingLastTripView, OnMapReadyCallback {

    private lateinit var googleMap: GoogleMap

    private var showDialog = false

    override val layout: Int = R.layout.view_last_trip_rate

    @InjectPresenter
    lateinit var presenter: RatingLastTripPresenter

    @ProvidePresenter
    fun providePresenter() = RatingLastTripPresenter()

    companion object {
        const val RATING_LAST_TRIP_TAG = "rating_last_trip_tag"

        fun newInstance() = RatingLastTripFragment()
    }

    override fun initUx(savedInstanceState: Bundle?) {
        super.initUx(savedInstanceState)
        tv_transfer_details.setOnClickListener { presenter.onTransferDetailsClick() }
        ivClose.setOnClickListener { presenter.onReviewCanceled() }
        rate_bar_last_trip.setOnRatingChangeListener { _, fl ->
            cancelReview()
            presenter.onRateClicked(fl)
        }
    }

    override fun initUi(savedInstanceState: Bundle?) {
        super.initUi(savedInstanceState)
        (fragmentManager?.findFragmentById(R.id.rate_map) as SupportMapFragment).getMapAsync(this)
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        customizeGoogleMaps(googleMap)
    }

    private fun customizeGoogleMaps(gm: GoogleMap) {
        gm.uiSettings.isRotateGesturesEnabled = false
        gm.uiSettings.isTiltGesturesEnabled = false
        gm.uiSettings.isMyLocationButtonEnabled = false
        gm.uiSettings.isScrollGesturesEnabled = false
        gm.uiSettings.isZoomGesturesEnabled = false
    }

    override fun hideDialog() {
        dialog.hide()
    }

    override fun onResume() {
        super.onResume()
        if (!showDialog) dialog.hide()
    }

    override fun setupReviewForLastTrip(transfer: TransferModel, startPoint: LatLng, vehicle: String, color: String, routeModel: RouteModel?) {
        dialog.show()
        showDialog = true
        tv_transfer_number_rate.apply { text = text.toString().plus(" #${transfer.id}") }
        tv_transfer_date_rate.text = SystemUtils.formatDateTime(transfer.dateTime)
        tv_vehicle_model_rate.text = vehicle
        context?.let { carColor_rate.setImageDrawable(Utils.getVehicleColorFormRes(it, color)) }
        drawMapForReview(routeModel, transfer.from, startPoint)
    }

    private fun drawMapForReview(routeModel: RouteModel?, from: String, startPoint: LatLng) {
        if (routeModel != null) {
            val polyline = Utils.getPolyline(routeModel)
            if (MapHelper.isEmptyPolyline(polyline, routeModel)) return
            else context?.let { MapHelper.setPolyline(it, layoutInflater, googleMap, polyline, routeModel) }
        } else {
            setPinForHourlyTransfer(from, "", startPoint, Utils.getCameraUpdateForPin(startPoint))
        }
    }

    private fun setPinForHourlyTransfer(placeName: String, info: String, point: LatLng, cameraUpdate: CameraUpdate, driver: Boolean = false) {
        val markerRes = R.drawable.ic_map_label_a
        val bmPinA = MapHelper.getPinBitmap(layoutInflater, placeName, info, markerRes)
        val startMakerOptions = MarkerOptions()
                .position(point)
                .icon(BitmapDescriptorFactory.fromBitmap(bmPinA))
        googleMap.addMarker(startMakerOptions)
        googleMap.moveCamera(cameraUpdate)
    }

    override fun cancelReview() {
        dismiss()
    }

    override fun askRateInPlayMarket() =
        StoreDialogFragment.newInstance().show(fragmentManager, StoreDialogFragment.STORE_DIALOG_TAG)

    override fun thanksForRate() =
            ThanksForRateFragment
                    .newInstance()
                    .show(fragmentManager, ThanksForRateFragment.TAG)

    override fun showDetailedReview(rate: Float, offerId: Long) {
        if (fragmentManager?.fragments?.firstOrNull { it.tag == RatingDetailDialogFragment.RATE_DIALOG_TAG} == null) {
            RatingDetailDialogFragment
                    .newInstance(rate, rate, rate, offerId)
                    .show(fragmentManager, RatingDetailDialogFragment.RATE_DIALOG_TAG)
        }
    }

    override fun blockInterface(block: Boolean, useSpinner: Boolean) {}

    override fun setError(finish: Boolean, errId: Int, vararg args: String?) {}

    override fun setError(e: ApiException) {}

    override fun setError(e: DatabaseException) {}
}