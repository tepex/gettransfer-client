package com.kg.gettransfer.fragment


import android.app.Fragment
import android.app.FragmentManager
import android.content.Context
import android.graphics.Typeface
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import android.widget.TextView
import com.google.android.gms.common.GooglePlayServicesNotAvailableException
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.model.*
import com.google.maps.DirectionsApi
import com.google.maps.GeoApiContext
import com.google.maps.android.PolyUtil
import com.google.maps.model.DirectionsResult
import com.kg.gettransfer.R
import com.kg.gettransfer.data.LocationDetailed
import com.kg.gettransfer.modules.http.json.NewTransfer
import com.kg.gettransfer.views.BitmapDescriptorFactory
import com.kg.gettransfer.views.LocationView
import io.reactivex.functions.Consumer
import kotlinx.android.synthetic.main.fragment_createtransfer.*
import kotlinx.android.synthetic.main.fragment_createtransfer.view.*
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject
import java.util.logging.Logger


/**
 * Created by denisvakulenko on 25/01/2018.
 */


class CreateTransferFragment : Fragment(), KoinComponent {
    private val log = Logger.getLogger("CreateTransferFragment")

    private val frChooseLocation: ChooseLocationFragment by lazy { ChooseLocationFragment() }
    private val frTransferDetails: TransferDetailsFragment by lazy { TransferDetailsFragment() }

    val geoApiContext: GeoApiContext by inject()

    private var map: GoogleMap? = null

    private var savedView: View? = null

    private val backStackListener: () -> Unit = { updateFab() }

    private var modeAB = true

    private var routeDistance = 0L
    private var routeDuration = 0L

    private var hireDuration = 0


    override fun onCreateView(
            inflater: LayoutInflater?,
            container: ViewGroup?,
            savedInstanceState: Bundle?)
            : View {
        var v = savedView

        if (v == null) {
            v = inflater?.inflate(R.layout.fragment_createtransfer,
                    container,
                    false)!!

            installEditTextWatcher(v)

            with(v) {
                lvFrom.setOnClickListener { locationViewClick(v.lvFrom) }
                lvTo.setOnClickListener { locationViewClick(v.lvTo) }

                fieldDuration.setOnClickListener { chooseDuration() }

                btnPromo.setOnClickListener { btnPromo() }

                fabConfirmStep.setOnClickListener { showTransferDetails() }

                try {
                    MapsInitializer.initialize(activity)
                } catch (e: GooglePlayServicesNotAvailableException) {
                }

                mapView.onCreate(savedInstanceState)

                tvFromAToB.setOnClickListener {
                    updateTabs(true)
                }

                tvForAWhile.setOnClickListener {
                    updateTabs(false)
                }

                ibSwap.setOnClickListener {
                    val location = lvFrom.location
                    lvFrom.location = lvTo.location
                    lvTo.location = location
                }
            }

            savedView = v
        }

        return v
    }


