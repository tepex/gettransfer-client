package com.kg.gettransfer.presentation.ui.utils

import android.animation.Animator
import android.animation.AnimatorInflater
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.support.annotation.IdRes
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager

object FragmentUtils {

    fun replaceFragment(fm: FragmentManager, fragment: Fragment, @IdRes id: Int, tag: String? = null) =
            fm
                    .beginTransaction()
                    .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                    .replace(id, fragment, tag)
                    .commitAllowingStateLoss()

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