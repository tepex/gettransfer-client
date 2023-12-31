package com.kg.gettransfer.presentation.presenter

import com.kg.gettransfer.core.presentation.WorkerManager

import com.kg.gettransfer.domain.ApiException
import com.kg.gettransfer.domain.AsyncUtils
import com.kg.gettransfer.domain.CoroutineContexts
import com.kg.gettransfer.domain.eventListeners.ChatBadgeEventListener
import com.kg.gettransfer.domain.eventListeners.OfferEventListener

import com.kg.gettransfer.domain.interactor.OfferInteractor
import com.kg.gettransfer.domain.interactor.TransferInteractor
import com.kg.gettransfer.domain.interactor.ChatInteractor
import com.kg.gettransfer.domain.interactor.CountEventsInteractor
import com.kg.gettransfer.domain.interactor.ReviewInteractor
import com.kg.gettransfer.domain.interactor.PaymentInteractor
import com.kg.gettransfer.domain.interactor.OrderInteractor
import com.kg.gettransfer.domain.interactor.SocketInteractor
import com.kg.gettransfer.domain.interactor.SessionInteractor

import com.kg.gettransfer.domain.model.ChatBadgeEvent
import com.kg.gettransfer.domain.model.Offer
import com.kg.gettransfer.domain.model.Result
import com.kg.gettransfer.domain.model.Transfer
import com.kg.gettransfer.domain.model.TransportType

import com.kg.gettransfer.extensions.getOffer

import com.kg.gettransfer.presentation.delegate.AccountManager
import com.kg.gettransfer.presentation.delegate.PushTokenManager

import com.kg.gettransfer.presentation.view.BaseView
import com.kg.gettransfer.presentation.view.Screens

import com.kg.gettransfer.sys.domain.SetFavoriteTransportsInteractor
import com.kg.gettransfer.sys.presentation.ConfigsManager

import com.kg.gettransfer.utilities.Analytics
import com.kg.gettransfer.utilities.GTDownloadManager

import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

import moxy.MvpPresenter

import org.koin.core.KoinComponent
import org.koin.core.get
import org.koin.core.inject
import org.koin.core.parameter.parametersOf

import org.slf4j.Logger

import ru.terrakok.cicerone.Router

