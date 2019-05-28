package com.kg.gettransfer.presentation.presenter

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.kg.gettransfer.domain.AsyncUtils
import com.kg.gettransfer.domain.CoroutineContexts
import com.kg.gettransfer.domain.interactor.ReviewInteractor
import com.kg.gettransfer.presentation.view.ThanksForRateView
import kotlinx.coroutines.Job
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject

@InjectViewState
class ThanksForRatePresenter: MvpPresenter<ThanksForRateView>(), KoinComponent {
    private val compositeDisposable = Job()
    private val coroutineContexts: CoroutineContexts by inject()
    private val utils = AsyncUtils(coroutineContexts, compositeDisposable)
    private val reviewInteractor: ReviewInteractor by inject()

    override fun attachView(view: ThanksForRateView?) {
        super.attachView(view)
        viewState.setComment(reviewInteractor.comment)
    }

    fun sendThanks() {
        utils.launchSuspend {
            with(reviewInteractor) {
                if (comment.isNotEmpty()) {
                    utils.asyncAwait { pushComment() }
                }
                releaseRepo()
            }
            compositeDisposable.cancel()
        }
    }
}