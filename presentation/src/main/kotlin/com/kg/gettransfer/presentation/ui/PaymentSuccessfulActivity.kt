package com.kg.gettransfer.presentation.ui

import android.app.DownloadManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.support.annotation.NonNull
import android.support.design.widget.BottomSheetBehavior
import android.view.View
import android.webkit.URLUtil

import com.arellomobile.mvp.presenter.InjectPresenter
import com.google.android.gms.maps.CameraUpdate

import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.kg.gettransfer.BuildConfig

import com.kg.gettransfer.R
import com.kg.gettransfer.extensions.isVisible
import com.kg.gettransfer.presentation.model.PolylineModel
import com.kg.gettransfer.presentation.presenter.PaymentSuccessfulPresenter
import com.kg.gettransfer.presentation.view.PaymentSuccessfulView
import kotlinx.android.synthetic.main.activity_payment_successful.*
import kotlinx.android.synthetic.main.dialog_payment_successful.*
import org.jetbrains.anko.longToast
import pub.devrel.easypermissions.EasyPermissions

class PaymentSuccessfulActivity : BaseGoogleMapActivity(), PaymentSuccessfulView,
        EasyPermissions.PermissionCallbacks, EasyPermissions.RationaleCallbacks {

    @InjectPresenter
    internal lateinit var presenter: PaymentSuccessfulPresenter

    private lateinit var bsPayment: BottomSheetBehavior<View>

    override fun getPresenter(): PaymentSuccessfulPresenter = presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment_successful)
        setStatusBarColor(R.color.black_50)
        presenter.offerId = intent.getLongExtra(OFFER_ID, 0L)
        presenter.transferId = intent.getLongExtra(TRANSFER_ID, 0L)
        showPaymentDialog(savedInstanceState)
    }

    private fun showPaymentDialog(savedInstanceState: Bundle?) {
        bsPayment = BottomSheetBehavior.from(sheetSuccessPayment)
        bsPayment.state = BottomSheetBehavior.STATE_EXPANDED
        bsPayment.setBottomSheetCallback(bsCallback)

        sheetSuccessPayment.layoutParams.height =
                getScreenSide(true) - Utils.dpToPxInt(this, 8f)

        _mapView = mapViewRoute
        initMapView(savedInstanceState)
        presenter.setMapRoute()

        tvBookingNumber.text = getString(R.string.LNG_BOOKING_NUMBER).plus(" ${presenter.transferId}")
        tvDetails.setOnClickListener { presenter.onDetailsClick() }

        if (presenter.offerId == 0L) {
            tvBookNowSuccess.isVisible = true
            tvSupport.text = getString(R.string.LNG_OFFERS_SUPPORT)
        }
        ivClose.setOnClickListener { finish() }
        btnSupport.setOnClickListener { presenter.sendEmail(null, presenter.transferId) }
        btnDownloadVoucher.setOnClickListener { checkPermissionForWrite() }
    }

    private fun checkPermissionForWrite() {
        val perms = arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
        if (EasyPermissions.hasPermissions(this, *perms)) {
            downloadVoucher()
        } else EasyPermissions.requestPermissions(
                this,
                getString(R.string.LNG_DOWNLOAD_BOOKING_VOUCHER_QUESTION),
                RC_WRITE_FILE, *perms)
    }

    private fun downloadVoucher() {
        val apiUrl =
                if (BuildConfig.FLAVOR == "prod" || BuildConfig.FLAVOR == "home")
                    getString(R.string.api_url_prod)
                else getString(R.string.api_url_demo)

        val url = apiUrl + API_VOUCHER + presenter.transferId

        val request = DownloadManager.Request(Uri.parse(url)).apply {
            allowScanningByMediaScanner()
            setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            setDestinationInExternalPublicDir(
                    Environment.DIRECTORY_DOWNLOADS,
                    URLUtil.guessFileName(url, null, MIME_TYPE))
        }
        val dm = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        dm.enqueue(request)
        longToast(getString(R.string.LNG_DOWNLOADING))
    }

    private val bsCallback = object : BottomSheetBehavior.BottomSheetCallback() {
        override fun onSlide(p0: View, p1: Float) {

        }

        override fun onStateChanged(@NonNull bottomSheet: View, newState: Int) {
            if (newState == BottomSheetBehavior.STATE_DRAGGING) {
                bsPayment.state = BottomSheetBehavior.STATE_EXPANDED
            }
        }
    }

    override suspend fun customizeGoogleMaps(gm: GoogleMap) {
        super.customizeGoogleMaps(gm)
        gm.uiSettings.isScrollGesturesEnabled = false
        gm.uiSettings.isZoomGesturesEnabled = false
    }

    override fun setRoute(polyline: PolylineModel) = setPolylineWithoutInfo(polyline)

    override fun setRemainTime(days: Int, hours: Int, minutes: Int) {
        val time = "$days${getString(R.string.LNG_D)}:$hours${getString(R.string.LNG_H)}:$minutes${getString(R.string.LNG_M)}"
        tvRemainTime.text = time
    }

    companion object {
        const val TRANSFER_ID = "transferId"
        const val OFFER_ID = "offerId"
        private const val RC_WRITE_FILE = 111
        private const val API_VOUCHER = "/api/transfers/voucher/"
        private const val MIME_TYPE = "application/pdf"
    }

    override fun setPinHourlyTransfer(point: LatLng, cameraUpdate: CameraUpdate) {
        processGoogleMap(false) { setPinForHourlyWithoutInfo(point, cameraUpdate) }
    }

    override fun initCallButton() {
        btnCall.isVisible = true
        tvCall.isVisible = true
        btnCall.setOnClickListener { presenter.onCallClick() }
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        longToast(getString(R.string.LNG_DOWNLOAD_BOOKING_VOUCHER_ACCESS))
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        downloadVoucher()
    }

    override fun onRationaleDenied(requestCode: Int) {
        longToast(getString(R.string.LNG_DOWNLOAD_BOOKING_VOUCHER_ACCESS))
    }

    override fun onRationaleAccepted(requestCode: Int) {}

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }
}
