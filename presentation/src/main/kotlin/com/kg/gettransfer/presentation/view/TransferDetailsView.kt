package com.kg.gettransfer.presentation.view

import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.google.android.gms.maps.CameraUpdate

import com.google.android.gms.maps.model.LatLng

import com.kg.gettransfer.presentation.model.OfferModel
import com.kg.gettransfer.presentation.model.ProfileModel
import com.kg.gettransfer.presentation.model.TransferModel
import java.io.File

@StateStrategyType(OneExecutionStateStrategy::class)
interface TransferDetailsView: BaseView, RouteView {
    companion object {
        val EXTRA_TRANSFER_ID = "${TransferDetailsView::class.java.name}.transferId"
    }

    fun setTransfer(transferModel: TransferModel, userProfile: ProfileModel)
    fun setOffer(offerModel: OfferModel, childSeats: Int)
    //fun setPinHourlyTransfer(placeName: String, info: String, point: LatLng)
    //fun setPinHourlyTransfer(placeName: String, info: String, point: LatLng, cameraUpdate: CameraUpdate)
    fun showAlertCancelRequest()
    fun sendEmail(emailCarrier: String?, logsFile: File?)
    fun callPhone(phoneCarrier: String)
    fun copyText(text: String)
    fun recreateActivity()
    fun centerRoute(cameraUpdate: CameraUpdate)
}
