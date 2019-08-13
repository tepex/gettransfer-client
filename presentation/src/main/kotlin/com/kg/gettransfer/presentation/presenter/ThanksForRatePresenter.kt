package com.kg.gettransfer.presentation.presenter

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter

import com.kg.gettransfer.domain.AsyncUtils
import com.kg.gettransfer.domain.CoroutineContexts
import com.kg.gettransfer.domain.interactor.ReviewInteractor
import com.kg.gettransfer.presentation.view.ThanksForRateView

import kotlinx.coroutines.Job

import org.koin.core.KoinComponent
import org.koin.core.inject

@InjectViewState
class ThanksForRatePresenter: MvpPresenter<ThanksForRateView>(), KoinComponent {

    private val compositeDisposable = Job()
    private val coroutineContexts: CoroutineContexts by inject()
    private val utils = AsyncUtils(coroutineContexts, compositeDisposable)
    private val reviewInteractor: ReviewInteractor by inject()

    fun sendThanks() {
        utils.launchSuspend {
            with(reviewInteractor) {
                if (comment.isNotEmpty()) utils.asyncAwait { pushComment() }
                releaseReviewData()
                showStoreDialog(shouldAskRateInMarket)
            }
            compositeDisposable.cancel()
        }
    }

    private fun showStoreDialog(shouldAskRateInMarket: Boolean) {
        if (shouldAskRateInMarket) {
            reviewInteractor.shouldAskRateInMarket = false
            viewState.askRateInPlayMarket()
        }
    }

    fun setComment(comment: String) {
        reviewInteractor.comment = comment
    }
}
