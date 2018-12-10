package com.kg.gettransfer.presentation.ui.helpers

import android.content.Context
import android.os.Handler
import android.view.View
import android.view.animation.AnimationUtils

import com.kg.gettransfer.R
import com.kg.gettransfer.extensions.*

class AnimationHelper(val context: Context) {
    fun hourlyAnim(viewOut: View, imgOut: View, viewIn: View, imgIn: View) {
        Handler().postDelayed({
            viewOut.isVisible = false
            imgOut.isVisible = false
            viewIn.isVisible = true
            viewIn.startAnimation(AnimationUtils.loadAnimation(context, R.anim.transition_r2l))
            imgOut.isVisible = true
            imgIn.startAnimation(AnimationUtils.loadAnimation(context, R.anim.transition_down_bounced))
        }, 300)

        viewOut.startAnimation(AnimationUtils.loadAnimation(context, R.anim.transition_l2r))
        imgOut.startAnimation(AnimationUtils.loadAnimation(context, R.anim.transition_up))
    }
}
