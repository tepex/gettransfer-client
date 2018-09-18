package com.kg.gettransfer.presentation.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.annotation.CallSuper
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.Toolbar
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.kg.gettransfer.R
import com.kg.gettransfer.domain.model.CarrierTrip
import com.kg.gettransfer.domain.model.DistanceUnit
import com.kg.gettransfer.presentation.Screens
import com.kg.gettransfer.presentation.adapter.TripsRVAdapter
import com.kg.gettransfer.presentation.presenter.CarrierTripsPresenter
import com.kg.gettransfer.presentation.view.CarrierTripsView
import kotlinx.android.synthetic.main.activity_carrier_trips.*
import kotlinx.android.synthetic.main.toolbar.view.*

class CarrierTripsActivitty: BaseActivity(), CarrierTripsView{
    @InjectPresenter
    internal lateinit var presenter: CarrierTripsPresenter

    @ProvidePresenter
    fun createCarrierTripsPresenter(): CarrierTripsPresenter = CarrierTripsPresenter(coroutineContexts, router, apiInteractor)

    protected override var navigator = object: BaseNavigator(this) {
        protected override fun createActivityIntent(context: Context, screenKey: String, data: Any?): Intent? {
            when(screenKey) {
                Screens.TRIP_DETAILS -> return Intent(context, CarrierTripDetailsActivity::class.java)
            }
            return null
        }
    }

    override fun getPresenter(): CarrierTripsPresenter = presenter

    @CallSuper
    protected override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_carrier_trips)

        setSupportActionBar(toolbar as Toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        (toolbar as Toolbar).toolbar_title.setText(R.string.activity_carrier_trips_title)
        (toolbar as Toolbar).setNavigationOnClickListener { presenter.onBackCommandClick() }

        rvTrips.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
    }

    override fun setTrips(trips: List<CarrierTrip>, distanceUnit: DistanceUnit) {
        rvTrips.adapter = TripsRVAdapter(presenter, trips, distanceUnit)
    }
}