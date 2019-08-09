package com.kg.gettransfer.presentation.ui.behavior

import android.content.Context
import androidx.coordinatorlayout.widget.CoordinatorLayout
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup

class TitleBarBehavior<V : ViewGroup>(private val mContext: Context, attrs: AttributeSet) : BaseBehavior<V>(mContext, attrs) {

    override fun onDependentViewChanged(parent: CoordinatorLayout, child: V, dependency: View): Boolean {
        if (isBottomSheet(dependency)) {
            child.bottom = when {
                dependency.top <= actionBarHeight * 2 -> (actionBarHeight * 2) - dependency.top
                else -> 0
            }
            child.top = child.bottom - actionBarHeight
            return true
        }
        return false
    }
}
