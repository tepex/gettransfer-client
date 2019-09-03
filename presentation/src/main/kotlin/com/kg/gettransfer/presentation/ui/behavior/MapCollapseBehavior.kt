package com.kg.gettransfer.presentation.ui.behavior

import android.content.Context
import androidx.coordinatorlayout.widget.CoordinatorLayout
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.material.bottomsheet.BottomSheetBehavior

class MapCollapseBehavior(mContext: Context, attrs: AttributeSet): BaseBehavior(mContext, attrs) {

    private var dependencyTopPos = -1
    private var bounds: LatLngBounds? = null

    fun setLatLngBounds (bounds: LatLngBounds) {
        this.bounds = bounds
    }

    override fun layoutDependsOn(parent: CoordinatorLayout, child: View, dependency: View): Boolean {
        return isBottomSheet(dependency)
    }

    override fun onDependentViewChanged(parent: CoordinatorLayout, child: View, dependency: View): Boolean {
        val behavior = (dependency.layoutParams as CoordinatorLayout.LayoutParams).behavior as BottomSheetBehavior

        if (behavior.peekHeight > 0 && dependency.top != dependencyTopPos) {
            val anchorPoint = behavior.halfExpandedRatio.toInt()
            dependencyTopPos = dependency.top
            (child as MapView).getMapAsync { map ->
                map.setPadding(0, 0, 0, when {
                    dependency.top >= anchorPoint -> screenHeight - dependency.top
                    else -> screenHeight - anchorPoint
                })
                bounds?.let {
                    map.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 0))
                }
            }
        }
        return false
    }
}
