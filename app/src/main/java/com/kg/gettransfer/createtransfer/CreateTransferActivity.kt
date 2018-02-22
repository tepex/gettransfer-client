package com.kg.gettransfer.createtransfer


import android.app.Activity
import android.app.FragmentManager
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Gravity
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
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
import com.kg.gettransfer.login.LoginActivity
import com.kg.gettransfer.modules.CurrentAccount
import com.kg.gettransfer.modules.http.json.NewTransfer
import com.kg.gettransfer.transfers.TransfersActivity
import com.kg.gettransfer.views.BitmapDescriptorFactory
import com.kg.gettransfer.views.LocationView
import io.reactivex.functions.Consumer
import kotlinx.android.synthetic.main.activity_createtransfer.*
import org.koin.android.ext.android.inject
import org.koin.standalone.KoinComponent
import java.util.logging.Logger


/**
 * Created by denisvakulenko on 25/01/2018.
 */


class CreateTransferActivity : AppCompatActivity(), KoinComponent {
    private val log = Logger.getLogger("CreateTransferActivity")

    private val currentAccount: CurrentAccount by inject()
//    private val transfers: Transfers by inject()

    private val frChooseLocation: ChooseLocationFragment by lazy { ChooseLocationFragment() }
    private val frTransferDetails: TransferDetailsFragment by lazy { TransferDetailsFragment() }

    private var map: GoogleMap? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_createtransfer)

        installEditTextWatcher()

        try {
            MapsInitializer.initialize(this)
        } catch (e: GooglePlayServicesNotAvailableException) {
        }

        mapView.onCreate(savedInstanceState)
        setUpMapIfNeeded()

        fragmentManager.addOnBackStackChangedListener {
            updateFab()
            clNavbar.visibility =
                    if (fragmentManager.backStackEntryCount == 0) VISIBLE
                    else GONE
        }
    }


    override fun onResume() {
        super.onResume()

        mapView.onResume()
        setUpMapIfNeeded()

        if (currentAccount.isLoggedIn) {
            btnUser.setImageResource(R.drawable.ic_person_gray_24dp)
        } else {
            btnUser.setImageResource(R.drawable.ic_person_outline_gray_24dp)
        }
    }


    private fun setUpMapIfNeeded() {
        if (map == null) {
            val connMgr = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val networkInfo = connMgr.activeNetworkInfo
            if (networkInfo == null || !networkInfo.isConnected) {
                val tvNoInternet = TextView(this)
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
                                .fromVector(this, R.drawable.ic_place_black_24dp)))

        markerTo = map?.addMarker(
                MarkerOptions()
                        .visible(false)
                        .position(LatLng(0.0, 0.0))
                        .anchor(0.5f, 0.95f)
                        .icon(BitmapDescriptorFactory
                                .fromVector(this, R.drawable.ic_place_black_24dp)))
    }


    public override fun onSaveInstanceState(outState: Bundle) {
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

    private fun installEditTextWatcher() {
        lvFrom.onLocationChanged = pathChanged
        lvTo.onLocationChanged = pathChanged
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
            val padding = (64 * applicationContext.resources.displayMetrics.density).toInt()
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


    private fun transferFromFields(): NewTransfer =
            NewTransfer(lvFrom.location?.toLocation()!!, lvTo.location?.toLocation()!!)


    fun fabClick(v: View) {
        showTransferDetails()
//        transfers.createTransfer()
//                .subscribe({ showTransfers(null) })
//
//        transfers.createTransfer(transferFromFields())
    }


    private val LOGIN_REQUEST = 1
    fun login(v: View) {
        val intent = Intent(this, LoginActivity::class.java)
        startActivityForResult(intent, LOGIN_REQUEST)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        // Check which request we're responding to
        if (requestCode == LOGIN_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                showProfile(null)
            }
        }
    }


    fun showTransfers(v: View?) {
        val intent = Intent(this, TransfersActivity::class.java)
        startActivityForResult(intent, 2)
    }


    private fun showProfile(v: View?) {

    }


    fun locationViewClick(v: View) {
        showLocationChooser(v as LocationView)
    }


    private fun showTransferDetails() {
        val ft = fragmentManager.beginTransaction()

        frTransferDetails.transfer = transferFromFields()

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


    // --


    fun btnPromo(v: View) {
        btnPromo.visibility = View.GONE
        lvFrom.location = LocationDetailed("Russia, Domodedovo")
        lvTo.location = LocationDetailed("Moscow, Tverskaya 8")
    }
}
