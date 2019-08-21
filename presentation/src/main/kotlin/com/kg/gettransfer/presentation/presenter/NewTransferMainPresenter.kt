package com.kg.gettransfer.presentation.presenter

import com.arellomobile.mvp.InjectViewState

import com.kg.gettransfer.domain.model.GTAddress

import com.kg.gettransfer.presentation.presenter.SearchPresenter.Companion.FIELD_FROM
import com.kg.gettransfer.presentation.view.NewTransferMainView
import com.kg.gettransfer.presentation.view.Screens
import com.kg.gettransfer.utilities.Analytics

import com.kg.gettransfer.sys.domain.SetLastModeInteractor
import com.kg.gettransfer.sys.domain.SetSelectedFieldInteractor

import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.inject

@InjectViewState
class NewTransferMainPresenter : BaseNewTransferPresenter<NewTransferMainView>() {

    private val setLastMode: SetLastModeInteractor by inject()
    private val setSelectedField: SetSelectedFieldInteractor by inject()

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()

        geoInteractor.initGeocoder()
        geoInteractor.initGoogleApiClient()

        worker.main.launch {
            withContext(worker.bg) {
                setLastMode(Screens.PASSENGER_MODE)
                setSelectedField(FIELD_FROM)
            }
        }

        // Создать листенер для обновления текущей локации
        // https://developer.android.com/training/location/receive-location-updates
    }

    fun readMoreClick() {
        viewState.showReadMoreDialog()
        analytics.logEvent(Analytics.EVENT_MENU, Analytics.PARAM_KEY_NAME, Analytics.BEST_PRICE_CLICKED)
    }

    override fun updateView() {
        fillViewFromState(FIELD_FROM)
    }

    override fun setPointAddress(currentAddress: GTAddress) {
        super.setPointAddress(currentAddress)
        setAddressInSelectedField(currentAddress.cityPoint.name)
    }

    override fun destroyView(view: NewTransferMainView) {
        geoInteractor.disconnectGoogleApiClient()
        super.destroyView(view)
    }
}
