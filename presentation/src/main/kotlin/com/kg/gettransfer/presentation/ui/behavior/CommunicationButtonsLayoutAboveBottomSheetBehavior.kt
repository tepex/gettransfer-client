package com.kg.gettransfer.presentation.ui.behavior

import android.content.Context
import androidx.coordinatorlayout.widget.CoordinatorLayout
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.kg.gettransfer.R

class CommunicationButtonsLayoutAboveBottomSheetBehavior<V : ViewGroup>(private val mContext: Context, attrs: AttributeSet) : BaseBehavior<V>(mContext, attrs) {

    override fun onDependentViewChanged(parent: CoordinatorLayout, child: V, dependency: View): Boolean {
        if (isBottomSheet(dependency)) {
            val behavior = (dependency.layoutParams as CoordinatorLayout.LayoutParams).behavior as BottomSheetBehavior
            child.bottom = when {
                dependency.top >= behavior.halfExpandedRatio.toInt() -> dependency.top
                else -> behavior.halfExpandedRatio.toInt()
            }
            child.bottom = child.bottom + mContext.resources.getDimension(R.dimen.activity_transfer_details_top_buttons_margin_bottom).toInt()
            child.top = child.bottom - (child as FrameLayout).measuredHeight
            return true
        }
        return false
    }
}