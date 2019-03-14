package com.kg.gettransfer.presentation.ui.behavior

import android.content.Context
import android.support.design.widget.CoordinatorLayout
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout

class BottomCommunicationButtonsLayoutBehavior<V : ViewGroup>(private val mContext: Context, attrs: AttributeSet) : BaseBehavior<V>(mContext, attrs) {

    override fun onDependentViewChanged(parent: CoordinatorLayout, child: V, dependency: View): Boolean {
        if (isBottomSheet(dependency)) {
            val childHeight = (child as LinearLayout).measuredHeight
            child.bottom = when {
                dependency.top <= actionBarHeight + childHeight -> dependency.bottom + dependency.top - actionBarHeight
                else -> screenHeight + childHeight
            }
            child.top = child.bottom - childHeight
            return true
        }
        return false
    }
}
