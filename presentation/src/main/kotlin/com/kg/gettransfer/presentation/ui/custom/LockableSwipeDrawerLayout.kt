package com.kg.gettransfer.presentation.ui.custom

import android.annotation.SuppressLint
import android.content.Context
import android.support.v4.widget.DrawerLayout
import android.util.AttributeSet
import android.view.Gravity
import android.view.MotionEvent

class LockableSwipeDrawerLayout @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0)
    : DrawerLayout(context, attrs, defStyle) {

  var isSwipeOpenEnabled: Boolean = true

  override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
      if (!isSwipeOpenEnabled && !isDrawerVisible(Gravity.START)) {
          return false
      }
      return super.onInterceptTouchEvent(ev)
  }

  @SuppressLint("ClickableViewAccessibility")
  override fun onTouchEvent(ev: MotionEvent): Boolean {
      if (!isSwipeOpenEnabled && !isDrawerVisible(Gravity.START)) {
          return false
      }
      return super.onTouchEvent(ev)
  }
}