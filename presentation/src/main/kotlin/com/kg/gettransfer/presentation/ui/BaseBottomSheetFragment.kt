package com.kg.gettransfer.presentation.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import androidx.coordinatorlayout.widget.CoordinatorLayout

import com.arellomobile.mvp.MvpAppCompatFragment

import com.google.android.material.bottomsheet.BottomSheetBehavior

abstract class BaseBottomSheetFragment : MvpAppCompatFragment() {

    abstract val layout: Int

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(layout, container, false)

    protected fun setBottomSheetState(view: View, state: Int) {
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
}
