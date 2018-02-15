package com.kg.gettransfer.views

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.view.View


/**
 * Created by Sinitsyn on 14/02/2018.
 */


class EmptyRecyclerView : RecyclerView {
    var emptyView: View? = null
        set(view) {
            field = view
            initEmptyView()
        }

    private val observer: AdapterDataObserver = object : AdapterDataObserver() {
        override fun onChanged() {
            initEmptyView()
        }

        override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
            initEmptyView()
        }

        override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
            initEmptyView()
        }
    }

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle)

    private fun initEmptyView() {
        if (emptyView != null) {
            val showEmptyView = adapter == null || adapter.itemCount == 0
            emptyView!!.visibility = if (showEmptyView) VISIBLE else GONE
            this@EmptyRecyclerView.visibility = if (showEmptyView) GONE else VISIBLE
        }
    }

    override fun setAdapter(adapter: RecyclerView.Adapter<*>?) {
        val oldAdapter = getAdapter()
        super.setAdapter(adapter)
        oldAdapter?.unregisterAdapterDataObserver(observer)
        adapter?.registerAdapterDataObserver(observer)
    }
}