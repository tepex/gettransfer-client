package com.kg.gettransfer.presentation.model

import android.content.Context

import androidx.annotation.StringRes

sealed class TitleModel {
    data class Str(val str: String) : TitleModel()
    data class Id(@StringRes val id: Int) : TitleModel()
    object Empty : TitleModel()
}

fun getTitleString(context: Context, title: TitleModel) = when (title) {
    is TitleModel.Str -> title.str
    is TitleModel.Id  -> context.getString(title.id)
    TitleModel.Empty             -> ""
}
