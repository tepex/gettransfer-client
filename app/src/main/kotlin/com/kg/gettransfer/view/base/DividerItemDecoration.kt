/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.kg.gettransfer.view.base

import android.content.Context

import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable

import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView

import android.util.Log
import android.view.View
import android.widget.LinearLayout

import com.kg.gettransfer.R

class DividerItemDecoration(context: Context, orientation: Int, leftOffset: Int, rightOffset: Int) : RecyclerView.ItemDecoration() {
	private var mDivider: Drawable? = null
	private var mOrientation: Int = 0

	private val leftOffset: Int
	private val rightOffset: Int

	private val mBounds = Rect()
	
	init {
		mDivider = ContextCompat.getDrawable(context, R.drawable.sh_divider_1dp)
		if(mDivider == null) Log.w(TAG, "@android:attr/listDivider was not set in the theme used for this DividerItemDecoration. Please set that attribute all call setDrawable()")
		this.leftOffset = leftOffset
		this.rightOffset = rightOffset
		setOrientation(orientation)
	}

	/**
     * Sets the orientation for this divider. This should be called if
     * [RecyclerView.LayoutManager] changes orientation.
     *
     * @param orientation [.HORIZONTAL] or [.VERTICAL]
     */
	fun setOrientation(orientation: Int) {
		if (orientation != LinearLayout.HORIZONTAL && 
			orientation != LinearLayout.VERTICAL)
				throw IllegalArgumentException("Invalid orientation. It should be either HORIZONTAL or VERTICAL")
		mOrientation = orientation
	}

	/**
     * Sets the [Drawable] for this divider.
     *
     * @param drawable Drawable that should be used as a divider.
     */

	fun setDrawable(drawable: Drawable) {
		if(drawable == null) throw IllegalArgumentException("Drawable cannot be null.")
		mDivider = drawable
	}
	
	override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
		if(parent.layoutManager == null || mDivider == null) return
			
		if(mOrientation == LinearLayout.VERTICAL) drawVertical(c, parent)
		else drawHorizontal(c, parent)
	}

	private fun drawVertical(canvas: Canvas, parent: RecyclerView) {
		canvas.save()
		val left: Int
		val right: Int
		
		if(parent.clipToPadding) {
			left = parent.paddingLeft
			right = parent.width - parent.paddingRight
			canvas.clipRect(left, parent.paddingTop, right, parent.height - parent.paddingBottom)
		}
		else {
			left = leftOffset
			right = parent.width - rightOffset
		}
		
		val childCount = parent.childCount
		for(i in 0 until childCount - 1) {
			val child = parent.getChildAt(i)
			parent.getDecoratedBoundsWithMargins(child, mBounds)
			val bottom = mBounds.bottom + Math.round(child.translationY)
			val top = bottom - mDivider!!.intrinsicHeight
			mDivider!!.setBounds(left, top, right, bottom)
			mDivider!!.draw(canvas)
		}
		canvas.restore()
	}
	
	private fun drawHorizontal(canvas: Canvas, parent: RecyclerView) {
		canvas.save()
		val top: Int
		val bottom: Int
		
		if(parent.clipToPadding) {
			top = parent.paddingTop
			bottom = parent.height - parent.paddingBottom
			canvas.clipRect(parent.paddingLeft, top, parent.width - parent.paddingRight, bottom)
		}
		else {
			top = leftOffset
			bottom = parent.height - rightOffset
		}

		val childCount = parent.childCount
		for(i in 0 until childCount - 1) {
			val child = parent.getChildAt(i)
			parent.layoutManager!!.getDecoratedBoundsWithMargins(child, mBounds)
			val right = mBounds.right + Math.round(child.translationX)
			val left = right - mDivider!!.intrinsicWidth
			mDivider!!.setBounds(left, top, right, bottom)
			mDivider!!.draw(canvas)
		}
		canvas.restore()
	}
	
	override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
		if(mDivider == null || parent.getChildAdapterPosition(view) == parent.childCount - 1) {
			outRect.set(0, 0, 0, 0)
			return
		}
		
		if(mOrientation == LinearLayout.VERTICAL) outRect.set(0, 0, 0, mDivider!!.intrinsicHeight)
		else outRect.set(0, 0, mDivider!!.intrinsicWidth, 0)
	}

	companion object {
		private val TAG = "DividerItem"
	}
}
