package com.kg.gettransfer.presentation.ui.utils

import android.animation.Animator
import android.animation.AnimatorInflater
import android.animation.AnimatorListenerAdapter
import android.content.Context
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.kg.gettransfer.R

object FragmentUtils {

    fun replaceFragment(
        fragmentManager: FragmentManager,
        fragment: Fragment,
        @IdRes containerViewId: Int,
        tag: String? = null,
        addToBackStack: Boolean = false,
        useAnimation: Boolean = true
    ) = fragmentManager
        .beginTransaction().apply {
            if (useAnimation) setCustomAnimations(R.anim.transition_b2t, R.anim.transition_t2b)
        }
        .replace(containerViewId, fragment, tag).apply {
            if (addToBackStack) addToBackStack(null)
        }
        .commitAllowingStateLoss()

    fun onCreateAnimation(context: Context, enter: Boolean, executeAfterAnimEnd: () -> Unit): Animator {
        val animatorId: Int = if (enter) R.animator.transition_b2t else R.animator.transition_t2b
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
