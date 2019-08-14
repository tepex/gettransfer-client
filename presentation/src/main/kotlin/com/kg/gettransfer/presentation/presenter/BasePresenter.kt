package com.kg.gettransfer.presentation.presenter

import com.arellomobile.mvp.MvpPresenter

import com.google.firebase.iid.FirebaseInstanceId

import com.kg.gettransfer.core.presentation.WorkerManager

import com.kg.gettransfer.domain.ApiException
import com.kg.gettransfer.domain.AsyncUtils
import com.kg.gettransfer.domain.CoroutineContexts
import com.kg.gettransfer.domain.eventListeners.ChatBadgeEventListener
import com.kg.gettransfer.domain.eventListeners.OfferEventListener
import com.kg.gettransfer.domain.interactor.*
import com.kg.gettransfer.domain.model.ChatBadgeEvent
import com.kg.gettransfer.domain.model.Offer
import com.kg.gettransfer.domain.model.Result

import com.kg.gettransfer.presentation.delegate.AccountManager
import com.kg.gettransfer.presentation.mapper.OfferMapper
import com.kg.gettransfer.presentation.model.OfferModel

import com.kg.gettransfer.presentation.view.BaseView
import com.kg.gettransfer.presentation.view.Screens

import com.kg.gettransfer.sys.domain.GetPreferencesInteractor

import com.kg.gettransfer.utilities.Analytics
import com.kg.gettransfer.utilities.GTDownloadManager
import com.kg.gettransfer.utilities.GTNotificationManager

import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

import org.koin.core.KoinComponent
import org.koin.core.get
import org.koin.core.inject
import org.koin.core.parameter.parametersOf

import org.slf4j.Logger

import ru.terrakok.cicerone.Router

