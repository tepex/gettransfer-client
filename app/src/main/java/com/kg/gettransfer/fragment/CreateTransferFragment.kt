package com.kg.gettransfer.fragment


import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.app.Fragment
import android.app.FragmentManager
import android.content.Context
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.graphics.Typeface
import android.location.Location
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.support.v4.app.ActivityCompat.requestPermissions
import android.support.v4.content.ContextCompat
import android.support.v4.content.ContextCompat.checkSelfPermission
import android.support.v7.app.AlertDialog
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.google.android.gms.common.GooglePlayServicesNotAvailableException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.model.*
import com.google.maps.DirectionsApi
import com.google.maps.DirectionsApiRequest
import com.google.maps.GeoApiContext
import com.google.maps.android.PolyUtil
import com.google.maps.model.DirectionsResult
import com.kg.gettransfer.R
import com.kg.gettransfer.data.LocationDetailed
import com.kg.gettransfer.mainactivity.REQUEST_PERMISSION_ACCESS_FINE_LOCATION
import com.kg.gettransfer.modules.http.json.NewTransfer
import com.kg.gettransfer.realm.getPlString
import com.kg.gettransfer.views.BitmapDescriptorFactory
import com.kg.gettransfer.views.LocationView
import io.reactivex.functions.Consumer
import kotlinx.android.synthetic.main.fragment_createtransfer.*
import kotlinx.android.synthetic.main.fragment_createtransfer.view.*
import kotlinx.android.synthetic.main.view_location.view.*
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

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val geoApiContext: GeoApiContext by inject()

    private var map: GoogleMap? = null

    private var savedView: View? = null

    private val backStackListener: () -> Unit = {
        updateFab()
    }

    private var modeAB = true

    private var routeDistance = 0L
    private var routeDuration = 0L

    private var hireDuration = 0

    private var markerFrom: Marker? = null
    private var markerTo: Marker? = null
    private var polyline: Polyline? = null

    private var directionsApiRequest: DirectionsApiRequest? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity)
    }


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

                btnPromo.setOnClickListener {
                    hsvPromo.visibility = View.GONE
                    lvTo.location = LocationDetailed("Domodedovo Airport, Moscow, Russia")
                }
                btnPromo2.setOnClickListener {
                    hsvPromo.visibility = View.GONE
                    lvTo.location = LocationDetailed("Sheremetevo Airport, Moscow, Russia")
                }

                fabRecenter.setOnClickListener { recenter() }

                fabConfirmStep.setOnClickListener { showTransferDetails() }

                try {
                    MapsInitializer.initialize(activity)
                } catch (e: GooglePlayServicesNotAvailableException) {
                }

                mapView.onCreate(savedInstanceState)

                tvFromAToB.setOnClickListener {
                    updateTabs(true)
                    ivMore.visibility = VISIBLE
                    tvTitle.visibility = VISIBLE
                    tvFromAToB.visibility = INVISIBLE
                    tvForAWhile.visibility = INVISIBLE
                }

                tvForAWhile.setOnClickListener {
                    updateTabs(false)
                    ivMore.visibility = VISIBLE
                    tvTitle.visibility = VISIBLE
                    tvFromAToB.visibility = INVISIBLE
                    tvForAWhile.visibility = INVISIBLE
                }

                ivSwap.setOnClickListener {
                    val location = lvFrom.location
                    lvFrom.location = lvTo.location
                    lvTo.location = location
                }

                btnDurationDec.setOnClickListener {
                    var j = 0
                    if (hireDuration > 2) {
                        hours.forEachIndexed { i, h ->
                            if (h < hireDuration) {
                                j = i
                            } else return@forEachIndexed
                        }
                    }
                    hireDuration = hours[j]
                    fieldDuration.text = labels[j]
                    updateFab()
                }

                btnDurationInc.setOnClickListener {
                    if (hireDuration < 24 * 15) {
                        hours.forEachIndexed { i, h ->
                            if (h > hireDuration) {
                                hireDuration = h
                                fieldDuration.text = labels[i]
                                updateFab()
                                return@setOnClickListener
                            }
                        }
                    }
                }

                lvTo.tvName.setTextColor(ContextCompat.getColor(context, R.color.colorAccent))

                lvFrom.tvName.hint = context.getString(R.string.pick_up_address)
                lvTo.tvName.hint = context.getString(R.string.destination_address)

                ivMore.setOnClickListener {
                    ivMore.visibility = INVISIBLE
                    tvTitle.visibility = INVISIBLE
                    tvFromAToB.visibility = VISIBLE
                    tvForAWhile.visibility = VISIBLE
                }

                fabMyLocation.setOnClickListener {
                    if (checkSelfPermission(activity, ACCESS_FINE_LOCATION) == PERMISSION_GRANTED) {
                        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                            if (location != null)
                                map?.animateCamera(
                                        CameraUpdateFactory.newLatLngZoom(
                                                LatLng(location.latitude, location.longitude),
                                                14f),
                                        500,
                                        null)
                            if (hasMarkers()) fabRecenter.visibility = VISIBLE
                        }
                    } else {
                        requestPermissions(
                                activity,
                                arrayOf(ACCESS_FINE_LOCATION),
                                REQUEST_PERMISSION_ACCESS_FINE_LOCATION)
                    }
                }
            }

            savedView = v
        }

        return v
    }


    private val hours = intArrayOf(
            2, 3, 4, 5, 6, 8, 10, 24, 48, 24 * 3, 24 * 4, 24 * 5, 24 * 10, 24 * 15)

    private val plHours by lazy { activity.getPlString(R.string.pl_hours) }
    private val plDays by lazy { activity.getPlString(R.string.pl_days) }
    private val labels by lazy {
        arrayOf(
                "2 ${plHours.forN(2)}",
                "3 ${plHours.forN(3)}",
                "4 ${plHours.forN(4)}",
                "5 ${plHours.forN(5)}",
                "6 ${plHours.forN(6)}",
                "8 ${plHours.forN(8)}",
                "10 ${plHours.forN(10)}",
                "1 ${plDays.forN(1)}",
                "2 ${plDays.forN(2)}",
                "3 ${plDays.forN(3)}",
                "4 ${plDays.forN(4)}",
                "5 ${plDays.forN(5)}",
                "10 ${plDays.forN(10)}",
                "15 ${plDays.forN(15)}")
    }

    private fun chooseDuration() {
        val builder = AlertDialog.Builder(activity)

        val alert = builder
                .setTitle(activity.getString(R.string.duration))
                .setItems(
                        labels,
                        { _, i ->
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
                if (it != null && map == null) {
                    map = it
                    configureMap(it)
                }
            }
        }
    }


    private fun configureMap(map: GoogleMap) {
        markerFrom = map.addMarker(
                MarkerOptions()
                        .visible(false)
                        .position(LatLng(0.0, 0.0))
                        .anchor(0.5f, 0.95f)
                        .icon(BitmapDescriptorFactory
                                .fromVector(activity, R.drawable.ic_place_black_24dp)))

        markerTo = map.addMarker(
                MarkerOptions()
                        .visible(false)
                        .position(LatLng(0.0, 0.0))
                        .anchor(0.5f, 0.95f)
                        .icon(BitmapDescriptorFactory
                                .fromVector(activity, R.drawable.ic_place_blue_24dp)))

        map.uiSettings.isRotateGesturesEnabled = false
        map.uiSettings.isCompassEnabled = false
        map.uiSettings.isMapToolbarEnabled = false
        map.uiSettings.isTiltGesturesEnabled = false
        map.uiSettings.isMyLocationButtonEnabled = false

        if (checkSelfPermission(activity, ACCESS_FINE_LOCATION) == PERMISSION_GRANTED) {
            map.isMyLocationEnabled = true
        }

        mapView.onUserChangedCamera = Runnable {
            fabRecenter.visibility = VISIBLE
        }

        view.postDelayed(
                { setFromMyCurrentLocation(true) },
                1000)
    }


    fun setFromMyCurrentLocation(askPermissionIfNotPermitted: Boolean) {
        val activity = activity ?: return
        if (checkSelfPermission(activity, ACCESS_FINE_LOCATION) == PERMISSION_GRANTED) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null)
                    lvFrom.location = LocationDetailed(
                            LatLng(
                                    location.latitude,
                                    location.longitude),
                            true)
            }
        } else if (askPermissionIfNotPermitted) {
            requestPermissions(
                    activity,
                    arrayOf(ACCESS_FINE_LOCATION),
                    REQUEST_PERMISSION_ACCESS_FINE_LOCATION)
        }
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

            vSwapBG.visibility = VISIBLE
            flSwapBG.visibility = VISIBLE
            ivSwap.visibility = VISIBLE
        } else {
            tvFromAToB.typeface = Typeface.DEFAULT
            tvForAWhile.typeface = Typeface.DEFAULT_BOLD
            tvFromAToB.setTextColor(resources.getColor(R.color.colorTextGray))
            tvForAWhile.setTextColor(resources.getColor(R.color.colorTextBlack))
            clDuration.visibility = VISIBLE

            vSwapBG.visibility = GONE
            flSwapBG.visibility = GONE
            ivSwap.visibility = GONE
        }
        pathChanged.run()
    }


    private fun updateFab() {
        val show = validateFields() && fragmentManager.backStackEntryCount == 0

        fabConfirmStep.visibility = if (show) VISIBLE else GONE

        if (show) btnPromo.visibility = INVISIBLE
    }


    private fun hasMarkers() = lvFrom.location.latLng != null ||
            clDuration.visibility == VISIBLE &&
            lvTo.location.latLng != null


    private val pathChanged = Runnable {
        val llFrom = lvFrom.location.latLng
        val llTo =
                if (clDuration.visibility == VISIBLE) null
                else lvTo.location.latLng

        updateMarkers(llFrom, llTo)
        animateMap(llFrom, llTo)

        updateRoute(llFrom, llTo)

        updateFab()
    }


    private fun recenter() {
        val llFrom = lvFrom.location.latLng
        val llTo =
                if (clDuration.visibility == VISIBLE) null
                else lvTo.location.latLng

        animateMap(llFrom, llTo)
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
            directionsApiRequest?.cancel()

            directionsApiRequest = DirectionsApi.getDirections(
                    geoApiContext,
                    com.google.maps.model.LatLng(llFrom.latitude, llFrom.longitude).toString(),
                    com.google.maps.model.LatLng(llTo.latitude, llTo.longitude).toString())

            directionsApiRequest?.setCallback(object : com.google.maps.PendingResult.Callback<DirectionsResult> {
                override fun onResult(result: DirectionsResult) {
                    mapView.post {
                        polyline?.remove()

                        if (result.routes.isEmpty()) {
                            Toast.makeText(activity, getString(R.string.routing_failed), Toast.LENGTH_SHORT).show()
                            return@post
                        }

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
                    try {
                        log.info("Routing failed: " + e.message)
                        e.printStackTrace()
                        Toast.makeText(activity, getString(R.string.routing_failed), Toast.LENGTH_SHORT).show()
                    } catch (e: Exception) {

                    }
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
        val map = map ?: return

        if (llFrom != null && llTo != null) {
            try {
                val padding = (96 * activity.resources.displayMetrics.density).toInt()
                map.animateCamera(
                        CameraUpdateFactory.newLatLngBounds(
                                getLatLngBounds(llFrom, llTo),
                                padding),
                        600,
                        null)
            } catch (e: Exception) {
                map.animateCamera(
                        CameraUpdateFactory.newLatLngBounds(
                                getLatLngBounds(llFrom, llTo),
                                0),
                        600,
                        null)
            }
        } else if (llFrom != null) {
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(llFrom, 14f), 500, null)
        } else if (llTo != null) {
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(llTo, 14f), 500, null)
        }

        fabRecenter.visibility = INVISIBLE

        mapView.userChangedCamera = false
    }


    private fun getLatLngBounds(a: LatLng, b: LatLng): LatLngBounds {
        return LatLngBounds(
                LatLng(Math.min(a.latitude, b.latitude), Math.min(a.longitude, b.longitude)),
                LatLng(Math.max(a.latitude, b.latitude), Math.max(a.longitude, b.longitude)))
    }


    private fun validateFields(): Boolean =
            lvFrom.location.isValid() &&
                    if (modeAB)
                        lvTo.location.isValid() &&
                                routeDuration > 0 &&
                                routeDistance > 0
                    else
                        hireDuration > 0


    private fun transferFromFields(): NewTransfer? {
        val lFrom = lvFrom.location.toLocation() ?: return null

        if (modeAB) {
            val lTo = lvTo.location.toLocation() ?: return null
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

        frTransferDetails.setTransfer(transfer)

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
        if (frChooseLocation.isAdded) return

        val ft = fragmentManager.beginTransaction()

        frChooseLocation.location = lv.location
        frChooseLocation.setHint(lv.tvName.hint)
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
}

