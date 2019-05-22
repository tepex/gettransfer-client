package com.kg.gettransfer.presentation.presenter

import android.os.Bundle
import android.support.annotation.CallSuper

import com.arellomobile.mvp.MvpPresenter

import com.google.firebase.iid.FirebaseInstanceId
import com.kg.gettransfer.domain.ApiException

import com.kg.gettransfer.domain.AsyncUtils
import com.kg.gettransfer.domain.CoroutineContexts
import com.kg.gettransfer.domain.eventListeners.ChatBadgeEventListener
import com.kg.gettransfer.domain.eventListeners.OfferEventListener
import com.kg.gettransfer.domain.interactor.*
import com.kg.gettransfer.domain.model.Account
import com.kg.gettransfer.domain.model.ChatBadgeEvent
import com.kg.gettransfer.domain.model.Offer

import com.kg.gettransfer.presentation.mapper.OfferMapper
import com.kg.gettransfer.presentation.model.OfferModel

import com.kg.gettransfer.presentation.view.BaseView
import com.kg.gettransfer.presentation.view.CarrierTripsMainView.Companion.BG_COORDINATES_REJECTED
import com.kg.gettransfer.presentation.view.Screens

import com.kg.gettransfer.utilities.Analytics
import com.kg.gettransfer.utilities.GTNotificationManager

import kotlinx.coroutines.Job

import com.kg.gettransfer.domain.model.Result
import com.kg.gettransfer.presentation.mapper.BookNowOfferMapper
import com.kg.gettransfer.presentation.mapper.TransferMapper
import kotlinx.coroutines.delay

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
    protected val transferMapper: TransferMapper by inject()
    protected val offerMapper: OfferMapper by inject()
    protected val bookNowOfferMapper: BookNowOfferMapper by inject()
    protected val offerEntityMapper: com.kg.gettransfer.data.mapper.OfferMapper by inject()
    protected val notificationManager: GTNotificationManager by inject()
    protected val offerInteractor: OfferInteractor by inject()
    protected val transferInteractor: TransferInteractor by inject()
    protected val carrierTripInteractor: CarrierTripInteractor by inject()
    protected val chatInteractor: ChatInteractor by inject()
    protected val countEventsInteractor: CountEventsInteractor by inject()

    private val pushTokenInteractor: PushTokenInteractor by inject()
    protected val socketInteractor: SocketInteractor by inject()
    protected val logsInteractor: LogsInteractor by inject()
    protected val sessionInteractor: SessionInteractor by inject()

    //private var sendingMessagesNow = false
    private var openedLoginScreenForUnauthorizedUser = false

    open fun onBackCommandClick() {
        val map = mutableMapOf<String, Any>()
        map[Analytics.PARAM_KEY_NAME] = Analytics.BACK_CLICKED
        analytics.logEvent(Analytics.EVENT_MAIN, createStringBundle(Analytics.PARAM_KEY_NAME, Analytics.BACK_CLICKED), map)
        router.exit()
    }

    protected fun login(nextScreen: String, email: String?) = router.navigateTo(Screens.Login(nextScreen, email))

    override fun onFirstViewAttach() {
        if (sessionInteractor.isInitialized) return
        utils.launchSuspend {
            fetchData { sessionInteractor.coldStart() }
                    ?.let { systemInitialized() }
        }
    }

    override fun attachView(view: BV) {
        super.attachView(view)
        offerInteractor.eventReceiver = this
        chatInteractor.eventChatBadgeReceiver = this
    }

    /*
    return false if error handled, true otherwise
     */

    protected fun checkResultError(error: ApiException): Boolean {
        if (!openedLoginScreenForUnauthorizedUser && (error.isNotLoggedIn() || error.isNoUser() )) {
            openedLoginScreenForUnauthorizedUser = true
            login(Screens.CLOSE_AFTER_LOGIN, sessionInteractor.account.user.profile.email)
            return false
        } else if (openedLoginScreenForUnauthorizedUser) {
            logout()
            return false
        }
        return true
    }

    private fun logout(){
        utils.launchSuspend {
            clearAllCachedData()
            router.navigateTo(Screens.ChangeMode(Screens.PASSENGER_MODE))
        }
    }

    protected suspend fun clearAllCachedData() {
        utils.asyncAwait { pushTokenInteractor.unregisterPushToken() }
        utils.asyncAwait { sessionInteractor.logout() }

        utils.asyncAwait { transferInteractor.clearTransfersCache() }
        utils.asyncAwait { offerInteractor.clearOffersCache() }
        utils.asyncAwait { carrierTripInteractor.clearCarrierTripsCache() }

        countEventsInteractor.clearCountEvents()
    }

    /*fun checkNewMessagesCached() {
        if(!sendingMessagesNow) {
            sendingMessagesNow = true
            utils.launchSuspend {
                val result = utils.asyncAwait { chatInteractor.sendAllNewMessagesSocket() }
                if (result.model > 0) doingSomethingAfterSendingNewMessagesCached()
            }
            sendingMessagesNow = false
        }
    }*/

    //open fun doingSomethingAfterSendingNewMessagesCached() {}

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
                fetchData { transferInteractor.getAllTransfers() }
                        ?.let { if (it.isNotEmpty()) transferID = it.first().id }
            } else transferID = transferId

            router.navigateTo(
                    Screens.SendEmail(
                            emailCarrier,
                            logsInteractor.logsFile,
                            transferID,
                            sessionInteractor.account.user.profile.email))
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
                        fetchResult { pushTokenInteractor.registerPushToken(it) }
                    }
                }
            } else Timber.w("getInstanceId failed", it.exception)
        }
    }

