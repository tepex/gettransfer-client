package com.kg.gettransfer.createtransfer


import android.app.Activity
import android.app.FragmentManager
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.design.widget.FloatingActionButton
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.TextView
import com.google.android.gms.common.GooglePlayServicesNotAvailableException
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.MapsInitializer
import com.kg.gettransfer.R
import com.kg.gettransfer.cabinet.TransfersListActivity
import com.kg.gettransfer.fragments.ChooseLocationFragment
import com.kg.gettransfer.fragments.TransferDetailsFragment
import com.kg.gettransfer.login.LoginActivity
import com.kg.gettransfer.modules.Transfers
import com.kg.gettransfer.modules.network.json.NewTransfer
import com.kg.gettransfer.views.LocationView
import io.reactivex.functions.Consumer
import org.koin.android.ext.android.inject
import org.koin.standalone.KoinComponent
import java.util.logging.Logger


/**
 * Created by denisvakulenko on 25/01/2018.
 */


class CreateTransferActivity : AppCompatActivity(), KoinComponent {
    private val log = Logger.getLogger("CreateTransferActivity")

    private val transfers: Transfers by inject()

    private val frChooseLocation: ChooseLocationFragment by lazy { ChooseLocationFragment() }
    private val frTransferDetails: TransferDetailsFragment by lazy { TransferDetailsFragment() }

    val lvFrom by lazy { findViewById<LocationView>(R.id.lvFrom) }
    val lvTo by lazy { findViewById<LocationView>(R.id.lvTo) }

    val fabConfirmStep by lazy { findViewById<FloatingActionButton>(R.id.fabConfirmStep) }

    private val mapView: MapView by lazy { findViewById<MapView>(R.id.map) }
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
            if (fragmentManager.backStackEntryCount == 0) {
                findViewById<ConstraintLayout>(R.id.clNavbar).visibility = VISIBLE
                fabConfirmStep.visibility = VISIBLE
            }
        }
    }


    override fun onResume() {
        super.onResume()
        mapView.onResume()
        setUpMapIfNeeded()
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
//        double lat = Double . parseDouble (0));
//        double lon = Double . parseDouble (0));
//        LatLng coords = new LatLng(lon, lat);
//
//        map.moveCamera(CameraUpdateFactory.newLatLngZoom(coords, 10));
//        map.addMarker(new MarkerOptions ().position(coords));
    }


    public override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView.onSaveInstanceState(outState)
    }


    private fun installEditTextWatcher() {
        val textWatcher: TextWatcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                fabConfirmStep.visibility = if (validateFields()) VISIBLE else GONE
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        }

        lvFrom.addTextChangedListener(textWatcher)
        lvTo.addTextChangedListener(textWatcher)
    }


    private fun validateFields(): Boolean =
            lvFrom.location?.valid == true && lvTo.location?.valid == true


    private fun transferFromFields(): NewTransfer {
        val newTransfer = NewTransfer(lvFrom.location?.toLocation()!!, lvTo.location?.toLocation()!!)

        frTransferDetails.populateNewTransfer(newTransfer)

        return newTransfer
    }


    fun fabClick(v: View) {
        showTransferDetails()

        fabConfirmStep.visibility = GONE
        findViewById<ConstraintLayout>(R.id.clNavbar).visibility = GONE

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
        val intent = Intent(this, TransfersListActivity::class.java)
        startActivityForResult(intent, 2)
    }

    fun showProfile(v: View?) {

    }


    fun locationViewClick(v: View) {
        showLocationChooser(v as LocationView)
    }


    fun showTransferDetails() {
        val ft = fragmentManager.beginTransaction()

//        frTransferDetails.consumer = Consumer {
//            if (it != null) lv.location = it
//            hideLocationChooser()
//        }

        if (frTransferDetails.isAdded) {
            ft.show(frTransferDetails)
        } else {
            ft.add(R.id.flFragmentTransferDetails, frTransferDetails)
        }

        ft.addToBackStack("TransferDetails")

        ft.commit()
    }


    var ftidLocationChooser: Int = -1

    fun showLocationChooser(lv: LocationView) {
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

    fun hideLocationChooser() {
        fragmentManager.popBackStack(ftidLocationChooser, FragmentManager.POP_BACK_STACK_INCLUSIVE)
    }
}
