package com.kg.gettransfer.presentation.presenter

import android.support.annotation.CallSuper
import com.arellomobile.mvp.InjectViewState

import com.kg.gettransfer.R

import com.kg.gettransfer.domain.interactor.CarrierTripInteractor

import com.kg.gettransfer.presentation.mapper.CarrierTripsListItemsMapper

import com.kg.gettransfer.presentation.model.CarrierTripsRVItemModel

import com.kg.gettransfer.presentation.view.CarrierTripsListFragmentView
import com.kg.gettransfer.presentation.view.Screens

import org.koin.standalone.inject

@InjectViewState
class CarrierTripsListFragmentPresenter : BasePresenter<CarrierTripsListFragmentView>() {
    private val carrierTripsListItemsMapper: CarrierTripsListItemsMapper by inject()

    private var tripsRVItems: List<CarrierTripsRVItemModel>? = null

    @CallSuper
    override fun attachView(view: CarrierTripsListFragmentView) {
        super.attachView(view)
        utils.launchSuspend {
            viewState.blockInterface(true)

            fetchData { carrierTripInteractor.getCarrierTrips() }
                    ?.let {
                        val carrierTripsRVItemsList = carrierTripsListItemsMapper.toRecyclerView(it)
                        tripsRVItems = carrierTripsRVItemsList.itemsList
                        viewState.setTrips(tripsRVItems!!, carrierTripsRVItemsList.startTodayPosition, carrierTripsRVItemsList.endTodayPosition)
                    }

            viewState.blockInterface(false)
        }
    }

    fun onTripSelected(tripId: Long, transferId: Long) = router.navigateTo(Screens.TripDetails(tripId, transferId))
}
