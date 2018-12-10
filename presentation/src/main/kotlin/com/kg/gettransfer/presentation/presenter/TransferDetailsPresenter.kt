package com.kg.gettransfer.presentation.presenter

import android.support.annotation.CallSuper

import com.arellomobile.mvp.InjectViewState
import com.google.android.gms.maps.CameraUpdate

import com.google.android.gms.maps.model.LatLng

import com.kg.gettransfer.domain.interactor.OfferInteractor
import com.kg.gettransfer.domain.interactor.RouteInteractor
import com.kg.gettransfer.domain.interactor.TransferInteractor

import com.kg.gettransfer.presentation.mapper.OfferMapper
import com.kg.gettransfer.presentation.mapper.ProfileMapper
import com.kg.gettransfer.presentation.mapper.TransferMapper

import com.kg.gettransfer.presentation.model.Mappers
import com.kg.gettransfer.presentation.model.PolylineModel
import com.kg.gettransfer.presentation.model.RouteModel
import com.kg.gettransfer.presentation.model.TransferModel

import com.kg.gettransfer.presentation.ui.SystemUtils
import com.kg.gettransfer.presentation.ui.Utils

import com.kg.gettransfer.presentation.view.TransferDetailsView

import com.kg.gettransfer.utilities.Analytics

import org.koin.standalone.inject

import timber.log.Timber

@InjectViewState
class TransferDetailsPresenter : BasePresenter<TransferDetailsView>() {
    private val routeInteractor: RouteInteractor by inject()
    private val transferInteractor: TransferInteractor by inject()
    private val offerInteractor: OfferInteractor by inject()

    private val offerMapper: OfferMapper by inject()
    private val profileMapper: ProfileMapper by inject()
    private val transferMapper: TransferMapper by inject()

    companion object {
        @JvmField val FIELD_EMAIL = "field_email"
        @JvmField val FIELD_PHONE = "field_phone"
        @JvmField val OPERATION_COPY = "operation_copy"
        @JvmField val OPERATION_OPEN = "operation_open"
    }

    private lateinit var transferModel: TransferModel
    private var routeModel: RouteModel? = null
    private var polyline: PolylineModel? = null
    private var track: CameraUpdate? = null

    internal var transferId = 0L

    @CallSuper
    override fun attachView(view: TransferDetailsView) {
        super.attachView(view)
        utils.launchSuspend {
            viewState.blockInterface(true, true)
            val result = utils.asyncAwait { transferInteractor.getTransfer(transferId) }
            if (result.error != null) viewState.setError(result.error!!)
            else {
                transferModel = transferMapper.toView(result.model)
                viewState.setTransfer(transferModel, profileMapper.toView(systemInteractor.account.user.profile))
                if (transferModel.status.checkOffers) {
                    val r = utils.asyncAwait { offerInteractor.getOffers(result.model.id) }
                    if(r.error == null && r.model.size == 1) viewState.setOffer(offerMapper.toView(r.model.first()), transferModel.countChilds)
                }

                if (result.model.to != null) {
                    val r = utils.asyncAwait { routeInteractor.getRouteInfo(result.model.from.point!!, result.model.to!!.point!!, true, false) }
                    if (r.error == null) {
                        routeModel = Mappers.getRouteModel(
                            r.model.distance,
                            r.model.polyLines,
                            result.model.from.name!!,
                            result.model.to!!.name!!,
                            result.model.from.point!!,
                            result.model.to!!.point!!,
                            SystemUtils.formatDateTime(transferModel.dateTime)
                        )
                        routeModel?.let {
                            polyline = Utils.getPolyline(it)
                            track = polyline!!.track
                            viewState.setRoute(polyline!!, it)
                        }
                    }
                } else if (result.model.duration != null) {
                    val point = LatLng(result.model.from.point!!.latitude, result.model.from.point!!.longitude)
                    track = Utils.getCameraUpdateForPin(point)
                    viewState.setPinHourlyTransfer(
                        transferModel.from,
                        SystemUtils.formatDateTime(transferModel.dateTime),
                        point,
                        track!!
                    )
                }
            }
            viewState.blockInterface(false)
        }
    }

    fun onCenterRouteClick() { track?.let { viewState.centerRoute(it) } }

    fun onCancelRequestClicked() {
        viewState.showAlertCancelRequest()
    }

    fun cancelRequest(isCancel: Boolean) {
        if (!isCancel) return
        utils.launchSuspend {
            viewState.blockInterface(true, true)
            val result = utils.asyncAwait { transferInteractor.cancelTransfer(transferId, "") }
            if (result.error != null) {
                Timber.e(result.error!!)
                viewState.setError(result.error!!)
            } else viewState.recreateActivity()
            viewState.blockInterface(false)
        }
    }

    fun makeFieldOperation(field: String, operation: String, text: String) {
        when (operation) {
            OPERATION_COPY -> viewState.copyText(text)
            OPERATION_OPEN -> {
                when (field) {
                    FIELD_PHONE -> callPhone(text)
                    FIELD_EMAIL -> sendEmail(text)
                }
            }
        }
    }

    fun logEventGetOffer(key: String, value: String) {
        val map = mutableMapOf<String, Any?>()
        map[key] = value
        analytics.logEvent(Analytics.EVENT_GET_OFFER, createStringBundle(key, value), map)
    }
}
