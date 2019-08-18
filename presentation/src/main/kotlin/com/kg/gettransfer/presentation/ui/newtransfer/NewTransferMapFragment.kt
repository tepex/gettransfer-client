package com.kg.gettransfer.presentation.ui.newtransfer

import android.Manifest
import android.animation.Animator
import android.os.Bundle

import androidx.annotation.CallSuper
import androidx.core.content.ContextCompat

import android.view.*
import androidx.navigation.fragment.findNavController

import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker

import com.kg.gettransfer.R

import com.kg.gettransfer.extensions.isVisible
import com.kg.gettransfer.extensions.setThrottledClickListener
import com.kg.gettransfer.extensions.visibleFade

import com.kg.gettransfer.presentation.presenter.NewTransferMapPresenter
import com.kg.gettransfer.presentation.ui.*
import com.kg.gettransfer.presentation.ui.dialogs.HourlyDurationDialogFragment

import com.kg.gettransfer.presentation.ui.helpers.HourlyValuesHelper
import com.kg.gettransfer.presentation.ui.utils.FragmentUtils

import com.kg.gettransfer.presentation.view.NewTransferMapView
import com.kg.gettransfer.utilities.NetworkLifeCycleObserver

import kotlinx.android.synthetic.main.fragment_new_transfer_map.*
import kotlinx.android.synthetic.main.search_form_main.*
import kotlinx.android.synthetic.main.view_switcher.*

import pub.devrel.easypermissions.EasyPermissions

@Suppress("TooManyFunctions")
class NewTransferMapFragment : BaseMapFragment(), NewTransferMapView {

    @InjectPresenter
    internal lateinit var presenter: NewTransferMapPresenter

    private var isFirst = true
    private var isPermissionRequested = false
    private var centerMarker: Marker? = null

    private var markerTranslationY = 0f

