package com.kg.gettransfer.presentation.ui.behavior

import android.content.Context
import android.support.design.widget.CoordinatorLayout
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup

class MapCollapseBehavior<V : ViewGroup>(private val mContext: Context, attrs: AttributeSet): BaseBehavior<V>(mContext, attrs) {

    override fun onDependentViewChanged(parent: CoordinatorLayout, child: V, dependency: View): Boolean {
        if (isBottomSheet(dependency)) {
            val behavior = (dependency.layoutParams as CoordinatorLayout.LayoutParams).behavior as BottomSheetTripleStatesBehavior

            if (behavior.peekHeight > 0) {
                val anchorPoint = behavior.anchorPoint
                if (dependency.top >= screenHeight - anchorPoint) {
                    val dy = dependency.top - parent.height
                    child.translationY = (dy / 2).toFloat()
                    return true
                }
            }
        }
        return false
    }
}
