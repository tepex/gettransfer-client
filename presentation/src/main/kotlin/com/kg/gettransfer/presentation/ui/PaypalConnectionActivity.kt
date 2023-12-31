package com.kg.gettransfer.presentation.ui

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import androidx.annotation.CallSuper
import android.view.View
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import com.kg.gettransfer.R
import com.kg.gettransfer.presentation.presenter.PaypalConnectionPresenter
import com.kg.gettransfer.presentation.view.PaypalConnectionView
import kotlinx.android.synthetic.main.activity_paypal_connection.*

class PaypalConnectionActivity : BaseActivity(), PaypalConnectionView {

    @InjectPresenter
    internal lateinit var presenter: PaypalConnectionPresenter

    override fun getPresenter(): PaypalConnectionPresenter = presenter

    @ProvidePresenter
    fun createPaypalConnectionPresenter() = PaypalConnectionPresenter()

    private var animator: AnimatorSet? = null

    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        presenter.paymentId = intent.getLongExtra(PaypalConnectionView.EXTRA_PAYMENT_ID, 0L)
        presenter.nonce = intent.getStringExtra(PaypalConnectionView.EXTRA_NONCE)
        presenter.transferId = intent.getLongExtra(PaypalConnectionView.EXTRA_TRANSFER_ID, 0L)
        presenter.offerId = intent.getLongExtra(PaypalConnectionView.EXTRA_OFFER_ID, 0L)
        setContentView(R.layout.activity_paypal_connection)
    }

    private fun playAnimation() {

        val dx = DX * ivRec1.width.toFloat()

        // animation for left rectangles
        val (lftToRgt1, lftToRgt3) = animateLeftToRight13(dx)
        val (rgtToLft1, rgtToLft3) = animateRightToLeft13(dx)

        // animation for right rectangles
        val (rgtToLft2, rgtToLft4) = animateRightToLeft24(dx)
        val (lftToRgt2, lftToRgt4) = animateLeftToRight24()

        animator = AnimatorSet().apply {
            play(lftToRgt1).with(lftToRgt3).with(rgtToLft2).with(rgtToLft4)
            play(rgtToLft1).with(rgtToLft3).with(lftToRgt2).with(lftToRgt4).after(lftToRgt3)
            start()
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    super.onAnimationEnd(animation)
                    animation?.start()
                }
            })
        }
    }

    private fun animateLeftToRight24(): Pair<ObjectAnimator, ObjectAnimator> {
        val lftToRgt2 = ObjectAnimator
                .ofFloat(ivRec2, View.TRANSLATION_X, -ivRec1.right.toFloat(), 0f)
                .apply {
                    duration = DURATION_LEFT_TO_RIGHT_24
                    startDelay = START_DELAY_LFT_TO_RGT_2
                }
        val lftToRgt4 = ObjectAnimator
                .ofFloat(ivRec4, View.TRANSLATION_X, -ivRec3.right.toFloat(), 0f)
                .apply {
                    duration = DURATION_LEFT_TO_RIGHT_24
                    startDelay = START_DELAY_LFT_TO_RGT_4
                }
        return lftToRgt2 to lftToRgt4
    }

    private fun animateRightToLeft24(dx: Float): Pair<ObjectAnimator, ObjectAnimator> {
        val rgtToLft2 = ObjectAnimator
                .ofFloat(ivRec2, View.TRANSLATION_X, 0f, -dx)
                .apply {
                    duration = DURATION_1000
                    startDelay = START_DELAY_RGT_TO_LFT_2
                }
        val rgtToLft4 = ObjectAnimator
                .ofFloat(ivRec4, View.TRANSLATION_X, 0f, -dx)
                .apply {
                    duration = DURATION_1000
                    startDelay = START_DELAY_300
                }
        return rgtToLft2 to rgtToLft4
    }

    private fun animateRightToLeft13(dx: Float): Pair<ObjectAnimator, ObjectAnimator> {
        val rgtToLft1 = ObjectAnimator
                .ofFloat(ivRec1, View.TRANSLATION_X, dx, 0f)
                .apply {
                    duration = DURATION_1000
                    startDelay = START_DELAY_RGT_TO_LFT_1
                }
        val rgtToLft3 = ObjectAnimator
                .ofFloat(ivRec3, View.TRANSLATION_X, dx, 0f)
                .setDuration(DURATION_RGT_TO_LFT_3)
        return rgtToLft1 to rgtToLft3
    }

    private fun animateLeftToRight13(dx: Float): Pair<ObjectAnimator, ObjectAnimator> {
        val lftToRgt1 = ObjectAnimator
                .ofFloat(ivRec1, View.TRANSLATION_X, 0f, dx)
                .setDuration(DURATION_1000)
        val lftToRgt3 = ObjectAnimator
                .ofFloat(ivRec3, View.TRANSLATION_X, 0f, dx)
                .apply {
                    duration = DURATION_1000
                    startDelay = START_DELAY_300
                }
        return lftToRgt1 to lftToRgt3
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            playAnimation()
        }
    }

    override fun stopAnimation() {
        animator?.apply {
            cancel()
            removeAllListeners()
        }
    }

    companion object {
        private const val DURATION_LEFT_TO_RIGHT_24 = 700L
        private const val DURATION_RGT_TO_LFT_3 = 800L
        private const val DURATION_1000 = 1000L
        private const val START_DELAY_LFT_TO_RGT_2 = 400L
        private const val START_DELAY_LFT_TO_RGT_4 = 600L
        private const val START_DELAY_RGT_TO_LFT_2 = 500L
        private const val START_DELAY_RGT_TO_LFT_1 = 200L
        private const val START_DELAY_300 = 300L
        private const val DX = 3
    }
}
