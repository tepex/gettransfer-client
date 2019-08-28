package com.kg.gettransfer.presentation.ui.utils

import android.graphics.*

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation
import com.kg.gettransfer.presentation.ui.roundedSquareBitmap

import java.security.MessageDigest

class TopRightRoundedCornerTransform(private val dimensionPixelSize: Int) : BitmapTransformation() {

    val id: String
        get() = javaClass.name

    override fun transform(pool: BitmapPool, toTransform: Bitmap, outWidth: Int, outHeight: Int): Bitmap? {
        return circleCrop(pool, toTransform)
    }

    private fun circleCrop(pool: BitmapPool, source: Bitmap?): Bitmap? {
        if (source == null) return null
        return source.roundedSquareBitmap(dimensionPixelSize, false, true, false, false)
    }

    override fun updateDiskCacheKey(messageDigest: MessageDigest) {
    }
}