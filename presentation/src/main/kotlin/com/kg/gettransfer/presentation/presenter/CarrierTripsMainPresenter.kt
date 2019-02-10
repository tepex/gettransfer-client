package com.kg.gettransfer.presentation.presenter

import android.support.annotation.CallSuper
import com.arellomobile.mvp.InjectViewState
import com.kg.gettransfer.presentation.mapper.ProfileMapper
import com.kg.gettransfer.presentation.view.CarrierTripsMainView
import com.kg.gettransfer.presentation.view.Screens
import org.koin.standalone.inject

@InjectViewState
class CarrierTripsMainPresenter : CarrierPresenter<CarrierTripsMainView>() {
    private val profileMapper: ProfileMapper by inject()

    override fun onFirstViewAttach() {
        checkLoggedIn()
        systemInteractor.lastMode = Screens.CARRIER_MODE
        viewState.initNavigation(profileMapper.toView(systemInteractor.account.user.profile))
        changeTypeView(systemInteractor.lastCarrierTripsTypeView)
    }

    @CallSuper
    override fun attachView(view: CarrierTripsMainView) {
        super.attachView(view)
        checkLoggedIn()
    }

    fun changeTypeView(type: String){
        systemInteractor.lastCarrierTripsTypeView = type
        viewState.changeTypeView(type)
    }

    private fun checkLoggedIn() {
        if(!systemInteractor.account.user.loggedIn) router.navigateTo(Screens.ChangeMode(Screens.PASSENGER_MODE))
    }

    fun onCarrierTripsClick()   { /*router.navigateTo(Screens.CARRIER_TRIPS)*/ }
    fun onAboutClick()          = router.navigateTo(Screens.About(false))
    fun readMoreClick()         = viewState.showReadMoreDialog()
    fun onSettingsClick()       = router.navigateTo(Screens.Settings)
    fun onPassengerModeClick()  = router.navigateTo(Screens.ChangeMode(Screens.PASSENGER_MODE))
}