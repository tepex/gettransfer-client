package com.kg.gettransfer.presentation.ui.behavior

import android.app.Activity
import android.content.Context
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout

open class BaseBehavior (context: Context, attrs: AttributeSet) : CoordinatorLayout.Behavior<View>(context, attrs) {
    val screenHeight: Int
    var actionBarHeight: Int = 0

    init {
        val displayMetrics = DisplayMetrics()
        (context as Activity).windowManager
                .defaultDisplay
                .getMetrics(displayMetrics)
        screenHeight = displayMetrics.heightPixels

        val tv = TypedValue()
        actionBarHeight = if (context.getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            TypedValue.complexToDimensionPixelSize(tv.data, context.getResources().displayMetrics)
        } else 0
    }

    protected fun isBottomSheet(view: View): Boolean {
        val lp = view.layoutParams
        return if (lp is CoordinatorLayout.LayoutParams) {
            lp.behavior is BottomSheetTripleStatesBehavior<*>
        } else false
    }
}