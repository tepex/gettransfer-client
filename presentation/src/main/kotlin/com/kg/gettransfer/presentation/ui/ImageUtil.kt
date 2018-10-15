package com.kg.gettransfer.presentation.ui

import com.kg.gettransfer.R

import android.content.Context
import android.widget.ImageView
import com.bumptech.glide.Glide

abstract class ImageUtil{
    companion object {
        fun loadImage(context: Context?, imageUrl: String?, imageView: ImageView){
            Glide.with(context!!).load(context.getString(R.string.api_photo_url, imageUrl)).into(imageView)
        }
    }
}