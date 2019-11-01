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

    private var bottomSheet: BottomSheetBehavior<View>? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(layout, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initBottomSheetBehavior()
    }

    private fun initBottomSheetBehavior() {
        val parent = view?.parent
        if (parent is ViewGroup) {
            val params = parent.layoutParams
            if (params is CoordinatorLayout.LayoutParams) {
                val behavior = params.behavior
                if (behavior is BottomSheetBehavior) {
                    bottomSheet = behavior
                }
            }
        }
        bottomSheet?.addBottomSheetCallback(bsCallback)
    }

    private val bsCallback = object : BottomSheetBehavior.BottomSheetCallback() {
        override fun onSlide(bottomSheet: View, slideOffset: Float) {}

        override fun onStateChanged(bottomSheet: View, newState: Int) {
            if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                removeFragment()
            }
        }
    }

    override fun showBottomSheet() {
        setBottomSheetState(BottomSheetBehavior.STATE_EXPANDED)
    }

    override fun hideBottomSheet() {
        setBottomSheetState(BottomSheetBehavior.STATE_HIDDEN)
    }

    private fun setBottomSheetState(state: Int) {
        bottomSheet?.state = state
    }

    override fun onDestroyView() {
        super.onDestroyView()
        bottomSheet?.removeBottomSheetCallback(bsCallback)
    }

    fun removeFragment() {
        requireFragmentManager().beginTransaction().remove(this).commit()
    }
}
