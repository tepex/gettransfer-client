package com.kg.gettransfer.views

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.support.v4.content.ContextCompat
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory

/**
 * Created by denisvakulenko on 14/02/2018.
 */

class BitmapDescriptorFactory {
    companion object {
        fun fromVector(context: Context, vectorResId: Int): BitmapDescriptor? {
            val vectorDrawable = ContextCompat.getDrawable(context, vectorResId) ?: return null
            vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
            val bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap);
            vectorDrawable.draw(canvas);
            return BitmapDescriptorFactory.fromBitmap(bitmap);
        }
    }
}
