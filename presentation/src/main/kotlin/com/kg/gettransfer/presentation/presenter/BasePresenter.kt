package com.kg.gettransfer.presentation.presenter

import android.os.Bundle
import android.support.annotation.CallSuper

import com.arellomobile.mvp.MvpPresenter

import com.google.firebase.iid.FirebaseInstanceId
import com.kg.gettransfer.data.model.OfferEntity

import com.kg.gettransfer.domain.AsyncUtils
import com.kg.gettransfer.domain.CoroutineContexts
import com.kg.gettransfer.domain.eventListeners.ChatBadgeEventListener
import com.kg.gettransfer.domain.eventListeners.OfferEventListener
import com.kg.gettransfer.domain.interactor.CarrierTripInteractor
import com.kg.gettransfer.domain.interactor.ChatInteractor
import com.kg.gettransfer.domain.interactor.OfferInteractor
import com.kg.gettransfer.domain.interactor.SystemInteractor
import com.kg.gettransfer.domain.interactor.TransferInteractor
import com.kg.gettransfer.domain.model.Account
import com.kg.gettransfer.domain.model.ChatBadgeEvent
import com.kg.gettransfer.domain.model.Offer

import com.kg.gettransfer.presentation.mapper.OfferMapper
import com.kg.gettransfer.presentation.model.OfferModel

import com.kg.gettransfer.presentation.view.BaseView
import com.kg.gettransfer.presentation.view.Screens

import com.kg.gettransfer.utilities.Analytics
import com.kg.gettransfer.utilities.NotificationManager

import kotlinx.coroutines.Job
import kotlinx.serialization.json.JSON

import org.koin.standalone.get
import org.koin.standalone.inject
import org.koin.standalone.KoinComponent

import ru.terrakok.cicerone.Router

import timber.log.Timber

open class BasePresenter<BV: BaseView> : MvpPresenter<BV>(), OfferEventListener, ChatBadgeEventListener, KoinComponent {
    protected val compositeDisposable = Job()
    protected val utils = AsyncUtils(get<CoroutineContexts>(), compositeDisposable)
    protected val router: Router by inject()
    protected val analytics: Analytics by inject()
    protected val systemInteractor: SystemInteractor by inject()
    protected val offerMapper: OfferMapper by inject()
    protected val offerEntityMapper: com.kg.gettransfer.data.mapper.OfferMapper by inject()
    protected val notificationManager: NotificationManager by inject()
    protected val offerInteractor: OfferInteractor by inject()
    protected val transferInteractor: TransferInteractor by inject()
    protected val carrierTripInteractor: CarrierTripInteractor by inject()
    protected val chatInteractor: ChatInteractor by inject()

    open fun onBackCommandClick() {
        val map = mutableMapOf<String, Any>()
        map[Analytics.PARAM_KEY_NAME] = Analytics.BACK_CLICKED
        analytics.logEvent(Analytics.EVENT_MAIN, createStringBundle(Analytics.PARAM_KEY_NAME, Analytics.BACK_CLICKED), map)
        router.exit()
    }

    protected fun login(nextScreen: String, email: String) = router.navigateTo(Screens.Login(nextScreen, email))

    override fun onFirstViewAttach() {
        if (systemInteractor.isInitialized) return
        utils.launchSuspend {
            val result = utils.asyncAwait { systemInteractor.coldStart() }
            if(result.error != null) viewState.setError(result.error!!)
            else systemInitialized()
        }
    }

    override fun attachView(view: BV) {
        super.attachView(view)
        offerInteractor.eventReceiver = this
        chatInteractor.eventChatBadgeReceiver = this
    }

    fun checkNewMessagesCached() {
        utils.launchSuspend { utils.asyncAwait{ chatInteractor.sendAllNewMessages() } }
    }

    @CallSuper
    override fun onDestroy() {
        compositeDisposable.cancel()
        super.onDestroy()
    }

    protected open fun systemInitialized() {}

    protected fun createEmptyBundle() = createStringBundle("", "")