    @ProvidePresenter
    fun createNewTransferPresenter() = NewTransferMapPresenter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Added network change listener
        lifecycle.addObserver(NetworkLifeCycleObserver(this, this))
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_new_transfer_map, container, false)
    }

    @CallSuper
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        baseMapView = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment

        baseBtnCenter = btnMyLocation
        markerTranslationY = mMarker.translationY
        initMapView(savedInstanceState)

        isFirst = savedInstanceState == null

        switch_mode_.setOnCheckedChangeListener { _, isChecked -> presenter.tripModeSwitched(isChecked) }
        search_panel.setSearchFromClickListener {
            presenter.navigateToFindAddress(
                    searchFrom.text,
                    searchTo.text,
                    isCameFromMap = true
            )
        }
        search_panel.setSearchToClickListener {
            presenter.navigateToFindAddress(
                    searchFrom.text,
                    searchTo.text,
                    isClickTo = true,
                    isCameFromMap = true
            )
        }
        search_panel.setHourlyClickListener {  presenter.showHourlyDurationDialog() }
        search_panel.setIvSelectFieldToClickListener{ presenter.switchUsedField() }
        btnNext.setThrottledClickListener { performNextClick() }
        btnBack.setOnClickListener {
            findNavController().navigateUp()
        }
        btnMyLocation.setOnClickListener {
            checkPermission()
            presenter.updateCurrentLocation()
        }

        enableBtnNext()
    }

    /**
     * Request permission after fragment started
     */
    override fun onCreateAnimator(transit: Int, enter: Boolean, nextAnim: Int): Animator {
        return FragmentUtils.onCreateAnimation(requireContext(), enter) {

            search_panel.visibleFade(true, search_panel)
            presenter.updateView()

            if (!isPermissionRequested) {
                isPermissionRequested = true
                checkPermission()
            }
        }
    }

    @CallSuper
    override suspend fun customizeGoogleMaps(gm: GoogleMap) {
        super.customizeGoogleMaps(gm)
        gm.setOnCameraMoveListener {
            gm.cameraPosition?.let { presenter.onCameraMove(it.target, true) }
        }
        gm.setOnCameraIdleListener { presenter.onCameraIdle(gm.projection.visibleRegion.latLngBounds) }
    }

    override fun initMap() {
    }

    fun performNextClick() {
        presenter.onNextClick { process ->
            btnNext?.isEnabled = false
        }
    }

    override fun showHourlyDurationDialog(durationValue: Int?) {
        HourlyDurationDialogFragment
                .newInstance(durationValue, object : HourlyDurationDialogFragment.OnHourlyDurationListener {
                    override fun onDone(durationValue: Int) {
                        presenter.updateDuration(durationValue)
                    }
                })
                .show(childFragmentManager, HourlyDurationDialogFragment.DIALOG_TAG)
    }

    @CallSuper
    override fun enablePinAnimation() {
        presenter.enablePinAnimation()
    }

    private fun checkPermission() {
        if (!EasyPermissions.hasPermissions(requireContext(), *PERMISSIONS)) {
            EasyPermissions.requestPermissions(
                    this,
                    getString(R.string.LNG_LOCATION_ACCESS),
                    PERMISSION_REQUEST, *PERMISSIONS
            )
        }
    }

    override fun defineAddressRetrieving(block: (withGps: Boolean) -> Unit) {
        block(EasyPermissions.hasPermissions(requireContext(), *PERMISSIONS))
    }

    @CallSuper
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
        presenter.updateCurrentLocation()
    }

    override fun setMapPoint(point: LatLng, withAnimation: Boolean, showBtnMyLocation: Boolean) {
        val zoom = resources.getInteger(R.integer.map_min_zoom).toFloat()
        processGoogleMap(false) { googleMap ->
            if (centerMarker != null) {
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(point, zoom))
                moveCenterMarker(point)
            } else {
                /* Грязный хак!!! */
                if (isFirst || googleMap.cameraPosition.zoom <= MAX_INIT_ZOOM) {
                    val zoom1 = resources.getInteger(R.integer.map_min_zoom).toFloat()
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(point, zoom1))
                    isFirst = false
                } else {
                    if (withAnimation) {
                        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(point, zoom))
                    } else {
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(point, zoom))
                    }
                }
            }
        }
        btnMyLocation.isVisible = showBtnMyLocation
    }

    override fun setMarkerElevation(up: Boolean) {
        val animator = mMarker.animate()
                .withStartAction { presenter.isMarkerAnimating = true }
                .withEndAction {
                    presenter.isMarkerAnimating = false
                    if (!up) markerShadow.setImageDrawable(
                            ContextCompat.getDrawable(requireContext(), R.drawable.default_position_shadow)
                    )
                }
                .setDuration(MAGIC_DURATION)
        if (up) {
            animator.translationYBy(Utils.convertDpToPixels(requireContext(), -MARKER_ELEVATION))
        } else {
            animator.translationY(markerTranslationY)
        }

        animator.start()

        if (up) markerShadow.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.lifted_marker_shadow))
    }

    override fun moveCenterMarker(point: LatLng) {
        centerMarker?.position = point
    }

    override fun blockFromField() {
        search_panel.searchFrom.text = getString(R.string.LNG_LOADING)
    }

    override fun blockToField() {
        search_panel.searchTo.text = getString(R.string.LNG_LOADING)
    }

    override fun setAddressFrom(address: String) {
        search_panel.setSearchFrom(address)
        enableBtnNext()
    }

    override fun setAddressTo(address: String) {
        search_panel.setSearchTo(address)
        enableBtnNext()
    }

    private fun enableBtnNext() {
        btnNext.isEnabled =
            !search_panel.isEmptySearchFrom() && (!search_panel.isEmptySearchTo() || presenter.isHourly())
    }

    override fun selectFieldFrom() {
        mMarker.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.point_orange))
        search_panel.selectSearchFrom()
    }

    override fun setFieldTo() {
        mMarker.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_map_label_b))
        search_panel.selectSearchTo()
    }

    override fun updateTripView(isHourly: Boolean) {
        search_panel.hourlyMode(isHourly)

        enableBtnNext()
    }

    override fun setHourlyDuration(duration: Int?) {
        if (duration != null){
            switch_mode_.isChecked = true
            search_panel.setCurrentHoursText(HourlyValuesHelper.getValue(duration, requireContext()))
        } else {
            switch_mode_.isChecked = false
        }
    }

    override fun onNetworkWarning(available: Boolean) {
        layoutTextNetworkNotAvailable.changeViewVisibility(!available)
        if (available) {
            presenter.fillAddressFieldsCheckIsEmpty()
        }
    }

    override fun goToSearchAddress(addressFrom: String, addressTo: String, isClickTo: Boolean, isCameFromMap: Boolean) {
        findNavController().navigate(NewTransferMapFragmentDirections.goToSearchAddress(addressFrom, addressTo, isClickTo, isCameFromMap))
    }

    override fun goToCreateOrder() {
        findNavController().navigate(NewTransferMainFragmentDirections.goToCreateOrder())
    }

    companion object {
        @JvmField
        val PERMISSIONS =
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
        const val PERMISSION_REQUEST = 2211

        const val MAX_INIT_ZOOM = 2.0f
        const val MAGIC_DURATION = 150L
        const val MARKER_ELEVATION = 5f
    }
}
