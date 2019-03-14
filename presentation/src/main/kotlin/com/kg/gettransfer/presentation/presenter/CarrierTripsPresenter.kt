package com.kg.gettransfer.presentation.presenter

import android.support.annotation.CallSuper

import com.arellomobile.mvp.InjectViewState

import com.kg.gettransfer.R

import com.kg.gettransfer.domain.interactor.CarrierTripInteractor

import com.kg.gettransfer.presentation.mapper.ProfileMapper
import com.kg.gettransfer.presentation.mapper.CarrierTripsRVItemsListMapper

import com.kg.gettransfer.presentation.model.CarrierTripsRVItemModel

import com.kg.gettransfer.presentation.view.CarrierTripsView
import com.kg.gettransfer.presentation.view.Screens

import org.koin.standalone.inject

@InjectViewState
class CarrierTripsPresenter : BasePresenter<CarrierTripsView>() {
    private val carrierTripInteractor: CarrierTripInteractor by inject()

    private val carrierTripsRVItemsListMapper: CarrierTripsRVItemsListMapper by inject()
    private val profileMapper: ProfileMapper by inject()

    private var tripsRVItems: List<CarrierTripsRVItemModel>? = null

    override fun onFirstViewAttach() {
        checkLoggedIn()
        systemInteractor.lastMode = Screens.CARRIER_MODE
        utils.launchSuspend {
            viewState.blockInterface(true)
            val result = utils.asyncAwait { carrierTripInteractor.getCarrierTrips() }
            if (result.error != null) viewState.setError(result.error!!)
            else {
                val carrierTripsRVItemsList = carrierTripsRVItemsListMapper.toRecyclerView(result.model)
                tripsRVItems = carrierTripsRVItemsList.itemsList
                viewState.initNavigation(profileMapper.toView(systemInteractor.account.user.profile))
                viewState.setTrips(tripsRVItems!!, carrierTripsRVItemsList.startTodayPosition, carrierTripsRVItemsList.endTodayPosition)
            }
            viewState.blockInterface(false)
        }
    }

    @CallSuper
    override fun attachView(view: CarrierTripsView) {
        super.attachView(view)
        checkLoggedIn()
    }

    fun onTripSelected(tripId: Long, transferId: Long) = router.navigateTo(Screens.TripDetails(tripId, transferId))

    fun checkLoggedIn() {
        if(!systemInteractor.account.user.loggedIn) router.navigateTo(Screens.ChangeMode(Screens.PASSENGER_MODE))
    }

    fun onCarrierTripsClick()   { /*router.navigateTo(Screens.CARRIER_TRIPS)*/ }
    fun onAboutClick()          = router.navigateTo(Screens.About(false))
    fun readMoreClick()         = viewState.showReadMoreDialog()
    fun onSettingsClick()       = router.navigateTo(Screens.Settings)
    fun onPassengerModeClick()  = router.navigateTo(Screens.ChangeMode(Screens.PASSENGER_MODE))
}
