package com.kg.gettransfer.view.base


import android.content.Context
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SimpleItemAnimator
import android.util.AttributeSet
import android.view.View


/**
 * Created by Sinitsyn on 14/02/2018.
 */


class EmptyRecyclerView : RecyclerView {
    var emptyView: View? = null
        set(view) {
            field = view
            updateEmptyView()
        }

    private val observer: AdapterDataObserver = object : AdapterDataObserver() {
        override fun onChanged() {
            updateEmptyView()
        }

        override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
            updateEmptyView()
        }

        override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
            updateEmptyView()
        }

        override fun onItemRangeMoved(fromPosition: Int, toPosition: Int, itemCount: Int) {
            updateEmptyView()
        }

        override fun onItemRangeChanged(positionStart: Int, itemCount: Int) {
            updateEmptyView()
        }

        override fun onItemRangeChanged(positionStart: Int, itemCount: Int, payload: Any?) {
            updateEmptyView()
        }
    }

    constructor(c: Context) : super(c)
    constructor(c: Context, attrs: AttributeSet) : super(c, attrs)
    constructor(c: Context, attrs: AttributeSet, defStyle: Int) : super(c, attrs, defStyle)

    init {
        val animator = itemAnimator
        if (animator is SimpleItemAnimator) {
            animator.supportsChangeAnimations = false
        }
    }

    private fun updateEmptyView() {
        if (emptyView == null) return
        val showEmptyView = adapter == null || adapter.itemCount == 0
        emptyView?.visibility = if (showEmptyView) VISIBLE else GONE
        visibility = if (showEmptyView) GONE else VISIBLE
    }

    override fun setAdapter(newAdapter: RecyclerView.Adapter<*>?) {
        adapter?.unregisterAdapterDataObserver(observer)

        newAdapter?.registerAdapterDataObserver(observer)
        super.setAdapter(newAdapter)

        updateEmptyView()
    }
}