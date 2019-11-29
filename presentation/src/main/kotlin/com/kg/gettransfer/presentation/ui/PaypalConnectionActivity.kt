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
import com.kg.gettransfer.presentation.model.PaymentRequestModel
import com.kg.gettransfer.presentation.presenter.PaypalConnectionPresenter
import com.kg.gettransfer.presentation.view.PaypalConnectionView
import kotlinx.android.synthetic.main.activity_paypal_connection.*

@Suppress("MagicNumber")
class PaypalConnectionActivity : BaseActivity(), PaypalConnectionView {

    @InjectPresenter
    internal lateinit var presenter: PaypalConnectionPresenter

    override fun getPresenter(): PaypalConnectionPresenter = presenter

    @ProvidePresenter
    fun createPaypalConnectionPresenter() = PaypalConnectionPresenter()

    private lateinit var animator: AnimatorSet

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
        animator = AnimatorSet()

        val dx = 3 * ivRec1.width.toFloat()

        // animation for left rectangles
        val (lftToRgt1, lftToRgt3) = animateLeftToRight13(dx)
        val (rgtToLft1, rgtToLft3) = animateRightToLeft13(dx)

        // animation for right rectangles
        val (rgtToLft2, rgtToLft4) = animateRightToLeft24(dx)
        val (lftToRgt2, lftToRgt4) = animateLeftToRight24()

        animator.play(lftToRgt1).with(lftToRgt3).with(rgtToLft2).with(rgtToLft4)
        animator.play(rgtToLft1).with(rgtToLft3).with(lftToRgt2).with(lftToRgt4).after(lftToRgt3)
        animator.start()
        animator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
                super.onAnimationEnd(animation)
                animation?.start()
            }
        })
    }

    private fun animateLeftToRight24(): Pair<ObjectAnimator, ObjectAnimator> {
        val lftToRgt2 = ObjectAnimator
                .ofFloat(ivRec2, View.TRANSLATION_X, -ivRec1.right.toFloat(), 0f)
                .apply {
                    duration = 700
                    startDelay = 400
                }
        val lftToRgt4 = ObjectAnimator
                .ofFloat(ivRec4, View.TRANSLATION_X, -ivRec3.right.toFloat(), 0f)
                .apply {
                    duration = 700
                    startDelay = 600
                }
        return lftToRgt2 to lftToRgt4
    }

    private fun animateRightToLeft24(dx: Float): Pair<ObjectAnimator, ObjectAnimator> {
        val rgtToLft2 = ObjectAnimator
                .ofFloat(ivRec2, View.TRANSLATION_X, 0f, -dx)
                .apply {
                    duration = 1000
                    startDelay = 500
                }
        val rgtToLft4 = ObjectAnimator
                .ofFloat(ivRec4, View.TRANSLATION_X, 0f, -dx)
                .apply {
                    duration = 1000
                    startDelay = 300
                }
        return rgtToLft2 to rgtToLft4
    }

    private fun animateRightToLeft13(dx: Float): Pair<ObjectAnimator, ObjectAnimator> {
        val rgtToLft1 = ObjectAnimator
                .ofFloat(ivRec1, View.TRANSLATION_X, dx, 0f)
                .apply {
                    duration = 1000
                    startDelay = 200
                }
        val rgtToLft3 = ObjectAnimator
                .ofFloat(ivRec3, View.TRANSLATION_X, dx, 0f)
                .setDuration(800)
        return rgtToLft1 to rgtToLft3
    }

    private fun animateLeftToRight13(dx: Float): Pair<ObjectAnimator, ObjectAnimator> {
        val lftToRgt1 = ObjectAnimator
                .ofFloat(ivRec1, View.TRANSLATION_X, 0f, dx)
                .setDuration(1000)
        val lftToRgt3 = ObjectAnimator
                .ofFloat(ivRec3, View.TRANSLATION_X, 0f, dx)
                .apply {
                    duration = 1000
                    startDelay = 300
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
        animator.cancel()
        animator.removeAllListeners()
    }
}