open class BasePresenter<BV : BaseView> : MvpPresenter<BV>(),
    OfferEventListener,
    ChatBadgeEventListener,
    KoinComponent {

    protected val compositeDisposable = Job()
    protected val utils = AsyncUtils(get<CoroutineContexts>(), compositeDisposable)
    protected val router: Router by inject()
    protected val analytics: Analytics by inject()
    protected val offerMapper: OfferMapper by inject()
    protected val notificationManager: GTNotificationManager by inject()
    protected val offerInteractor: OfferInteractor by inject()
    protected val transferInteractor: TransferInteractor by inject()
    protected val carrierTripInteractor: CarrierTripInteractor by inject()
    protected val chatInteractor: ChatInteractor by inject()
    protected val countEventsInteractor: CountEventsInteractor by inject()
    protected val reviewInteractor: ReviewInteractor by inject()

    private val pushTokenInteractor: PushTokenInteractor by inject()
    protected val socketInteractor: SocketInteractor by inject()
    protected val sessionInteractor: SessionInteractor by inject()
    protected val accountManager: AccountManager by inject()
    protected val downloadManager: GTDownloadManager by inject()

    private val worker: WorkerManager by inject { parametersOf("BasePresenter") }
    protected val getPreferences: GetPreferencesInteractor by inject()

    //private var sendingMessagesNow = false
    private var openedLoginScreenForUnauthorizedUser = false

    protected val log: Logger by inject { parametersOf("GTR-presenter") }

    open fun onBackCommandClick() {
        analytics.logEvent(Analytics.EVENT_MAIN, Analytics.PARAM_KEY_NAME, Analytics.BACK_CLICKED)
        router.exit()
    }

    protected fun login(nextScreen: String, email: String?) = router.navigateTo(Screens.MainLogin(nextScreen, email))

    override fun onFirstViewAttach() {
        if (sessionInteractor.isInitialized) return
        utils.launchSuspend {
            fetchData { sessionInteractor.coldStart() }?.let { systemInitialized() }
        }
        worker.main.launch {
            offerMapper.url = getPreferences().getModel().endpoint!!.url
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

    private fun checkResultError(error: ApiException) =
        if (!openedLoginScreenForUnauthorizedUser && (error.isNotLoggedIn() || error.isNoUser())) {
            openedLoginScreenForUnauthorizedUser = true
            login(Screens.CLOSE_AFTER_LOGIN, accountManager.remoteProfile.email)
            false
        } else if (openedLoginScreenForUnauthorizedUser) {
            logout()
            false
        } else true

    private fun logout() {
        utils.launchSuspend {
            clearAllCachedData()
            router.backTo(Screens.MainPassenger(true))
        }
    }

    protected suspend fun clearAllCachedData() {
        utils.asyncAwait { pushTokenInteractor.unregisterPushToken() }
        utils.asyncAwait { accountManager.logout() }

        utils.asyncAwait { transferInteractor.clearTransfersCache() }
        utils.asyncAwait { offerInteractor.clearOffersCache() }
        utils.asyncAwait { carrierTripInteractor.clearCarrierTripsCache() }
        utils.asyncAwait { reviewInteractor.clearReviewCache() }

        countEventsInteractor.clearCountEvents()
    }

    fun saveGeneralSettings(withRestartApp: Boolean = false) {
        if (accountManager.hasAccount) saveAccount(withRestartApp) else saveNoAccount(withRestartApp)
    }

    fun saveAccount(withRestartApp: Boolean = false) = utils.launchSuspend {
        viewState.blockInterface(true)
        val result = utils.asyncAwait { accountManager.putAccount(isTempAccount = false) }
        result.error?.let { if (!it.isNotLoggedIn()) viewState.setError(it) }
        if (result.error == null && withRestartApp) {
            restartApp()
        }
        viewState.blockInterface(false)
    }

    fun saveNoAccount(withRestartApp: Boolean) = utils.launchSuspend {
        val result = utils.asyncAwait { sessionInteractor.putNoAccount() }
        if (result.error == null && withRestartApp) {
            restartApp()
        }
    }

    open fun restartApp() {}

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

    override fun onDestroy() {
        compositeDisposable.cancel()
        worker.cancel()
        super.onDestroy()
    }

    protected open fun systemInitialized() {}

    fun networkConnected() {
        utils.launchSuspend {
            utils.asyncAwait { reviewInteractor.checkNotSendedReviews() }
        }
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
                    transferID,
                    accountManager.remoteProfile.email
                )
            )
        }
    }

    internal fun callPhone(phone: String) {
        router.navigateTo(Screens.CallPhone(phone))
    }

    protected fun registerPushToken() {
        FirebaseInstanceId.getInstance().instanceId.addOnCompleteListener {
            if (it.isSuccessful) {
                it.result?.token?.let {
                    log.debug("[FCM token]: $it")
                    utils.launchSuspend {
                        fetchResult { pushTokenInteractor.registerPushToken(it) }
                    }
                }
            } else log.warn("getInstanceId failed", it.exception)
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
        return offerMapper.toView(offer).also { notificationManager.showOfferNotification(it) }
    }

    override fun onNewOfferEvent(offer: Offer) {
        onNewOffer(offer.also {
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
        if (!chatBadgeEvent.clearBadge) {
            utils.launchSuspend {
                fetchDataOnly { transferInteractor.getTransfer(chatBadgeEvent.transferId) }?.let { transfer ->
                    increaseEventsMessagesCounter(chatBadgeEvent.transferId, transfer.unreadMessagesCount)
                    notificationManager.showNewMessageNotification(
                        chatBadgeEvent.transferId,
                        transfer.unreadMessagesCount,
                        true
                    )
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

    fun onAppStateChanged(isForeGround: Boolean) = worker.main.launch {
        with(socketInteractor) {
            if (isForeGround && accountManager.hasAccount) {
                withContext(worker.bg) { openSocketConnection() }
            } else if (getPreferences().getModel().lastMode != Screens.CARRIER_MODE ||
                getPreferences().getModel().isBackgroundCoordinatesRejected()) {
                closeSocketConnection()
            }
        }
    }

    private fun increaseEventsOffersCounter(transferId: Long) = with(countEventsInteractor) {
        eventsCount += 1
        mapCountNewOffers = increaseMapCounter(transferId, mapCountNewOffers)
    }

    protected fun increaseViewedOffersCounter(transferId: Long, plusCount: Int) = with(countEventsInteractor) {
        eventsCount -= plusCount
        mapCountViewedOffers = increaseMapCounter(transferId, mapCountViewedOffers, null, plusCount)
    }

    private fun decreaseViewedOffersCounter(transferId: Long) = with(countEventsInteractor) {
        eventsCount += 1
        mapCountViewedOffers = decreaseMapCounter(transferId, mapCountViewedOffers)
    }

    private fun increaseEventsMessagesCounter(transferId: Long, count: Int) = with(countEventsInteractor) {
        eventsCount = eventsCount + count - (mapCountNewMessages[transferId] ?: 0)
        mapCountNewMessages = increaseMapCounter(transferId, mapCountNewMessages, count)
    }

    protected fun decreaseEventsMessagesCounter(transferId: Long) = with(countEventsInteractor) {
        mapCountNewMessages = decreaseMapCounter(transferId, mapCountNewMessages)
    }

    private fun increaseMapCounter(
        transferId: Long,
        map: Map<Long, Int>,
        count: Int? = null,
        plussedCount: Int? = null
    ) = map.toMutableMap().apply { put(transferId, count ?: map[transferId]?.plus(plussedCount ?: 1) ?: 1) }

    private fun decreaseMapCounter(transferId: Long, map: Map<Long, Int>) = map.toMutableMap().apply {
        if (this[transferId] != null) {
            if (map.getValue(transferId) > 1) {
                put(transferId, map.getValue(transferId) - 1)
            } else {
                remove(transferId)
            }
        }
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
    protected suspend fun <M> fetchResult(
        processError: Boolean = DEFAULT_ERROR,
        withCacheCheck: Boolean = CHECK_CACHE,
        checkLoginError: Boolean = true,
        block: suspend () -> Result<M>
    ) = utils.asyncAwait { block() }.also {
        it.error?.let { e -> if (checkLoginError) checkResultError(e) else true }
            ?.let { handle ->
                if (!handle) return@also
                if (withCacheCheck) !it.fromCache else true
            }?.let { resultCheck ->
                if (!processError && resultCheck) it.error?.let { e -> viewState.setError(e) }
                log.error("BasePresenter.fetchResult", it.error)
            }
    }

    /*
    Method to fetch only data without result if no need to have error object in client class.
    As we unwrap return data safely in client class, so it's possible to use it without care.
     */
    protected suspend fun <D> fetchData(
        processError: Boolean = DEFAULT_ERROR,
        withCacheCheck: Boolean = CHECK_CACHE,
        checkLoginError: Boolean = true,
        block: suspend () -> Result<D>
    ) = with(fetchResult(processError, withCacheCheck, checkLoginError) { block() }) {
        if (error == null || withCacheCheck && fromCache) model else null
    }

    /*
    Optional methods for easy request with only suspend block and without params to handle
    errors.
     */
    protected suspend fun <R> fetchResultOnly(block: suspend () -> Result<R>) =
        fetchResult(WITHOUT_ERROR, NO_CACHE_CHECK, false) { block() }

    protected suspend fun <D> fetchDataOnly(block: suspend () -> Result<D>) =
        fetchData(WITHOUT_ERROR, NO_CACHE_CHECK, false) { block() }

    companion object {
        //when you want to handle error in child presenter
        const val SHOW_ERROR = true
        const val DEFAULT_ERROR = false
        //the same as SHOW_ERROR, but when you will not show error even in child presenter
        const val WITHOUT_ERROR = true
        const val CHECK_CACHE = true
        const val NO_CACHE_CHECK = false
    }
}
