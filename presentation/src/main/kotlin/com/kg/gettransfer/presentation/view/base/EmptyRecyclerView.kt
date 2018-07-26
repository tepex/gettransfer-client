package com.kg.gettransfer.view.base

import android.content.Context

import android.support.annotation.CallSuper
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SimpleItemAnimator

import android.util.AttributeSet
import android.view.View

class EmptyRecyclerView: RecyclerView {
	var emptyView: View? = null
		set(view) {
			field = view
			updateEmptyView()
		}

	private val observer: AdapterDataObserver = object: AdapterDataObserver() {
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
	
	constructor(c: Context): super(c)
	constructor(c: Context, attrs: AttributeSet): super(c, attrs)
	constructor(c: Context, attrs: AttributeSet, defStyle: Int): super(c, attrs, defStyle)
	
	init {
		val animator = itemAnimator
		if(animator is SimpleItemAnimator) animator.supportsChangeAnimations = false
	}

	private fun updateEmptyView() {
		if(emptyView == null) return
		if(getAdapter() == null || getAdapter()?.itemCount == 0) {
			emptyView?.visibility = VISIBLE
			visibility = GONE
		}
		else {
			emptyView?.visibility = GONE
			visibility = VISIBLE
		}
	}
	
	@CallSuper
	override fun setAdapter(newAdapter: RecyclerView.Adapter<*>?) {
		adapter?.unregisterAdapterDataObserver(observer)
		
		newAdapter?.registerAdapterDataObserver(observer)
		super.setAdapter(newAdapter)
		updateEmptyView()
	}
}