@Suppress("TooManyFunctions")
open class BasePresenter<BV : BaseView> : MvpPresenter<BV>(),
    OfferEventListener,
    ChatBadgeEventListener,
    KoinComponent {

    protected val compositeDisposable = Job()
    protected val utils = AsyncUtils(get<CoroutineContexts>(), compositeDisposable)
    protected val router: Router by inject()
    protected val analytics: Analytics by inject()
    protected val offerInteractor: OfferInteractor by inject()
    protected val transferInteractor: TransferInteractor by inject()
    protected val chatInteractor: ChatInteractor by inject()
    protected val countEventsInteractor: CountEventsInteractor by inject()
    protected val reviewInteractor: ReviewInteractor by inject()
    protected val paymentInteractor: PaymentInteractor by inject()
    protected val orderInteractor: OrderInteractor by inject()

    protected val socketInteractor: SocketInteractor by inject()
    protected val sessionInteractor: SessionInteractor by inject()
    protected val accountManager: AccountManager by inject()
    protected val pushTokenManager: PushTokenManager by inject()
    protected val downloadManager: GTDownloadManager by inject()

    private val worker: WorkerManager by inject { parametersOf("BasePresenter") }

    private val setFavoriteTransports: SetFavoriteTransportsInteractor by inject()

    private var openedLoginScreenForUnauthorizedUser = false

    protected val log: Logger by inject { parametersOf("GTR-presenter") }

    protected val configsManager: ConfigsManager by inject()

    open fun onBackCommandClick() {
        analytics.logEvent(Analytics.EVENT_MAIN, Analytics.PARAM_KEY_NAME, Analytics.BACK_CLICKED)
        router.exit()
    }

    override fun onFirstViewAttach() {
        worker.main.launch {
            withContext(worker.bg) {
                if (sessionInteractor.isInitialized) {
                    systemInitialized()
                } else {
                    sessionInteractor.coldStart()
                    systemInitialized()
                }
            }
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

    private fun checkAuthorizationError(error: ApiException) =
        if (!openedLoginScreenForUnauthorizedUser && (error.isNotLoggedIn() || error.isNoUser())) {
            openedLoginScreenForUnauthorizedUser = true
            router.navigateTo(Screens.MainLogin(Screens.CLOSE_AFTER_LOGIN, accountManager.remoteProfile.email))
            false
        } else if (openedLoginScreenForUnauthorizedUser) {
            isAuthorizationError()
            false
        } else true

    private fun isAuthorizationError() = utils.launchSuspend {
        utils.asyncAwait { accountManager.logout() }.isSuccess()?.let {
            router.newRootScreen(Screens.MainPassenger())
        }
    }

    protected fun saveChosenTransportTypes(transportTypes: Set<TransportType.ID>) {
        worker.main.launch {
            withContext(worker.bg) { setFavoriteTransports(transportTypes) }
        }
    }

    protected fun clearChosenTransportTypes() {
        orderInteractor.clear()
        saveChosenTransportTypes(emptySet())
    }

    suspend fun saveGeneralSettings() {
        viewState.blockInterface(true)
        val result = utils.asyncAwait { accountManager.saveSettings() }
        result.error?.let { if (!it.isNotLoggedIn()) viewState.setError(it) }
        viewState.blockInterface(false)
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

    // open fun doingSomethingAfterSendingNewMessagesCached() {}

    protected open fun systemInitialized() {}

    fun networkConnected() = worker.main.launch {
        withContext(worker.bg) { reviewInteractor.checkNotSendedReviews() }
    }

    fun getUserRole(): String =
        if (isBusinessAccount()) Transfer.Role.PARTNER.toString() else Transfer.Role.PASSENGER.toString()

    fun isBusinessAccount(): Boolean = accountManager.remoteAccount.isBusinessAccount

    open suspend fun onNewOffer(offer: Offer) {  }

    override fun onNewOfferEvent(offer: Offer) {
        worker.main.launch {
            updateOfferEventsCounter(offer)
            withContext(worker.bg) { offerInteractor.newOffer(offer) }
            onNewOffer(offer)
        }
    }

    @Suppress("NestedBlockDepth")
    private suspend fun updateOfferEventsCounter(offer: Offer) {
        val result = withContext(worker.bg) { offerInteractor.getOffers(offer.transferId, true) }
        if (result.error == null) {
            if (result.model.getOffer(offer.id) != null) {
                countEventsInteractor.mapCountViewedOffers[offer.transferId]?.let { countViewedOffers ->
                    countEventsInteractor.mapCountNewOffers[offer.transferId]?.let { countNewOffers ->
                        if (countNewOffers == countViewedOffers && countViewedOffers > 0) {
                            decreaseViewedOffersCounter(offer.transferId)
                        }
                    }
                }
            } else {
                increaseEventsOffersCounter(offer.transferId)
            }
        }
    }

    override fun onChatBadgeChangedEvent(chatBadgeEvent: ChatBadgeEvent) {
        worker.main.launch {
            if (!chatBadgeEvent.clearBadge) {
                val result = withContext(worker.bg) { transferInteractor.getTransfer(chatBadgeEvent.transferId) }
                if (result.error == null) {
                    increaseEventsMessagesCounter(chatBadgeEvent.transferId, result.model.unreadMessagesCount)
                }
            } else {
                with(countEventsInteractor) {
                    mapCountNewMessages = mapCountNewMessages.toMutableMap().apply {
                        this[chatBadgeEvent.transferId]?.let { count ->
                            eventsCount -= count
                            remove(chatBadgeEvent.transferId)
                        }
                    }
                }
            }
        }
    }

    fun onAppStateChanged(isForeGround: Boolean) {
        with(socketInteractor) {
            if (isForeGround && accountManager.hasAccount) {
                openSocketConnection()
            } else {
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
    ) = map.toMutableMap().apply { put(transferId, count ?: (map[transferId] ?: 0).plus(plussedCount ?: 1)) }

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
    @Suppress("ComplexMethod")
    protected suspend fun <M> fetchResult(
        processError: Boolean = DEFAULT_ERROR,
        withCacheCheck: Boolean = CHECK_CACHE,
        checkLoginError: Boolean = true,
        block: suspend () -> Result<M>
    ) = utils.asyncAwait { block() }.also { result ->
        result.error?.let { e -> if (checkLoginError) checkAuthorizationError(e) else true }
            ?.let { handle ->
                if (!handle) return@also
                if (withCacheCheck) !result.fromCache else true
            }?.let { resultCheck ->
                if (!processError && resultCheck) result.error?.let { e -> viewState.setError(e) }
                log.error("BasePresenter.fetchResult", result.error)
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

    override fun onDestroy() {
        compositeDisposable.cancel()
        worker.cancel()
        super.onDestroy()
    }

    companion object {
        // when you want to handle error in child presenter
        const val SHOW_ERROR = true
        const val DEFAULT_ERROR = false
        // the same as SHOW_ERROR, but when you will not show error even in child presenter
        const val WITHOUT_ERROR = true
        const val CHECK_CACHE = true
        const val NO_CACHE_CHECK = false
    }
}
