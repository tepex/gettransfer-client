package com.kg.gettransfer.presentation.ui

import android.os.Bundle
import com.google.android.material.bottomsheet.BottomSheetBehavior
import androidx.coordinatorlayout.widget.CoordinatorLayout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.arellomobile.mvp.MvpAppCompatFragment
import com.kg.gettransfer.presentation.view.BaseBottomSheetView

//import leakcanary.AppWatcher

abstract class BaseBottomSheetFragment : MvpAppCompatFragment(), BaseBottomSheetView {
    abstract val layout: Int

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(layout, container, false)

    protected fun setBottomSheetState(
        view: View,
        state: Int
    ) {
        val parent = view.parent
        if (parent is ViewGroup) {
            val params = parent.layoutParams
            if (params is CoordinatorLayout.LayoutParams) {
                val behaviour = params.behavior
                if (behaviour is BottomSheetBehavior)
                    behaviour.state = state
            }
        }
    }

    override fun showBottomSheet() {
        view?.let { setBottomSheetState(it, BottomSheetBehavior.STATE_EXPANDED) }
    }

    override fun hideBottomSheet() {
        view?.let { setBottomSheetState(it, BottomSheetBehavior.STATE_HIDDEN) }
    }

    override fun onDestroy() {
        super.onDestroy()
//        AppWatcher.objectWatcher.watch(this)
    }
}
