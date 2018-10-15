package com.kg.gettransfer.presentation.ui

import android.content.Context
import android.view.View
import android.widget.ImageView
import com.bumptech.glide.Glide

internal class UtilsImage{
    companion object {
        fun loadImage(context: Context, imageUrl: String?, imageView: ImageView){
            Glide.with(context).load(imageUrl).into(imageView)
        }

        fun loadImage(view: View, imageUrl: String?, imageView: ImageView){
            Glide.with(view).load(imageUrl).into(imageView)
        }
    }
}