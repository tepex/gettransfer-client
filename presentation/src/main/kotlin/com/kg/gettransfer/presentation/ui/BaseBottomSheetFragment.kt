package com.kg.gettransfer.presentation.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import androidx.coordinatorlayout.widget.CoordinatorLayout

import moxy.MvpAppCompatFragment

import com.google.android.material.bottomsheet.BottomSheetBehavior

import com.kg.gettransfer.presentation.view.BaseBottomSheetView

abstract class BaseBottomSheetFragment : MvpAppCompatFragment(), BaseBottomSheetView {
    abstract val layout: Int

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(layout, container, false)

    private fun setBottomSheetState(view: View, state: Int) {
        val parent = view.parent
        if (parent is ViewGroup) {
            val params = parent.layoutParams
            if (params is CoordinatorLayout.LayoutParams) {
                val behaviour = params.behavior
                if (behaviour is BottomSheetBehavior) {
                    behaviour.state = state
                }
            }
        }
    }

    override fun showBottomSheet() {
        view?.let { setBottomSheetState(it, BottomSheetBehavior.STATE_EXPANDED) }
    }

    override fun hideBottomSheet() {
        view?.let { setBottomSheetState(it, BottomSheetBehavior.STATE_HIDDEN) }
    }
}