//    fun onOfferJsonReceived(jsonOffer: String, transferId: Long) =
//            JSON.nonstrict.parse(OfferEntity.serializer(), jsonOffer)
//                    .also { it.transferId = transferId }
//                    .let  { offerEntityMapper.fromEntity(it) }
//                    .also { it.vehicle.photos = it.vehicle.photos
//                            .map { photo -> systemInteractor.endpoint.url.plus(photo) } }
//                    .also { onNewOffer(it) }

    open fun onNewOffer(offer: Offer): OfferModel {
        utils.launchSuspend { utils.asyncAwait { offerInteractor.newOffer(offer) } }
        return offerMapper.toView(offer)
                .also { notificationManager.showOfferNotification(it) }
    }

    override fun onNewOfferEvent(offer: Offer) {
        onNewOffer(offer.also {
            it.vehicle.photos = it.vehicle.photos.map { photo -> "${systemInteractor.endpoint.url}$photo" }
            utils.launchSuspend {
                fetchDataOnly { offerInteractor.getOffers(offer.transferId, true) }?.let { offersCached ->
                    if (offersCached.find { offerCached -> offerCached.id == offer.id } != null) {
                        countEventsInteractor.mapCountViewedOffers[offer.transferId]?.let { countViewedOffers ->
                            countEventsInteractor.mapCountNewOffers[offer.transferId]?.let { countNewOffers ->
                                if (countNewOffers == countViewedOffers && countViewedOffers > 0) {
                                    decreaseViewedOffersCounter(offer.transferId)
                                }
                            }
                        }
                    } else {
                        increaseEventsOffersCounter(it.transferId)
                    }
                }
            }
        })
    }

    override fun onChatBadgeChangedEvent(chatBadgeEvent: ChatBadgeEvent) {
        if(!chatBadgeEvent.clearBadge) {
            utils.launchSuspend {
                fetchDataOnly { transferInteractor.getTransfer(chatBadgeEvent.transferId) }?.let {transfer ->
                    increaseEventsMessagesCounter(chatBadgeEvent.transferId, transfer.unreadMessagesCount)
                    notificationManager.showNewMessageNotification(
                            chatBadgeEvent.transferId,
                            transfer.unreadMessagesCount,
                            sessionInteractor.account.groups.indexOf(Account.GROUP_CARRIER_DRIVER) < 0)
                }
            }
        } else {
            with(countEventsInteractor) {
                mapCountNewMessages = mapCountNewMessages.toMutableMap().apply {
                    this[chatBadgeEvent.transferId]?.let {
                        eventsCount -= it
                        remove(chatBadgeEvent.transferId)
                    }
                }
            }
        }
    }

    open fun currencyChanged() {}

    fun saveGeneralSettings(withRestartApp: Boolean = false) {
        if (accountManager.hasAccount) saveAccount(withRestartApp)
        else saveNoAccount(withRestartApp)
    }

    fun saveAccount(withRestartApp: Boolean = false) = utils.launchSuspend {
        viewState.blockInterface(true)
        val result = utils.asyncAwait { accountManager.putAccount() }
        result.error?.let { if (!it.isNotLoggedIn()) viewState.setError(it) }
        if (result.error == null && withRestartApp) restartApp()
        viewState.blockInterface(false)
    }

    fun saveNoAccount(withRestartApp: Boolean) = utils.launchSuspend {
        val result = utils.asyncAwait { sessionInteractor.putNoAccount() }
        if (result.error == null && withRestartApp) restartApp()
    }

    open fun restartApp() {}

    fun onAppStateChanged(isForeGround: Boolean) {
        with(socketInteractor) {
            if (isForeGround) openSocketConnection()
            else if (systemInteractor.lastMode != Screens.CARRIER_MODE ||
                    carrierTripInteractor.bgCoordinatesPermission == BG_COORDINATES_REJECTED){
                closeSocketConnection()
            }
        }
    }

    fun openSocketConnection() { socketInteractor.openSocketConnection() }

    private fun increaseEventsOffersCounter(transferId: Long) =
            with(countEventsInteractor) {
                eventsCount += 1
                mapCountNewOffers = increaseMapCounter(transferId, mapCountNewOffers)
            }

    protected fun increaseViewedOffersCounter(transferId: Long, plusCount: Int) =
            with(countEventsInteractor) {
                eventsCount -= plusCount
                mapCountViewedOffers = increaseMapCounter(transferId, mapCountViewedOffers, null, plusCount)
            }

    private fun decreaseViewedOffersCounter(transferId: Long) =
            with(countEventsInteractor) {
                eventsCount += 1
                mapCountViewedOffers = decreaseMapCounter(transferId, mapCountViewedOffers)
            }

    private fun increaseEventsMessagesCounter(transferId: Long, count: Int) =
            with(countEventsInteractor) {
                eventsCount = eventsCount + count - (mapCountNewMessages[transferId] ?: 0)
                mapCountNewMessages = increaseMapCounter(transferId, mapCountNewMessages, count)
            }

    protected fun decreaseEventsMessagesCounter(transferId: Long) =
            with(countEventsInteractor) {
                mapCountNewMessages = decreaseMapCounter(transferId, mapCountNewMessages)
            }

    private fun increaseMapCounter(transferId: Long, map: Map<Long, Int>, count: Int? = null, plussedCount: Int? = null)
            = map.toMutableMap().apply {
                put(transferId, count ?: map[transferId]?.plus(plussedCount ?: 1) ?: 1)
            }

    private fun decreaseMapCounter(transferId: Long, map: Map<Long, Int>)
            = map.toMutableMap().apply {
                if (this[transferId] != null) {
                    if (map.getValue(transferId) > 1) {
                        put(transferId, map.getValue(transferId) - 1)
                    } else {
                        remove(transferId)
                    }
                }
            }

    fun onDriverModeExit() {
        if (systemInteractor.lastMode == Screens.CARRIER_MODE) socketInteractor.closeSocketConnection()
    }


    /*
    First - work with error: check login error. CheckResultError returns
    false if error is handled and no need to show info to user.
    Next - show error for user with default viewState method. We can do it
    in child presenter if indicate "processError" = true (use "SHOW_ERROR" from Companion)
    Default:
     - DEFAULT_ERROR: if want to call only viewState.setError()
     - CHECK_CACHE: when want to show error also after check data in cache
     */
    protected suspend fun <M>fetchResult(processError: Boolean = DEFAULT_ERROR,
                                         withCacheCheck: Boolean = CHECK_CACHE,
                                         checkLoginError: Boolean = true,
                                         block: suspend () -> Result<M>) =
            utils.asyncAwait { block() }
                .also {
                    it.error
                            ?.let { e -> if (checkLoginError) checkResultError(e) else true }
                            ?.let { handle -> if (!handle) return@also
                                if (withCacheCheck) !it.fromCache else true }
                            ?.let { resultCheck ->
                                if (!processError && resultCheck) viewState.setError(it.error!!)
                                Timber.e(it.error!!) }
                }


    /*
    Method to fetch only data without result if no need to have error object in client class.
    As we unwrap return data safely in client class, so it's possible to use it without care.
     */
    protected suspend fun <D>fetchData(processError: Boolean = DEFAULT_ERROR,
                                       withCacheCheck: Boolean = CHECK_CACHE,
                                       checkLoginError: Boolean = true,
                                       block: suspend () -> Result<D>) =
            with(fetchResult(processError, withCacheCheck, checkLoginError) { block() }) {
                if (error == null || withCacheCheck && fromCache) model else null
            }
    /*
    Optional methods for easy request with only suspend block and without params to handle
    errors.
     */
    protected suspend fun<R>fetchResultOnly(block: suspend () -> Result<R>) =
            fetchResult(WITHOUT_ERROR, NO_CACHE_CHECK, false) { block() }

    protected suspend fun <D>fetchDataOnly(block: suspend () -> Result<D>) =
            fetchData(WITHOUT_ERROR, NO_CACHE_CHECK, false) { block() }

    companion object {
        const val SINGLE_CAPACITY = 1
        const val DOUBLE_CAPACITY = 2

        const val SHOW_ERROR         = true   //when you want to handle error in child presenter
        const val DEFAULT_ERROR      = false
        const val WITHOUT_ERROR      = true   //the same as SHOW_ERROR, but when you will not show error even in child presenter
        const val CHECK_CACHE        = true
        const val NO_CACHE_CHECK     = false
    }

}
