package com.kg.gettransfer.common

import android.view.View

class DebouncingOnClickListener(private val clickAction: (view: View) -> Unit) : View.OnClickListener {

    override fun onClick(v: View) {
        if (enabled) {
            enabled = false
            v.post(enable_again)
            clickAction.invoke(v)
        }
    }

    companion object {
        private var enabled = true
        private val enable_again = Runnable { enabled = true }
    }
}
