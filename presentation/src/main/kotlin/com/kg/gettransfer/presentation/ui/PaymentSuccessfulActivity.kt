package com.kg.gettransfer.presentation.ui

import android.os.Bundle
import androidx.annotation.CallSuper

import moxy.presenter.InjectPresenter

import com.google.android.gms.maps.CameraUpdate
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng

import com.kg.gettransfer.R
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import com.kg.gettransfer.presentation.listeners.GoToPlayMarketListener
import com.kg.gettransfer.presentation.model.PolylineModel
import com.kg.gettransfer.presentation.presenter.PaymentSuccessfulPresenter
import com.kg.gettransfer.presentation.view.Screens.showSupportScreen
import com.kg.gettransfer.presentation.view.PaymentSuccessfulView

import kotlinx.android.synthetic.main.content_payment_successful.*
import kotlinx.android.synthetic.main.view_communication_button.view.*

import org.jetbrains.anko.longToast

import pub.devrel.easypermissions.EasyPermissions

class PaymentSuccessfulActivity : BaseGoogleMapActivity(),
    PaymentSuccessfulView,
    EasyPermissions.PermissionCallbacks,
    EasyPermissions.RationaleCallbacks,
    GoToPlayMarketListener {

    @InjectPresenter
    internal lateinit var presenter: PaymentSuccessfulPresenter

    override fun getPresenter(): PaymentSuccessfulPresenter = presenter

    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment_successful)
        setStatusBarColor(R.color.black_50)
        presenter.transferId = intent.getLongExtra(PaymentSuccessfulView.EXTRA_TRANSFER_ID, 0L)
        presenter.offerId = intent.getLongExtra(PaymentSuccessfulView.EXTRA_OFFER_ID, 0L)
        showPaymentDialog(savedInstanceState)
    }

    private fun showPaymentDialog(savedInstanceState: Bundle?) {
        baseMapView = mapViewRoute
        initMapView(savedInstanceState)
        presenter.setMapRoute()

        tvBookingNumber.text = getString(R.string.LNG_BOOKING_NUMBER).plus(" ${presenter.transferId}")
        tvDetails.setOnClickListener { presenter.onDetailsClick() }

        if (presenter.offerId == 0L) {
            tvBookNowSuccess.isVisible = true
            btnSupport.btnName.text = getString(R.string.LNG_OFFERS_SUPPORT)
            tvDownloadVoucher.isInvisible = true
        }
        ivClose.setOnClickListener { finish() }
        btnSupport.setOnClickListener { showSupportScreen(supportFragmentManager, presenter.transferId) }
        tvDownloadVoucher.setOnClickListener { checkPermissionForWrite() }
    }

    private fun checkPermissionForWrite() {
        val perms = arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
        if (EasyPermissions.hasPermissions(this, *perms)) {
            presenter.onDownloadVoucherClick()
        } else {
            EasyPermissions.requestPermissions(
                this,
                getString(R.string.LNG_DOWNLOAD_BOOKING_VOUCHER_QUESTION),
                RC_WRITE_FILE, *perms
            )
        }
    }

    @CallSuper
    override suspend fun customizeGoogleMaps(gm: GoogleMap) {
        super.customizeGoogleMaps(gm)
        gm.uiSettings.isScrollGesturesEnabled = false
        gm.uiSettings.isZoomGesturesEnabled = false
    }

    override fun setRoute(polyline: PolylineModel) = setPolylineWithoutInfo(polyline)

    override fun setRemainTime(days: Int, hours: Int, minutes: Int) {
        val d = "$days${getString(R.string.LNG_D)}"
        val h = "$hours${getString(R.string.LNG_H)}"
        val m = "$minutes${getString(R.string.LNG_M)}"

        val time = "$d:$h:$m"

        tvRemainTime.text = time
    }

    override fun setPinHourlyTransfer(point: LatLng, cameraUpdate: CameraUpdate) {
        processGoogleMap(false) { setPinForHourlyWithoutInfo(point, cameraUpdate) }
    }

    override fun initCallButton() {
        btnCall.isVisible = true
        btnCall.setOnClickListener { presenter.onCallClick() }
    }

    override fun initChatButton() {
        btnChat.isVisible = true
        btnChat.setOnClickListener { presenter.onChatClick() }
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        longToast(getString(R.string.LNG_DOWNLOAD_BOOKING_VOUCHER_ACCESS))
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        presenter.onDownloadVoucherClick()
    }

    override fun onRationaleDenied(requestCode: Int) {
        longToast(getString(R.string.LNG_DOWNLOAD_BOOKING_VOUCHER_ACCESS))
    }

    override fun onRationaleAccepted(requestCode: Int) {}

    @CallSuper
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onClickGoToDriverApp() {
        Utils.goToGooglePlay(this, getString(R.string.driver_app_market_package))
    }

    companion object {
        private const val RC_WRITE_FILE = 111
    }
}
