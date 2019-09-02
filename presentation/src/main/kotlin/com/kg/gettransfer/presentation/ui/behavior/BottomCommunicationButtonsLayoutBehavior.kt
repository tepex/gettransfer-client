package com.kg.gettransfer.presentation.ui.behavior

import android.content.Context
import androidx.coordinatorlayout.widget.CoordinatorLayout
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout

class BottomCommunicationButtonsLayoutBehavior<V : ViewGroup>(mContext: Context, attrs: AttributeSet) : BaseBehavior<V>(mContext, attrs) {

    override fun layoutDependsOn(parent: CoordinatorLayout, child: V, dependency: View): Boolean {
        return isBottomSheet(dependency)
    }

    override fun onDependentViewChanged(parent: CoordinatorLayout, child: V, dependency: View): Boolean {
        if (isBottomSheet(dependency)) {
            val childHeight = (child as LinearLayout).measuredHeight
            child.bottom = when {
                dependency.top <= actionBarHeight + childHeight -> dependency.bottom + dependency.top + actionBarHeight
                else -> screenHeight + childHeight
            }
            child.top = child.bottom - childHeight
        }
        return false
    }
}
