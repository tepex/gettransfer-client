package com.kg.gettransfer.presentation.ui.behavior

import android.content.Context
import androidx.coordinatorlayout.widget.CoordinatorLayout
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.kg.gettransfer.R

class CommunicationButtonsLayoutAboveBottomSheetBehavior(private val mContext: Context, attrs: AttributeSet) : BaseBehavior(mContext, attrs) {

    override fun layoutDependsOn(parent: CoordinatorLayout, child: View, dependency: View): Boolean {
        return isBottomSheet(dependency)
    }

    override fun onDependentViewChanged(parent: CoordinatorLayout, child: View, dependency: View): Boolean {
        // anchorId needs to set programmatically(not in xml) otherwise onDependentViewChanged will called all the time
        (child.layoutParams as CoordinatorLayout.LayoutParams).anchorId = R.id.sheetTransferDetails

        val behavior = (dependency.layoutParams as CoordinatorLayout.LayoutParams).behavior as BottomSheetBehavior
        child.bottom = when {
            dependency.top >= behavior.halfExpandedRatio.toInt() -> dependency.top
            else -> behavior.halfExpandedRatio.toInt()
        }
        child.bottom = child.bottom + mContext.resources.getDimension(R.dimen.activity_carrier_trip_details_top_buttons_margin_bottom).toInt()
        child.top = child.bottom - (child as FrameLayout).measuredHeight
        return true
    }
}