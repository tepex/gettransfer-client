package com.kg.gettransfer.presentation.ui.behavior

import android.app.Activity
import android.content.Context
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import androidx.coordinatorlayout.widget.CoordinatorLayout

open class BaseBehavior<V : ViewGroup>(private val mContext: Context, attrs: AttributeSet) : CoordinatorLayout.Behavior<V>(mContext, attrs) {
    val screenHeight: Int
    var actionBarHeight: Int = 0

    init {
        val displayMetrics = DisplayMetrics()
        (mContext as Activity).windowManager
                .defaultDisplay
                .getMetrics(displayMetrics)
        screenHeight = displayMetrics.heightPixels

        val tv = TypedValue()
        actionBarHeight = if (mContext.getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            TypedValue.complexToDimensionPixelSize(tv.data, mContext.getResources().displayMetrics)
        } else 0
    }

    protected fun isBottomSheet(view: View): Boolean {
        val lp = view.layoutParams
        return if (lp is CoordinatorLayout.LayoutParams) {
            lp.behavior is BottomSheetTripleStatesBehavior<*>
        } else false
    }

    protected fun convertDpToPixel(dp: Float, context: Context): Float {
        val densityDpi = context.resources.displayMetrics.densityDpi.toFloat()
        return dp * (densityDpi / DisplayMetrics.DENSITY_DEFAULT)
    }
}