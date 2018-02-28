package com.kg.gettransfer.createtransfer


import android.app.Fragment
import android.app.FragmentManager
import android.content.Context
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.TextView
import com.google.android.gms.common.GooglePlayServicesNotAvailableException
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.kg.gettransfer.R
import com.kg.gettransfer.data.LocationDetailed
import com.kg.gettransfer.fragments.ChooseLocationFragment
import com.kg.gettransfer.fragments.TransferDetailsFragment
import com.kg.gettransfer.modules.http.json.NewTransfer
import com.kg.gettransfer.views.BitmapDescriptorFactory
import com.kg.gettransfer.views.LocationView
import io.reactivex.functions.Consumer
import kotlinx.android.synthetic.main.fragment_createtransfer.*
import kotlinx.android.synthetic.main.fragment_createtransfer.view.*
import org.koin.standalone.KoinComponent
import java.util.logging.Logger


/**
 * Created by denisvakulenko on 25/01/2018.
 */


class CreateTransferFragment : Fragment(), KoinComponent {
    private val log = Logger.getLogger("CreateTransferFragment")

    private val frChooseLocation: ChooseLocationFragment by lazy { ChooseLocationFragment() }
    private val frTransferDetails: TransferDetailsFragment by lazy { TransferDetailsFragment() }

    private var map: GoogleMap? = null

    private var savedView: View? = null

