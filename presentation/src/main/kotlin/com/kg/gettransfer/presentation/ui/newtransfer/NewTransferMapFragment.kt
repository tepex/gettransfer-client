package com.kg.gettransfer.presentation.ui.newtransfer

import android.Manifest
import android.animation.Animator
import android.content.Intent
import android.os.Bundle

import androidx.annotation.CallSuper
import androidx.core.content.ContextCompat

import android.view.*
import androidx.navigation.fragment.findNavController

import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker

import com.kg.gettransfer.R

import androidx.core.view.isVisible
import com.kg.gettransfer.extensions.setThrottledClickListener

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
        btnNext.setThrottledClickListener { presenter.onNextClick() }
        btnBack.setOnClickListener { navigateBack() }
        btnMyLocation.setOnClickListener {
            checkPermission()
            presenter.updateLocation()
        }
    }

    /**
     * Request permission after fragment started
     */
    override fun onCreateAnimator(transit: Int, enter: Boolean, nextAnim: Int): Animator {
        return FragmentUtils.onCreateAnimation(requireContext(), enter) {

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

    override fun initMap() {}

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
    }

    override fun initUIForSelectedField(field: String) {
        mMarker.setImageDrawable(ContextCompat.getDrawable(requireContext(),
            when (field) {
                FIELD_FROM -> R.drawable.ic_map_label_a
                FIELD_TO   -> R.drawable.ic_map_label_b
                else       -> R.drawable.ic_map_label_empty
            }
        ))
        search_panel.setFieldMode(field == FIELD_TO)
    }

    override fun onNetworkWarning(available: Boolean) {
        layoutTextNetworkNotAvailable.changeViewVisibility(!available)
        btnNext.isEnabled = available
    }

    override fun goToSearchAddress(isClickTo: Boolean) {
        findNavController().navigate(NewTransferMapFragmentDirections.goToSearchAddress(isClickTo))
    }

    override fun goToCreateOrder() {
        findNavController().navigate(NewTransferMainFragmentDirections.goToCreateOrder())
    }

    override fun navigateBack() {
        findNavController().navigateUp()
    }

    override fun showRestartDialog() {
        Utils.getAlertDialogBuilder(requireActivity()).apply {
            setMessage(R.string.LNG_RESTART_APP)
            setPositiveButton(R.string.LNG_YES) { dialog, _ ->
                presenter.onOkClickResetDialog()
                dialog.dismiss()
            }
            setNegativeButton(R.string.LNG_NO) { dialog, _ -> dialog.dismiss() }
            show()
        }
    }

    override fun restartApp() {
        requireActivity().packageManager.getLaunchIntentForPackage(requireActivity().packageName)?.let { intent ->
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
        }
        requireActivity().finish()
        Runtime.getRuntime().exit(0)
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
