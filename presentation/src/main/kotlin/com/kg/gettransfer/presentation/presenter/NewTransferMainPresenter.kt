package com.kg.gettransfer.presentation.presenter

import com.arellomobile.mvp.InjectViewState

import com.kg.gettransfer.domain.model.GTAddress

import com.kg.gettransfer.presentation.view.NewTransferMainView

import com.kg.gettransfer.presentation.view.Screens
import com.kg.gettransfer.utilities.Analytics

import org.koin.core.KoinComponent

@InjectViewState
class NewTransferMainPresenter : BaseNewTransferPresenter<NewTransferMainView>(), KoinComponent {

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()

        systemInteractor.lastMode = Screens.PASSENGER_MODE
        systemInteractor.selectedField = FIELD_FROM

        geoInteractor.initGeocoder()
        geoInteractor.initGoogleApiClient()

        // Создать листенер для обновления текущей локации
        // https://developer.android.com/training/location/receive-location-updates
    }

    fun readMoreClick() {
        viewState.showReadMoreDialog()
        analytics.logEvent(Analytics.EVENT_MENU, Analytics.PARAM_KEY_NAME, Analytics.BEST_PRICE_CLICKED)
    }

    override fun updateView(isVisibleView: Boolean) {
        if (!isVisibleView) return

        fillViewFromState()
    }

    override fun changeUsedField(field: String) {
        super.changeUsedField(field)
        when (systemInteractor.selectedField) {
            FIELD_FROM -> viewState.selectFieldFrom()
            FIELD_TO -> viewState.setFieldTo()
        }
    }

    override fun setPointAddress(currentAddress: GTAddress) {
        super.setPointAddress(currentAddress)
        setAddressInSelectedField(currentAddress.cityPoint.name)
    }

    override fun destroyView(view: NewTransferMainView) {
        geoInteractor.disconnectGoogleApiClient()
        super.destroyView(view)
    }

    companion object {
        const val FIELD_FROM = "field_from"
        const val FIELD_TO = "field_to"
    }
}
