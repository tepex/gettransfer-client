package com.kg.gettransfer.presentation.ui.behavior

import android.content.Context
import android.support.design.widget.CoordinatorLayout
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.LatLngBounds

class MapCollapseBehavior<V : ViewGroup>(private val mContext: Context, attrs: AttributeSet): BaseBehavior<V>(mContext, attrs) {

    private var dependencyTopPos = -1
    private var bounds: LatLngBounds? = null

    fun setLatLngBounds (bounds: LatLngBounds) {
        this.bounds = bounds
    }

    override fun onDependentViewChanged(parent: CoordinatorLayout, child: V, dependency: View): Boolean {
        if (isBottomSheet(dependency)) {
            val behavior = (dependency.layoutParams as CoordinatorLayout.LayoutParams).behavior as BottomSheetTripleStatesBehavior

            if (behavior.peekHeight > 0 && dependency.top != dependencyTopPos) {
                val anchorPoint = behavior.anchorPoint
                dependencyTopPos = dependency.top
                child.layoutParams = (child as MapView).layoutParams.apply {
                    height = when {
                        dependency.top >= anchorPoint -> dependency.top
                        else -> anchorPoint
                    }
                }
                (child as MapView).getMapAsync{ map ->
                    bounds?.let {
                        map.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 0))
                    }
                }
                return true
            }
        }
        return false
    }
}
