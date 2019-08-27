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
import com.kg.gettransfer.presentation.presenter.SearchPresenter.Companion.FIELD_FROM
import com.kg.gettransfer.presentation.presenter.SearchPresenter.Companion.FIELD_TO
import com.kg.gettransfer.presentation.ui.*

import com.kg.gettransfer.presentation.ui.utils.FragmentUtils

import com.kg.gettransfer.presentation.view.NewTransferMapView
import com.kg.gettransfer.utilities.NetworkLifeCycleObserver

import kotlinx.android.synthetic.main.fragment_new_transfer_map.*
import kotlinx.android.synthetic.main.search_form_map.*
//import leakcanary.AppWatcher

import pub.devrel.easypermissions.EasyPermissions
import java.lang.UnsupportedOperationException

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

        search_panel.setSearchClickListener {
            presenter.navigateToFindAddress()
        }
        btnNext.setThrottledClickListener { performNextClick() }
        btnBack.setOnClickListener {
            findNavController().navigateUp()
        }
        btnMyLocation.setOnClickListener {
            checkPermission()
            presenter.updateLocation()
        }

        presenter.checkBtnNextState()
    }

    /**
     * Request permission after fragment started
     */
    override fun onCreateAnimator(transit: Int, enter: Boolean, nextAnim: Int): Animator {
        return FragmentUtils.onCreateAnimation(requireContext(), enter) {

            search_panel.visibleFade(true)
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
        presenter.updateLocation()
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

    override fun blockAddressField() {
        search_panel.searchField.text = getString(R.string.LNG_LOADING)
    }

    override fun setAddress(address: String) {
        search_panel.setSearchFieldText(address)
        presenter.checkBtnNextState()
    }

    override fun setBtnNextState(enable: Boolean) {
        btnNext.isEnabled = enable
    }

    override fun initUIForSelectedField(field: String) {
        mMarker.setImageDrawable(ContextCompat.getDrawable(requireContext(),
            when (field) {
                FIELD_FROM -> R.drawable.point_orange
                FIELD_TO   -> R.drawable.ic_map_label_b
                else       -> throw UnsupportedOperationException()
            }
        ))
        search_panel.setFieldMode(field == FIELD_TO)
    }

    override fun onNetworkWarning(available: Boolean) {
        layoutTextNetworkNotAvailable.changeViewVisibility(!available)
    }

    override fun goToSearchAddress(isClickTo: Boolean, isCameFromMap: Boolean) {
        findNavController().navigate(NewTransferMapFragmentDirections.goToSearchAddress(isClickTo, isCameFromMap))
    }

    override fun goToCreateOrder() {
        findNavController().navigate(NewTransferMainFragmentDirections.goToCreateOrder())
    }

    override fun onDestroy() {
        super.onDestroy()
//        AppWatcher.objectWatcher.watch(this)
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
