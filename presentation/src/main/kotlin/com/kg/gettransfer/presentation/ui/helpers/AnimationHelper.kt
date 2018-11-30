package com.kg.gettransfer.presentation.ui.helpers

import android.content.Context
import android.os.Handler
import android.view.View
import android.view.animation.AnimationUtils
import com.kg.gettransfer.R

class AnimationHelper(val context: Context) {
    fun hourlyAnim(viewOut: View, imgOut: View, viewIn: View, imgIn: View) {
        Handler().postDelayed({
            viewOut.visibility = View.GONE
            imgOut.visibility  = View.GONE
            viewIn.visibility  = View.VISIBLE
            viewIn.startAnimation(AnimationUtils.loadAnimation(context, R.anim.transition_r2l))
            imgOut.visibility  = View.VISIBLE
            imgIn.startAnimation(AnimationUtils.loadAnimation(context, R.anim.transition_down_bounced))


        }, 300)
        viewOut.startAnimation(AnimationUtils.loadAnimation(context, R.anim.transition_l2r))
        imgOut.startAnimation(AnimationUtils.loadAnimation(context, R.anim.transition_up))
    }
}