package com.kg.gettransfer.view.base


import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import com.google.android.gms.maps.MapView


/**
 * Created by denisvakulenko on 19/04/2018.
 */


class MapViewExt : MapView {
    var userChangedCamera: Boolean = false
    var onUserChangedCamera: Runnable = Runnable { }

    var actionDownXY: Float = 0f

    constructor(c: Context) : super(c)
    constructor(c: Context, attrs: AttributeSet) : super(c, attrs)
    constructor(c: Context, attrs: AttributeSet, defStyle: Int) : super(c, attrs, defStyle)

    override fun dispatchTouchEvent(event: MotionEvent?): Boolean {
        if (event?.action == MotionEvent.ACTION_DOWN) actionDownXY = event.x * 10000 + event.y
        if (event?.action == MotionEvent.ACTION_MOVE && actionDownXY != event.x * 10000 + event.y) {
            if (!userChangedCamera) {
                userChangedCamera = true
                onUserChangedCamera.run()
            }
        }
        return super.dispatchTouchEvent(event)
    }
}