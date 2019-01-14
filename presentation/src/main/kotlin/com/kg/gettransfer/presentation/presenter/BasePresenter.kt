package com.kg.gettransfer.presentation.presenter

import android.os.Bundle
import android.support.annotation.CallSuper

import com.arellomobile.mvp.MvpPresenter

import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.iid.FirebaseInstanceId
import com.kg.gettransfer.domain.ApiException

import com.kg.gettransfer.domain.AsyncUtils
import com.kg.gettransfer.domain.CoroutineContexts
import com.kg.gettransfer.domain.interactor.OfferInteractor
import com.kg.gettransfer.domain.interactor.SystemInteractor
import com.kg.gettransfer.domain.model.Offer

import com.kg.gettransfer.presentation.mapper.OfferMapper
import com.kg.gettransfer.presentation.model.OfferModel

import com.kg.gettransfer.presentation.view.BaseView
import com.kg.gettransfer.presentation.view.Screens

import com.kg.gettransfer.utilities.Analytics
import com.kg.gettransfer.utilities.NotificationManager

import kotlinx.coroutines.Job

import org.koin.standalone.get
import org.koin.standalone.inject
import org.koin.standalone.KoinComponent

import ru.terrakok.cicerone.Router

import timber.log.Timber

open class BasePresenter<BV: BaseView> : MvpPresenter<BV>(), KoinComponent {
    protected val compositeDisposable = Job()
    protected val utils = AsyncUtils(get<CoroutineContexts>(), compositeDisposable)
    protected val router: Router by inject()
    protected val analytics: Analytics by inject()
    protected val systemInteractor: SystemInteractor by inject()
    protected val offerMapper: OfferMapper by inject()
    protected val notificationManager: NotificationManager by inject()
    protected val offerInteractor: OfferInteractor by inject()

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

    @CallSuper
    override fun onDestroy() {
        compositeDisposable.cancel()
        super.onDestroy()
    }

    protected open fun systemInitialized() {}

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

    internal fun sendEmail(emailCarrier: String?) {
        router.navigateTo(
            Screens.SendEmail(
                emailCarrier,
                if (emailCarrier == null) systemInteractor.logsFile else null
            )
        )
    }

    internal fun callPhone(phone: String) {
        router.navigateTo(Screens.CallPhone(phone))
    }

    protected fun registerPushToken() {
        FirebaseInstanceId.getInstance().instanceId.addOnCompleteListener {
            if (it.isSuccessful) {
                it.result?.token?.let {
                    Timber.d("[FCM token]: $it")
                    utils.runAlien {
                        try { systemInteractor.registerPushToken(it) }
                        catch (e: ApiException) { viewState.setError(e) }
                    }
                }
            } else Timber.w("getInstanceId failed", it.exception)
        }
    }

    open fun onNewOffer(offer: Offer): OfferModel {
        offerInteractor.newOffer(offer)
        val offerModel = offerMapper.toView(offer)
        notificationManager.showOfferNotification(offerModel)
        return offerModel
    }

    fun saveAccount() = utils.launchSuspend {
        viewState.blockInterface(true)
        val result = utils.asyncAwait { systemInteractor.putAccount() }
        result.error?.let { if (!it.isNotLoggedIn()) viewState.setError(it) }
        viewState.blockInterface(false)
    }

    companion object AnalyticProps {
        const val SINGLE_CAPACITY = 1
        const val DOUBLE_CAPACITY = 2
    }
}
