package com.kg.gettransfer.presentation.ui.behavior

import android.content.Context
import androidx.coordinatorlayout.widget.CoordinatorLayout
import android.util.AttributeSet
import android.view.View
import com.kg.gettransfer.R

class TitleBarBehavior(context: Context, attrs: AttributeSet) : BaseBehavior(context, attrs) {

    override fun layoutDependsOn(parent: CoordinatorLayout, child: View, dependency: View): Boolean {
        return isBottomSheet(dependency)
    }

    override fun onDependentViewChanged(parent: CoordinatorLayout, child: View, dependency: View): Boolean {
        // anchorId needs to set programmatically(not in xml) otherwise onDependentViewChanged will called all the time
        (child.layoutParams as CoordinatorLayout.LayoutParams).anchorId = R.id.sheetTransferDetails

        child.bottom = when {
            dependency.top <= actionBarHeight * 2 -> (actionBarHeight * 2) - dependency.top
            else -> 0
        }
        child.top = child.bottom - actionBarHeight
        return true
    }
}