    private val backStackListener: () -> Unit = { updateFab() }


    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        if (savedView == null) {
            val view = inflater?.inflate(R.layout.fragment_createtransfer,
                    container,
                    false)!!

            installEditTextWatcher(view)

            with(view) {
                lvFrom.setOnClickListener { locationViewClick(view.lvFrom) }
                lvTo.setOnClickListener { locationViewClick(view.lvTo) }

                btnPromo.setOnClickListener { btnPromo() }

                fabConfirmStep.setOnClickListener { showTransferDetails() }

                try {
                    MapsInitializer.initialize(activity)
                } catch (e: GooglePlayServicesNotAvailableException) {
                }

                mapView.onCreate(savedInstanceState)
            }

            savedView = view
        }
        return savedView!!
    }


    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpMapIfNeeded()

        fragmentManager.addOnBackStackChangedListener(backStackListener)
    }


    override fun onResume() {
        super.onResume()

        mapView.onResume()
        setUpMapIfNeeded()
    }


    private fun setUpMapIfNeeded() {
        if (map == null) {
            val connMgr = activity.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val networkInfo = connMgr.activeNetworkInfo
            if (networkInfo == null || !networkInfo.isConnected) {
                val tvNoInternet = TextView(activity)
                tvNoInternet.gravity = Gravity.CENTER_HORIZONTAL
                tvNoInternet.text = getString(R.string.no_net_info)
                mapView.addView(tvNoInternet)
            }

            mapView.getMapAsync {
                map = it
                if (map != null) {
                    setUpMap()
                }
            }
        }
    }


    private fun setUpMap() {
        markerFrom = map?.addMarker(
                MarkerOptions()
                        .visible(false)
                        .position(LatLng(0.0, 0.0))
                        .anchor(0.5f, 0.95f)
                        .icon(BitmapDescriptorFactory
                                .fromVector(activity, R.drawable.ic_place_black_24dp)))

        markerTo = map?.addMarker(
                MarkerOptions()
                        .visible(false)
                        .position(LatLng(0.0, 0.0))
                        .anchor(0.5f, 0.95f)
                        .icon(BitmapDescriptorFactory
                                .fromVector(activity, R.drawable.ic_place_black_24dp)))
    }


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView.onSaveInstanceState(outState)
    }


    private fun updateFab() {
        fabConfirmStep.visibility =
                if (validateFields() && fragmentManager.backStackEntryCount == 0) VISIBLE
                else GONE
    }


    private var markerFrom: Marker? = null
    private var markerTo: Marker? = null

    private val pathChanged = Runnable {
        updateFab()

        val llFrom = lvFrom.location?.latLng
        val llTo = lvTo.location?.latLng
        updateMarkers(llFrom, llTo)
        animateMap(llFrom, llTo)
    }

    private fun installEditTextWatcher(v: View) {
        v.lvFrom.onLocationChanged = pathChanged
        v.lvTo.onLocationChanged = pathChanged
    }


    private fun updateMarkers(llFrom: LatLng?, llTo: LatLng?) {
        if (llFrom != null) {
            markerFrom?.position = llFrom
            markerFrom?.isVisible = true
        } else {
            markerFrom?.isVisible = false
        }

        if (llTo != null) {
            markerTo?.position = llTo
            markerTo?.isVisible = true
        } else {
            markerTo?.isVisible = false
        }
    }


    private fun animateMap(llFrom: LatLng?, llTo: LatLng?) {
        if (llFrom != null && llTo != null) {
            val padding = (64 * activity.resources.displayMetrics.density).toInt()
            map?.animateCamera(
                    CameraUpdateFactory.newLatLngBounds(
                            getLatLngBounds(llFrom, llTo),
                            padding),
                    600,
                    null)
        } else if (llFrom != null) {
            map?.animateCamera(CameraUpdateFactory.newLatLng(llFrom), 600, null)
        } else if (llTo != null) {
            map?.animateCamera(CameraUpdateFactory.newLatLng(llTo), 600, null)
        }
    }


    private fun getLatLngBounds(a: LatLng, b: LatLng): LatLngBounds {
        return LatLngBounds(
                LatLng(
                        Math.min(a.latitude, b.latitude),
                        Math.min(a.longitude, b.longitude)),
                LatLng(
                        Math.max(a.latitude, b.latitude),
                        Math.max(a.longitude, b.longitude)))
    }


    private fun validateFields(): Boolean =
            lvFrom.location?.valid == true && lvTo.location?.valid == true


    private fun transferFromFields(): NewTransfer? {
        val lFrom = lvFrom.location?.toLocation() ?: return null
        val lTo = lvTo.location?.toLocation() ?: return null

        return NewTransfer(lFrom, lTo)
    }


    private fun locationViewClick(v: View) {
        showLocationChooser(v as LocationView)
    }


    private fun showTransferDetails() {
        val transfer = transferFromFields() ?: return

        val ft = fragmentManager.beginTransaction()

        frTransferDetails.transfer = transfer

        if (Build.VERSION.SDK_INT >= 21) {
            val slide = android.transition.Slide()
            slide.duration = 150

            frTransferDetails.enterTransition = slide
        }

        if (frTransferDetails.isAdded) {
            ft.show(frTransferDetails)
        } else {
            ft.add(R.id.flFragmentTransferDetails, frTransferDetails)
        }

        ft.addToBackStack("TransferDetails")

        ft.commit()
    }


    private var ftidLocationChooser: Int = -1

    private fun showLocationChooser(lv: LocationView) {
        val ft = fragmentManager.beginTransaction()

        frChooseLocation.location = lv.location
        frChooseLocation.setHint(lv.hint)
        frChooseLocation.consumer = Consumer {
            if (it != null) lv.location = it
            hideLocationChooser()
        }

        if (frChooseLocation.isAdded) {
            ft.show(frChooseLocation)
        } else {
            ft.add(R.id.flFragment, frChooseLocation)
        }

        ft.addToBackStack("LocationChooser")

        ftidLocationChooser = ft.commit()
    }


    private fun hideLocationChooser() {
        fragmentManager.popBackStack(ftidLocationChooser, FragmentManager.POP_BACK_STACK_INCLUSIVE)
    }


    private fun btnPromo() {
        btnPromo.visibility = View.GONE
        lvFrom.location = LocationDetailed("Russia, Domodedovo")
        lvTo.location = LocationDetailed("Moscow, Tverskaya 8")
    }


    override fun onDestroyView() {
        super.onDestroyView()
        fragmentManager.removeOnBackStackChangedListener(backStackListener)
    }
}