    protected fun createStringBundle(key: String, value: String): Bundle {
        val bundle = Bundle(SINGLE_CAPACITY)
        bundle.putString(key, value)
        return bundle
    }

    protected fun createMultipleBundle(map: Map<String, Any>): Bundle {
        val bundle = Bundle()
        map.forEach { (k, v) -> bundle.putString(k, v.toString()) }
        return bundle
    }

    internal fun sendEmail(emailCarrier: String?, transferId: Long?) {
        utils.launchSuspend {
            var transferID: Long? = null
            if (transferId == null) {

                val transfers = utils.asyncAwait { transferInteractor.getAllTransfers() }
                if (transfers.model.isNotEmpty()) {
                    transferID = transfers.model.first().id
                }
            } else transferID = transferId

            router.navigateTo(
                    Screens.SendEmail(
                            emailCarrier,
                            systemInteractor.logsFile,
                            transferID,
                            systemInteractor.account.user.profile.email))
        }
    }

    internal fun callPhone(phone: String) {
        router.navigateTo(Screens.CallPhone(phone))
    }

    protected fun registerPushToken() {
        FirebaseInstanceId.getInstance().instanceId.addOnCompleteListener {
            if (it.isSuccessful) {
                it.result?.token?.let {
                    Timber.d("[FCM token]: $it")
                    utils.launchSuspend {
                        val result = utils.asyncAwait { systemInteractor.registerPushToken(it) }
                        if (result.error != null) viewState.setError(result.error!!)
                    }
                }
            } else Timber.w("getInstanceId failed", it.exception)
        }
    }

    fun onOfferJsonReceived(jsonOffer: String, transferId: Long) =
            JSON.nonstrict.parse(OfferEntity.serializer(), jsonOffer)
                    .also { it.transferId = transferId }
                    .let  { offerEntityMapper.fromEntity(it) }
                    .also { it.vehicle.photos = it.vehicle.photos
                            .map { photo -> systemInteractor.endpoint.url.plus(photo) } }
                    .also { onNewOffer(it) }

    open fun onNewOffer(offer: Offer): OfferModel {
        utils.launchSuspend { utils.asyncAwait { offerInteractor.newOffer(offer) } }
        return offerMapper.toView(offer)
                .also { notificationManager.showOfferNotification(it) }
    }

    override fun onNewOfferEvent(offer: Offer) {
        onNewOffer(offer.also {
            it.vehicle.photos = it.vehicle.photos.map { photo -> "${systemInteractor.endpoint.url}$photo" }
            increaseEventsCounter(it.transferId)
        })
    }

    override fun onChatBadgeChangedEvent(chatBadgeEvent: ChatBadgeEvent) {
        utils.launchSuspend {
            val result = utils.asyncAwait { transferInteractor.getTransfer(chatBadgeEvent.transferId) }
            if(result.error == null && result.model.unreadMessagesCount != 0) {
                notificationManager.showNewMessageNotification(
                        chatBadgeEvent.transferId,
                        result.model.unreadMessagesCount,
                        systemInteractor.account.groups.indexOf(Account.GROUP_CARRIER_DRIVER) < 0)
            }
        }
    }

    fun saveAccount() = utils.launchSuspend {
        viewState.blockInterface(true)
        val result = utils.asyncAwait { systemInteractor.putAccount() }
        result.error?.let { if (!it.isNotLoggedIn()) viewState.setError(it) }
        viewState.blockInterface(false)
    }

    fun onAppStateChanged(isForeGround: Boolean) {
        with(systemInteractor) {
            if (isForeGround) openSocketConnection()
            else closeSocketConnection()
        }
    }

    private fun increaseEventsCounter(transferId: Long) =
            with(systemInteractor) {
                eventsCount++
                transferIds = transferIds.toMutableList().apply { add(transferId) }
            }

    companion object AnalyticProps {
        const val SINGLE_CAPACITY = 1
        const val DOUBLE_CAPACITY = 2
    }
}
