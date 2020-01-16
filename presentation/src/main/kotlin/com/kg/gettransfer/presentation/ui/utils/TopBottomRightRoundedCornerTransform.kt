package com.kg.gettransfer.presentation.ui.utils

import android.graphics.Bitmap

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation

import com.kg.gettransfer.presentation.ui.roundedSquareBitmap
import java.nio.charset.Charset

import java.security.MessageDigest

data class TopBottomRightRoundedCornerTransform(private val dimensionPixelSize: Int) : BitmapTransformation() {

    private val id: String
        get() = javaClass.name

    private val idBytes = id.toByteArray(Charset.forName("UTF-8"))

    override fun transform(pool: BitmapPool, toTransform: Bitmap, outWidth: Int, outHeight: Int) =
        circleCrop(toTransform)

    private fun circleCrop(source: Bitmap) =
        source.roundedSquareBitmap(
            dimensionPixelSize,
            topLeft = false,
            topRight = true,
            bottomRight = true,
            bottomLeft = false)

    override fun updateDiskCacheKey(messageDigest: MessageDigest) {
        messageDigest.update(idBytes)
    }
}
