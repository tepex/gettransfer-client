package com.kg.gettransfer.presentation.ui

import android.support.design.widget.BottomSheetBehavior
import android.support.design.widget.CoordinatorLayout
import android.view.View
import android.view.ViewGroup
import com.arellomobile.mvp.MvpAppCompatFragment

abstract class BaseBottomSheetFragment: MvpAppCompatFragment() {

    protected fun setBottomSheetState(view: View,
                                      state: Int){
        val parent = view.parent
        if(parent is ViewGroup){
            val params = parent.layoutParams
            if(params is CoordinatorLayout.LayoutParams) {
                val behaviour = params.behavior
                if(behaviour is BottomSheetBehavior){
                    behaviour.state = state
                }
            }
        }
    }
}
