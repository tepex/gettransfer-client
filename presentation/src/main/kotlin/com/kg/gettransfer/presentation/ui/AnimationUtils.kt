package com.kg.gettransfer.presentation.ui

import android.animation.Animator
import android.animation.AnimatorInflater
import android.animation.AnimatorListenerAdapter
import android.content.Context

object AnimationUtils {

    fun onCreateAnimation(context: Context, enter: Boolean, executeAfterAnimEnd: () -> Unit): Animator {
        val animatorId: Int = if (enter) android.R.animator.fade_in else android.R.animator.fade_out
        val anim = AnimatorInflater.loadAnimator(context, animatorId)
        anim.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
                super.onAnimationEnd(animation)
                if (enter) {
                    executeAfterAnimEnd()
                }
            }
        })
        return anim
    }

}