    private fun chooseDuration() {
        val hours = intArrayOf(
                2, 3, 4, 5, 6, 8, 10, 24, 48, 24 * 3, 24 * 4, 24 * 5, 24 * 10, 24 * 15)

        val labels = arrayOf(
                "2 hours", "3 hours", "4 hours", "5 hours", "6 hours", "8 hours", "10 hours",
                "1 day", "2 days", "3 days", "4 days", "5 days", "10 days", "15 days")

        val builder = AlertDialog.Builder(activity)

        val alert = builder
                .setTitle("Duration")
                .setItems(
                        labels,
                        { d, i ->
                            fieldDuration.text = labels[i]
                            hireDuration = hours[i]
                            updateFab()
                        })
                .setCancelable(true)
                .setNegativeButton(
                        "CANCEL",
                        { d, _ ->
                            d.dismiss()
                        })
                .create()

        alert.show()
    }


    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpMapIfNeeded()
    }


    override fun onResume() {
        super.onResume()

        mapView.onResume()
        setUpMapIfNeeded()

        fragmentManager.addOnBackStackChangedListener(backStackListener)
    }


    override fun onPause() {
        super.onPause()
        fragmentManager.removeOnBackStackChangedListener(backStackListener)
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


    private fun updateTabs(modeAB: Boolean) {
        this.modeAB = modeAB
        if (modeAB) {
            tvFromAToB.typeface = Typeface.DEFAULT_BOLD
            tvForAWhile.typeface = Typeface.DEFAULT
            tvFromAToB.setTextColor(resources.getColor(R.color.colorTextBlack))
            tvForAWhile.setTextColor(resources.getColor(R.color.colorTextGray))
            clDuration.visibility = INVISIBLE
            ibSwap.visibility = VISIBLE
        } else {
            tvFromAToB.typeface = Typeface.DEFAULT
            tvForAWhile.typeface = Typeface.DEFAULT_BOLD
            tvFromAToB.setTextColor(resources.getColor(R.color.colorTextGray))
            tvForAWhile.setTextColor(resources.getColor(R.color.colorTextBlack))
            clDuration.visibility = VISIBLE
            ibSwap.visibility = GONE
        }
        pathChanged.run()
    }


    private fun updateFab() {
        val show = validateFields() && fragmentManager.backStackEntryCount == 0

        fabConfirmStep.visibility = if (show) VISIBLE else GONE

        if (show) btnPromo.visibility = INVISIBLE
    }


    private var markerFrom: Marker? = null
    private var markerTo: Marker? = null
    private var polyline: Polyline? = null

    private val pathChanged = Runnable {
        val llFrom = lvFrom.location?.latLng
        val llTo =
                if (clDuration.visibility == VISIBLE) null
                else lvTo.location?.latLng

        updateMarkers(llFrom, llTo)
        animateMap(llFrom, llTo)

        updateRoute(llFrom, llTo)

        updateFab()
    }

    private fun installEditTextWatcher(v: View) {
        v.lvFrom.onLocationChanged = pathChanged
        v.lvTo.onLocationChanged = pathChanged
    }


    private fun updateRoute(llFrom: LatLng?, llTo: LatLng?) {
        routeDuration = 0L
        routeDistance = 0L

        polyline?.remove()
        polyline = null

        if (llFrom == llTo) return

        if (llFrom != null && llTo != null) {
            val result = DirectionsApi.getDirections(geoApiContext,
                    com.google.maps.model.LatLng(llFrom.latitude, llFrom.longitude).toString(),
                    com.google.maps.model.LatLng(llTo.latitude, llTo.longitude).toString())

            result.setCallback(object : com.google.maps.PendingResult.Callback<DirectionsResult> {
                override fun onResult(result: DirectionsResult) {
                    mapView.post {
                        polyline?.remove()

                        if (result.routes.isEmpty()) return@post

                        val route = result.routes[0]

                        val decodedPath = PolyUtil.decode(route.overviewPolyline.encodedPath)
                        polyline = map?.addPolyline(PolylineOptions().addAll(decodedPath))

                        routeDistance = 0L
                        routeDuration = 0L
                        route.legs.forEach {
                            routeDistance += it.distance.inMeters
                            routeDuration += it.duration.inSeconds
                        }

                        updateFab()
                    }
                }

                override fun onFailure(e: Throwable) {
                    log.info("Routing fail: " + e.message)
                    e.printStackTrace()
                }
            })
        }
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
            map?.animateCamera(CameraUpdateFactory.newLatLngZoom(llFrom, 12f), 600, null)
        } else if (llTo != null) {
            map?.animateCamera(CameraUpdateFactory.newLatLngZoom(llTo, 12f), 600, null)
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
            lvFrom.location?.valid == true &&
                    if (modeAB)
                        lvTo.location?.valid == true &&
                                routeDuration > 0 &&
                                routeDistance > 0
                    else
                        hireDuration > 0


    private fun transferFromFields(): NewTransfer? {
        val lFrom = lvFrom.location?.toLocation() ?: return null

        if (modeAB) {
            val lTo = lvTo.location?.toLocation() ?: return null
            return NewTransfer(
                    lFrom,
                    lTo,
                    (routeDistance / 1000L).toInt(),
                    routeDuration.toInt() / 60)
        } else {
            return NewTransfer(
                    lFrom,
                    hireDuration)
        }
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
}

